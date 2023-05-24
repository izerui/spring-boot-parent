package com.yj2025.cost;

import com.yj2025.table.creator.TableTemplate;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class CostJobConfiguration {

    private final static Function<String, String> MADE_FINISHED_TABLE = yearMonth -> String.format("made_finished_%s", yearMonth);

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TableTemplate tableTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Bean("costJob")
    public Job costJob() {
        return jobs.get("costJob")
                .incrementer(new RunIdIncrementer())
                .start(step0(null))
                .next(stepTwo())
                .build();
    }

    // 查询当前月份有过登数的自制件数量
    @Bean("step0")
    @StepScope
    public Step step0(@Value("#{stepExecution}") StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        String entCode = jobParameters.getString("entCode");
        String yearMonth = jobParameters.getString("yearMonth");
        FinishedInventoryTempTableCreator tempTableCreator = new FinishedInventoryTempTableCreator(MADE_FINISHED_TABLE.apply(yearMonth), tableTemplate);
        tempTableCreator.createTable();

        String sql = """
                SELECT
                    r.ent_code,
                    '${yearMonth}' as ym,
                	r.bom_id,
                	(SELECT x.inventory_id from manufacture.production_demand x where x.bom_id = r.bom_id limit 1) as inventory_id,
                	'1' as attribute_code,
                	sum( r.quantity ) as quantity
                FROM
                	manufacture.operate_record r
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

        JdbcBatchItemWriter<Map<String, Object>> batchItemWriter = new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .beanMapped()
                .sql(tempTableCreator.getInsertSQL())
                .build();
        batchItemWriter.afterPropertiesSet();
        return steps.get("findMonthMageInventoriesStep")
                .listener(step0ExecutionListener(null))
                .<Map<String, Object>, Map<String, Object>>chunk(10000)
                .reader(
                        new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                                .dataSource(dataSource)
                                .name("monthMadeInventoriesReader")
                                .sql(sql)
                                .rowMapper(new ColumnMapRowMapper())
                                .build()
                )
                .writer(step0Writer(null))
                .listener(printChunkListener())
                .build();
    }


    @Bean
    @StepScope
    public JdbcBatchItemWriter step0Writer(@Value("#{stepExecution}") StepExecution stepExecution) {
        String tempTableName = MADE_FINISHED_TABLE.apply(stepExecution.getJobParameters().getString("yearMonth"));
        String insertSQL = """
                insert into %s (ent_code, bom_id, inventory_id, attribute_code, ym, quantity) values (:ent_code, :bom_id, :inventory_id, :attribute_code, :ym, :quantity)
                """;
        insertSQL = String.format(insertSQL, tempTableName);
        JdbcBatchItemWriter<Map<String, Object>> batchItemWriter = new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                .dataSource(dataSource)
                .columnMapped()
                .sql(insertSQL)
                .build();
        return batchItemWriter;
    }

    @Bean
    @StepScope
    public StepExecutionListener step0ExecutionListener(@Value("#{stepExecution}") StepExecution stepExecution) {
        final String entCode = stepExecution.getJobParameters().getString("entCode");
        final String yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        final String tableName = MADE_FINISHED_TABLE.apply(yearMonth);
        final String createTableIfNotExistsSQL = String.format(
                """
                        CREATE TABLE if not exists `%s` (
                          `ent_code` varchar(128) NOT NULL COMMENT 'ent_code',
                          `bom_id` varchar(128) NOT NULL COMMENT 'bom_id',
                          `inventory_id` varchar(128) DEFAULT NULL COMMENT 'inventory_id',
                          `ym` bigint DEFAULT NULL COMMENT '年月',
                          `attribute_code` varchar(64) DEFAULT NULL COMMENT '货品属性',
                          `quantity` decimal(24,8) DEFAULT '0.00000000',
                          PRIMARY KEY (`bom_id`)
                        ) ENGINE=InnoDB;
                        """,
                tableName

        );
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                jdbcTemplate.execute(createTableIfNotExistsSQL);
                jdbcTemplate.execute(String.format("delete from %s where ent_code = '%s'", tableName, entCode));
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
                System.out.println("已读取: " + context.getStepContext().getStepExecution().getReadCount());
                System.out.println("已写入: " + context.getStepContext().getStepExecution().getWriteCount());
            }
        };
    }

    @Bean
    public Step stepTwo() {
        return steps.get("stepTwo")
                .tasklet(new MyTaskTwo())
                .build();
    }

}
