package com.yj2025.performance;

import lombok.Data;
import lombok.ToString;

/**
 * @author liuyuhua
 * @date 2022/5/23
 */
@Data
@ToString
public class MyTask implements ClearEvent{

    private int id;
    private int value;

    @Override
    public void clear() {
        this.id = 0;
        this.value = 0;
    }
}
