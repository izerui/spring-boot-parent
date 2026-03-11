package com.yj2025.commons.util;

import org.apache.commons.lang3.StringUtils;

public class CommonStrUtils {

    /**
     * 处理字符串，包括前后空格，以及html的空格
     *
     *   CommonStrUtils.stripStr(null)     = ""
     *   CommonStrUtils.stripStr("")       = ""
     *   CommonStrUtils.stripStr("   ")    = ""
     *   CommonStrUtils.stripStr("abc")    = "abc"
     *   CommonStrUtils.stripStr("  abc")  = "abc"
     *   CommonStrUtils.stripStr("abc  ")  = "abc"
     *   CommonStrUtils.stripStr(" abc ")  = "abc"
     *   CommonStrUtils.stripStr(" ab c ") = "ab c"
     *
     * @param value
     * @return
     */
    public static String stripStr(String value) {
        value = StringUtils.stripToEmpty(value);
        value = value.replace("\u00A0", " ");
        return value;
    }

    /**
     * 转义字符串中的 特殊字符 包括  [`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……&（）"《》——+|{}【】‘；：”“’。，、？]
     *
     * @param str
     * @return
     */
    public static String escapeStr(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……&（）\"《》——+|{}【】‘；：”“’。，、？]";
        for (char c : regEx.toCharArray()) {
            str = StringUtils.replaceAll(str, "\\" + c, "\\\\" + c);
        }
        return str;
    }


    public static void main(String[] args) {
        String str = "(新闻)*?%%*(*.中国}34{45[ddd]12.fd'*&999<中国新闻>下面是  中文  ";
        System.out.println("原字符串  = " + str);
        System.out.println("匹配后的字符串 = " + CommonStrUtils.escapeStr(str));
    }
}
