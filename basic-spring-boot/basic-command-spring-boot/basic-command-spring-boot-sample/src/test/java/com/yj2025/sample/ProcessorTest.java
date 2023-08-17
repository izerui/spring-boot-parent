package com.yj2025.sample;

import com.yj2025.basic.processor.AbstractProcessor;
import com.yj2025.basic.processor.ProcessorChain;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ProcessorTest {

    @Test
    public void test01() {
        ProcessorChain chain = new ProcessorChain();
        chain.addProcessor(new AbstractProcessor() {
            @Override
            protected boolean process(Object request, ProcessorChain chain) throws Exception {
                System.out.println("1");
                return true;
            }
        });
        chain.addProcessor(new AbstractProcessor() {
            @Override
            protected boolean process(Object request, ProcessorChain chain) throws Exception {
                System.out.println("2");
                return true;
            }
        });
        chain.doProcess(new HashMap<>(){{
            put("a", "1");
        }});
    }
}
