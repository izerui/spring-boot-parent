package com.yj2025.cost;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class CostJobConfiguration {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Bean("costJob")
    public Job costJob() {
        return new JobBuilder("costJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step0(null))
                .next(step1(null))
                .next(step2(null))
                .build();
    }

    // 查询当前月份有过登数的自制件数量
    @Bean
    @JobScope
    public Step step0(@Value("#{jobExecution}") JobExecution jobExecution) {
        return new StepBuilder("从生产获取自制件的当月登数数据", jobRepository)
                .listener(step0ExeListener(null))
                .<Map<String, Object>, Map<String, Object>>chunk(10000)
                .reader(step0Reader(null))
                .writer(step0Writer(null))
                .listener(printChunkListener())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> step0Reader(@Value("#{stepExecution}") StepExecution stepExecution) {
        final String entCode = stepExecution.getJobParameters().getString("entCode");
        final String yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        String sql = """
                SELECT
                    r.ent_code, -- 账套编号
                    '${yearMonth}' as ym, -- 当前年月
                  r.demand_id, -- 生产需求id
                  d.inventory_id, -- 货品id
                  d.quantity as production_quantity, -- 总共需生产数量
                  (case when d.level_label is null then '0' else d.level_label end) as level_label, -- 在BOM中的层级标签
                  '1' as attribute_code, -- 货品属性
                  sum( r.quantity ) as quantity -- 本月登数数量
                FROM
                  manufacture.operate_record r
                left join manufacture.production_demand d on r.demand_id = d.record_id
                WHERE
                    r.ent_code = '${entCode}'
                  AND r.workflow = '报工'
                  AND DATE_FORMAT( r.create_time, '%Y%m' ) = '${yearMonth}'
                  AND r.remark = '报工'
                  AND r.quantity > 0
                GROUP BY
                  r.bom_id;
                    """;
        sql = new StringSubstitutor(new HashMap<>() {{
            put("entCode", entCode);
            put("yearMonth", yearMonth);
        }}).replace(sql);
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .name("获取生产本月登数数据")
                .dataSource(dataSource)
                .name("monthMadeInventoriesReader")
                .sql(sql)
                .rowMapper(new ColumnMapRowMapper())
                .build();
    }


    @Bean
    @StepScope
    public JdbcBatchItemWriter<Map<String, Object>> step0Writer(@Value("#{stepExecution}") StepExecution stepExecution) {
        String insertSQL = """
                insert into manufacture.year_month_made_finished (ent_code, demand_id, inventory_id, attribute_code, ym, quantity, production_quantity, level_label) values (:ent_code, :demand_id, :inventory_id, :attribute_code, :ym, :quantity, :production_quantity, :level_label)
                """;
        JdbcBatchItemWriter<Map<String, Object>> batchItemWriter = new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .columnMapped()
                .sql(insertSQL)
                .build();
        return batchItemWriter;
    }

    @Bean
    @StepScope
    public StepExecutionListener step0ExeListener(@Value("#{stepExecution}") StepExecution stepExecution) {
        final String entCode = stepExecution.getJobParameters().getString("entCode");
        final String yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                jdbcTemplate.execute(String.format("delete from manufacture.year_month_made_finished where ent_code = '%s' and ym = '%s'", entCode, yearMonth));
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return ExitStatus.COMPLETED;
            }
        };
    }

    @Bean
    @StepScope
    public ChunkListener printChunkListener() {
        return new ChunkListenerSupport() {
            @Override
            public void afterChunk(ChunkContext context) {
                log.info("{}/{} 已读取: {}", context.getStepContext().getJobName(), context.getStepContext().getStepName(), context.getStepContext().getStepExecution().getReadCount());
                log.info("{}/{} 已写入: {}", context.getStepContext().getJobName(), context.getStepContext().getStepName(), context.getStepContext().getStepExecution().getWriteCount());
            }
        };
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobExecution}") JobExecution jobExecution) {
        return new StepBuilder("获取当月使用的自制件物料的量", jobRepository)
                .listener(step1ExeListener(null))
                .<Map<String, Object>, Map<String, Object>>chunk(10000)
                .reader(step1Reader(null))
                .writer(step1Writer(null))
                .listener(printChunkListener())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public StepExecutionListener step1ExeListener(@Value("#{stepExecution}") StepExecution stepExecution) {
        final String entCode = stepExecution.getJobParameters().getString("entCode");
        final String yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                jdbcTemplate.execute(String.format("delete from manufacture.year_month_material_used where ent_code = '%s' and ym = '%s'", entCode, yearMonth));
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return ExitStatus.COMPLETED;
            }
        };
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Map<String, Object>> step1Reader(@Value("#{stepExecution}") StepExecution stepExecution) {
        final String entCode = stepExecution.getJobParameters().getString("entCode");
        final String yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        String sql = """
                SELECT
                    m.production_demand_id as demand_id, -- 生产需求id
                    m.inventory_id, -- 物料货品id
                    m.attribute_code, -- 物料的货品属性
                    m.production_quantity, -- 物料的需求总量
                    ((d.quantity / d.production_quantity) * m.production_quantity) as quantity, -- 物料的实际消耗数量
                    '${yearMonth}' as ym, -- 当前年月
                    m.ent_code -- 账套编号
                  FROM
                    manufacture.production_demand_material m
                    LEFT JOIN manufacture.year_month_made_finished d ON d.ent_code = m.ent_code
                    AND d.demand_id = m.production_demand_id
                  WHERE
                    d.ent_code = '${entCode}'
                    AND d.ym = '${yearMonth}'
                    AND m.record_status = 1
                    AND m.production_quantity > 0
                    AND m.record_status = 1;
                """;
        sql = new StringSubstitutor(new HashMap<>() {{
            put("entCode", entCode);
            put("yearMonth", yearMonth);
        }}).replace(sql);
        return new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                .name("获取本月登数demand的物料消耗量")
                .dataSource(dataSource)
                .name("monthMadeInventoriesReader")
                .sql(sql)
                .rowMapper(new ColumnMapRowMapper())
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Map<String, Object>> step1Writer(@Value("#{stepExecution}") StepExecution stepExecution) {
        String insertSQL = """
                insert into manufacture.year_month_material_used (ent_code, demand_id, inventory_id, attribute_code, ym, quantity, production_quantity) values (:ent_code, :demand_id, :inventory_id, :attribute_code, :ym, :quantity, :production_quantity)
                """;
        JdbcBatchItemWriter<Map<String, Object>> batchItemWriter = new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .columnMapped()
                .sql(insertSQL)
                .build();
        return batchItemWriter;
    }


    @Bean
    @JobScope
    public Step step2(@Value("#{jobExecution}") JobExecution jobExecution) {
        return new StepBuilder("计算自制件的平均单价", jobRepository)
                .tasklet(homeMadeCalculationTasklet(null, null))
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet homeMadeCalculationTasklet(@Value("#{stepExecution}") StepExecution stepExecution,
                                              JdbcTemplate jdbcTemplate) {
        return new HomeMadeCalculationTasklet(stepExecution, jdbcTemplate);
    }


}
