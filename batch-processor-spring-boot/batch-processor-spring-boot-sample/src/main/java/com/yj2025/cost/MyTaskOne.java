package com.yj2025.cost;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class MyTaskOne implements Tasklet {

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("MyTaskOne start.." + contribution.getStepExecution().getJobParameters().getString("index"));

        // ... your code

        log.info("MyTaskOne done.." + contribution.getStepExecution().getJobParameters().getString("index"));
        return RepeatStatus.FINISHED;
    }
}
