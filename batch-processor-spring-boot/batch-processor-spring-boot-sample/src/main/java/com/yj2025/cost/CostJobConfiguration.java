package com.yj2025.cost;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.table.creator.TableTemplate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.support.ColumnMapItemPreparedStatementSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.util.Map;

@Configuration
public class CostJobConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TableTemplate tableTemplate;

    @Bean("costJob")
    public Job costJob() {
        return jobs.get("costJob")
                .incrementer(new RunIdIncrementer())
                .start(findMonthMadeInventoriesStep(null))
                .next(stepTwo())
                .build();
    }

    // 查询当前月份有过登数的自制件数量
    @Bean("findMonthMageInventoriesStep")
    @JobScope
    public Step findMonthMadeInventoriesStep(@Value("#{jobExecution}") JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String entCode = jobParameters.getString("entCode");
        String yearMonth = jobParameters.getString("yearMonth");
        String tempTableName = String.format("made_invs_%s_%s", entCode, yearMonth);
        FinishedInventoryTempTableCreator tempTableCreator = new FinishedInventoryTempTableCreator(tempTableName, tableTemplate);
        tempTableCreator.createTable();

        String sql = """
                SELECT
                	DATE_FORMAT( r.create_time, '%Y%m' ) as ym,
                	r.bom_id,
                	(SELECT x.inventory_id from manufacture.production_demand x where x.bom_id = r.bom_id limit 1) as inventory_id,
                	'1' as attribute_code,
                	sum( r.quantity ) as quantity
                FROM
                	manufacture.operate_record r
                WHERE
                    r.ent_code = '%s'
                	r.workflow = '报工'
                	AND DATE_FORMAT( r.create_time, '%Y%m' ) = '%s'
                	AND r.remark = '报工'
                	AND r.quantity > 0
                GROUP BY
                	r.bom_id;
                """;

        sql = String.format(sql, entCode, yearMonth);
        return steps.get("findMonthMageInventoriesStep")
                .<Map<String, Object>, Map<String, Object>>chunk(10000)
                .reader(
                        new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                                .dataSource(dataSource)
                                .name("monthMadeInventoriesReader")
                                .sql(sql)
                                .rowMapper(new ColumnMapRowMapper())
                                .build()
                )
                .writer(
                        new JdbcBatchItemWriterBuilder<Map<String, Object>>()
                                .dataSource(dataSource)
                                .itemPreparedStatementSetter(new ColumnMapItemPreparedStatementSetter())
                                .sql(tempTableCreator.getInsertPlaceholderSQL())
                                .build()
                )
                .listener(new ChunkListenerSupport() {
                    @Override
                    public void afterChunk(ChunkContext context) {
                        System.out.println("已读取: " + context.getStepContext().getStepExecution().getReadCount());
                        System.out.println("已写入: " + context.getStepContext().getStepExecution().getWriteCount());
                    }
                })
                .build();
    }

    @Bean
    public Step stepTwo() {
        return steps.get("stepTwo")
                .tasklet(new MyTaskTwo())
                .build();
    }

}
