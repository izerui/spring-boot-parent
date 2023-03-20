package com.yj2025.commons.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright (C), 2014-2015, 深圳云集智造系统技术有限公司
 *
 * @Title:
 * @Description :
 * @Author by yandw
 * @date on 2016/8/24
 */
public class DateUtils {
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";


    public static Date getStartTimeOfDay(String date) {
        return new DateTime(parse(date, DATE_FORMAT)).withTimeAtStartOfDay().toDate();
    }

    public static Date getEndTimeOfDay(String date) {
        return new DateTime(parse(date, DATE_FORMAT)).plusDays(1).withTimeAtStartOfDay().toDate();
    }

    /**
     * 将日期按指定格式
     *
     * @param date   日期
     * @param format 格式
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将日期格式转换为yyyy-MM-dd格式的字符串
     *
     * @param date
     * @return
     */
    public static String formatYMD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * 将日期格式转换为yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String formatYMDHMS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdf.format(date);
    }

    /**
     * 将日期格式转换为HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String formatHMS(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }

    /**
     * 按指定格式将日期字符串转成日期ַ
     *
     * @param str    待转换的日期格式字符串
     * @param format 格式
     */
    public static Date parse(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按 yyyy-MM-dd 的格式将字符串转换成日期
     *
     * @param str 需要转换的日期字符串
     */
    public static Date parseYMD(String str) {
        return parse(str, DATE_FORMAT);
    }

    /**
     * 按 yyyy-MM-dd HH:mm:ss 的格式将字符串转换成日期
     *
     * @param str 需要转换的日期字符串
     */
    public static Date parseYMDHMS(String str) {
        return parse(str, DATE_TIME_FORMAT);
    }

    /**
     * 获取当前时期时间 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String curDateTime() {
        return format(new Date(), DATE_TIME_FORMAT);
    }

    /**
     * 获取当前日期 格式：yyyy-MM-dd
     */
    public static String curDate() {
        return format(new Date(), DATE_FORMAT);
    }

    /**
     * 获取当前时间 格式：HH:mm:ss
     */
    public static String curTime() {
        return format(new Date(), TIME_FORMAT);
    }

    /**
     * 获取当前日期含时分秒
     *
     * @return
     */
    public static final Date getTime() {
        return new Date();
    }

    /**
     * 获取当前日期不含时分秒
     *
     * @return
     */
    public static final Date getDate() {
        return LocalDate.now().toDate();
    }


    /**
     * date类型格式化
     *
     * @param dateStr 时间字符串
     * @param format  格式化字符串
     * @return 格式化后的时间
     */
    public static final Date formatDate(String dateStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        try {
            Date date = formatter.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取参数日期所在月份第一天
     *
     * @param date
     * @return
     */
    public static final Date getFirstDay(Date date) {
        return new LocalDate(date).dayOfMonth().withMinimumValue().toDate();
    }

    /**
     * 获取参数日期所在月份最后一天
     *
     * @param date
     * @return
     */
    public static final Date getLastedDay(Date date) {
        return new LocalDate(date).dayOfMonth().withMaximumValue().toDate();
    }

    /**
     * 获得指定日期的年份
     *
     * @param date
     * @return
     */
    public static final Integer getYear(Date date) {
        return new DateTime(date).getYear();
    }

    /**
     * 获得指定日期的月份
     *
     * @param date
     * @return
     */
    public static final Integer getMonth(Date date) {
        return new DateTime(date).getMonthOfYear();
    }

    /**
     * 时间比较，如果两个时间相同，返回真，否则返回假
     *
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean eqCompare(Date date1, Date date2) {
        if (date1.getTime() == date2.getTime()) return true;
        return false;
    }

    /**
     * 时间比较，如果参数date1大于参数date2 ，返回真，否则返回假
     *
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean gtCompare(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) return true;
        return false;
    }

    /**
     * 时间比较，如果参数date1小于参数date2 ，返回真，否则返回假
     *
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean ltCompare(Date date1, Date date2) {
        if (date1.getTime() < date2.getTime()) return true;
        return false;
    }

    /**
     * 天运算
     *
     * @param myDate 开始日期
     * @param dayNum 天数
     * @return Date
     */
    public static Date dayAdd(Date myDate, int dayNum) {
        return new DateTime(myDate).plusDays(dayNum).toDate();
    }

    /**
     * 小时运算
     *
     * @param myDate 开始日期
     * @param hoursNum 小时数
     * @return Date
     */
    public static Date hoursAdd(Date myDate, int hoursNum) {
        return new DateTime(myDate).plusHours(hoursNum).toDate();
    }

    /**
     * 分钟运算
     *
     * @param myDate 开始日期
     * @param num    分钟数
     * @return Date
     */
    public static Date minuteAdd(Date myDate, int num) {
        return new DateTime(myDate).plusMinutes(num).toDate();
    }

    /**
     * 月份运算
     *
     * @param myDate 开始日期
     * @param num    月数
     * @return Date
     */
    public static Date monthAdd(Date myDate, int num) {
        return new DateTime(myDate).plusMonths(num).toDate();
    }

    /**
     * 获取年第几周(每周星期一开始)
     *
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
        return new DateTime(date).getWeekOfWeekyear();
    }

    /**
     * 获取停滞时间
     *
     * @param date
     * @return
     */
    public static String getDeadTime(Date date) {
        long currentTime = System.currentTimeMillis();
        if (null == date || currentTime < date.getTime()) {
            return "0秒";
        } else {
            long time = date.getTime();
            int ss = (int) ((currentTime - time) / 1000);
            if (ss < 60) {
                return ss + "秒";
            } else {
                int mm = ss / 60;
                if (mm < 60) {
                    return mm + "分钟";
                } else {
                    int hh = mm / 60;
                    if (hh < 24) {
                        return hh + "小时";
                    } else {
                        return hh / 24 + "天" + (hh % 24 == 0 ? "" : (hh % 24 + "小时"));
                    }
                }
            }
        }
    }
}