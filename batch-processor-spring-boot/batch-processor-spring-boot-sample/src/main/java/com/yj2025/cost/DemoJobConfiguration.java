package com.yj2025.cost;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DemoJobConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean("step1")
    @JobScope
    public Step step1(@Value("#{jobParameters['entCode']}") String entCode, @Value("#{jobExecution}") JobExecution jobExecution) {
        System.out.println(entCode);
        System.out.println(jobExecution.getJobParameters().toString());
        return steps.get("readStockCenterDatas")
                .chunk(10000)
                .reader(
                        new JdbcCursorItemReaderBuilder<Map<String, Object>>()
                                .dataSource(dataSource)
                                .name("stockCenterReader")
                                .sql("select * from storehouse.stock_center")
                                .rowMapper(new ColumnMapRowMapper())
                                .build()
                )
//                .writer(
//                        new FlatFileItemWriterBuilder<>()
//                                .name("stockCenterWriter")
//                                .lineSeparator("\n")
//                                .lineAggregator(new PassThroughLineAggregator<>())
//                                .resource(new PathResource("/Users/liuyuhua/Downloads/stock_center.txt"))
//                                .build()
//                )
                .writer(
                        new JsonFileItemWriterBuilder<>()
                                .name("stockCenterWriter")
                                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>(objectMapper))
                                .resource(new PathResource("/Users/liuyuhua/Downloads/stock_center.json"))
                                .shouldDeleteIfExists(true)
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

    @Bean("costJob")
    public Job costJob(@Qualifier("step1") Step step1) {
        return jobs.get("costJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(stepTwo())
                .build();
    }
}
