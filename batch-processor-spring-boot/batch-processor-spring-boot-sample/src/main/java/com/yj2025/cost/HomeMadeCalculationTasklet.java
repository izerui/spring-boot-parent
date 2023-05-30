package com.yj2025.cost;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import tech.tablesaw.api.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 自制件-加权平均单价计算
 */
@Slf4j
public class HomeMadeCalculationTasklet implements Tasklet {

    private final StepExecution stepExecution;
    private final DataSource dataSource;
    private final String entCode;
    private final String yearMonth;

    private transient Table madeFinished;
    private transient Table materialUsed;

    public HomeMadeCalculationTasklet(StepExecution stepExecution, DataSource dataSource) {
        this.stepExecution = stepExecution;
        this.dataSource = dataSource;
        this.entCode = stepExecution.getJobParameters().getString("entCode");
        this.yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        initData();
    }

    @SneakyThrows
    private Table loadTable(String sql) {
        @Cleanup Connection conn = DataSourceUtils.getConnection(this.dataSource);
        @Cleanup Statement stmt = conn.createStatement();
        @Cleanup ResultSet resultSet = stmt.executeQuery(sql);
        Table table = Table.read().db(resultSet);
        return table;
    }

    private void initData() {
        String sql0 = String.format("select * from manufacture.year_month_made_finished where ent_code = '%s' and ym = '%s'", entCode, yearMonth);
        this.madeFinished = loadTable(sql0);

        String sql1 = String.format("select * from manufacture.year_month_material_used where ent_code = '%s' and ym = '%s'", entCode, yearMonth);
        this.materialUsed = loadTable(sql1);

        // 加载期初成本表

    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {


        log.info("MyTaskOne start.." + contribution.getStepExecution().getJobParameters().getString("index"));
        materialUsed.categoricalColumn("demand_id");
        // ... your code

        log.info("MyTaskOne done.." + contribution.getStepExecution().getJobParameters().getString("index"));
        return RepeatStatus.CONTINUABLE;
    }
}
