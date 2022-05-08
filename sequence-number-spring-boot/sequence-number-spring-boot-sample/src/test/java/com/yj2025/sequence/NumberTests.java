package com.yj2025.sequence;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

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

    @Test
    public void testAll() {
        String groupId = UUID.randomUUID().toString();
        PeriodType.Period period = PeriodType.DAY.period(DateTime.now());
        System.out.println("groupId: " + groupId);
        System.out.println("开始获取顺序号,顺序获取5次");
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            sequenceService.consumerNumber(groupId, period, number -> {
                System.out.println("第" + finalI + "次获取,number:  " + number);
            });
        }
        sequenceService.recycleNumber(groupId, period, 2);
        System.out.println("回收2");
        sequenceService.recycleNumber(groupId, period, 3);
        System.out.println("回收3");
        sequenceService.recycleNumber(groupId, period, 4);
        System.out.println("回收4");

        System.out.println("提前验证是否可用");
        for (int i = 0; i < 6; i++) {
            Integer vNumber = i + 1;
            boolean b = sequenceService.verifyNumber(groupId, period, vNumber);
            System.out.println(vNumber + " : " + (b ? "可用" : "不可用"));
        }

        System.out.println("开始获取顺序号，一次获取10次");
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            try {
                sequenceService.consumerNumber(groupId, period, number -> {
                    if (finalI == 8) {
                        throw new RuntimeException("故意抛出异常");
                    }
                    System.out.println("第" + finalI + "次获取,number:  " + number);
                });
            } catch (Exception ex) {
                System.out.println("第" + finalI + "次获取时候, 抛出异常，号码自动回收: " + ex.getMessage());
            }
        }

    }

}
