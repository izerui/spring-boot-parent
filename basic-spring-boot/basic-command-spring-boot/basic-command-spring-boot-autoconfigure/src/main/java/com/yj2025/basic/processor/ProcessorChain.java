package com.yj2025.basic.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessorChain {

    private List<AbstractProcessor> processors = new ArrayList<>();
    private int index = 0;

    /**
     * 添加处理器到处理列表的最后
     *
     * @param processor
     * @return
     */
    public ProcessorChain addProcessor(AbstractProcessor processor) {
        Assert.notNull(processor, "processor 不能为空");
        processors.add(processor);
        return this;
    }

    /**
     * 开始处理
     *
     * @param request
     * @param <T>
     * @throws Exception
     */
    public final <T> void doProcess(T request) {
        if (index == processors.size()) {
            return;
        }
        AbstractProcessor<T> next = processors.get(index);
        index++;
        try {
            if (next.isSupported(request)) {
                next.doProcess(request, this);
            } else {
                this.doProcess(request);
            }
        } catch (Exception ex) {
            if (next.skipException()) {
                log.warn(ex.getMessage());
            } else {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                throw new RuntimeException(ex);
            }
        }

    }

}
