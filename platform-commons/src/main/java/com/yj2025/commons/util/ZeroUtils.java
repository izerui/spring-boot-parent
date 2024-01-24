package com.yj2025.commons.util;

import java.text.NumberFormat;

/**
 * Created by serv on 2014/12/23.
 */
public class ZeroUtils {
    /**
     * 补零操作
     *
     * @param num    要补零的整数
     * @param length 位数
     * @return str
     */
    public static String returnAddedZero(int num, int length) {
        if (length == 0) {
            return String.valueOf(num);
        }
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(length);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(length);
        //输出测试语句
        return nf.format(num);
    }

    /**
     * 补零操作
     *
     * @param str    要补零的字符
     * @param length 位数
     * @return str
     */
    public static String returnAddedZero(String str, int length) {
        if (length == 0) {
            return str;
        }
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }
}
