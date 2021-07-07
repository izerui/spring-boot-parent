package com.yj2025.nacos.simple.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class SimpleController {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/count")
    public Integer count() {
        int count = atomicInteger.getAndAdd(1);
        log.info(count + "");
        return count;
    }


}
