package com.yj2025.sequence;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class NumberTests {

    @Autowired
    private SequenceService sequenceService;

    @Test
    public void getNumber() {
        sequenceService.consumerNumber("test", PeriodType.DAY.period(DateTime.now()), number -> {
            System.out.println(number);
        });
    }

    @Test
    public void recycleNumber() {
        sequenceService.recycleNumber("test", PeriodType.DAY.period(DateTime.now()), 4);
    }

    @Test
    public void verifyNumber() {
        boolean test = sequenceService.verifyNumber("test", PeriodType.DAY.period(DateTime.now()), 4);
        System.out.println(test);
    }

}
