package com.yj2025.commons.em;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by serv on 2017/6/22.
 */
@Slf4j
public final class UnitEnum {

    //类型
    private String type;
    //名字
    private String name;
    //符号
    private String symbol;
    //小数位数
    private int decimal;

    private UnitEnum(String type, String name, String symbol, int decimal) {
        this.type = type;
        this.name = name;
        this.symbol = symbol;
        this.decimal = decimal;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getDecimal() {
        return decimal;
    }

    /**
     * 按照计量单位的精度进行格式化
     *
     * @param sourceDecimal
     * @return
     */
    public BigDecimal format(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        return formatUp(sourceDecimal);
    }

    /**
     * 向上取整
     *
     * @param sourceDecimal
     * @return
     */
    public BigDecimal formatUp(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        //向上取整
        return sourceDecimal.setScale(decimal, BigDecimal.ROUND_CEILING).stripTrailingZeros();
    }

    /**
     * 向下取整
     *
     * @param sourceDecimal
     * @return
     */
    public BigDecimal formatDown(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        //向上取整
        return sourceDecimal.setScale(decimal, BigDecimal.ROUND_FLOOR).stripTrailingZeros();
    }


    /**
     * 银行家舍入 计算金额 使用该方法
     * <p>
     * 四舍六入五考虑，五后非零就进一，五后皆零看奇偶，五前为偶应舍 去，五前为奇要进一。
     *
     * @param sourceDecimal
     * @return
     */
    public BigDecimal formatEven(BigDecimal sourceDecimal) {
        if (sourceDecimal == null) {
            return null;
        }
        return sourceDecimal.setScale(decimal, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
    }


    /**
     * 扩展第三方单位
     *
     * @param type    计数单位、长度单位、面积单位、时间单位、体积单位、质量单位
     * @param name    个、米...
     * @param symbol  pcs、m...
     * @param decimal 0,2...
     */
    public static void addUnit(String type, String name, String symbol, int decimal) {
        long count = units.stream().filter(unitEnum -> unitEnum.getName().equals(name)).count();
        if (count > 0) {
            return;
        }
        units.add(new UnitEnum(type, name, symbol, decimal));
    }

    /**
     * 根据名称获取计量单位
     *
     * @param unitName
     * @return
     */
    public static UnitEnum instanceOf(String unitName) {
        for (UnitEnum unitEnum : units) {
            //单位名称
            if (StringUtils.equalsIgnoreCase(unitEnum.getName(), unitName)) {
                return unitEnum;
            }
            //单位符号
            if (StringUtils.equalsIgnoreCase(unitEnum.getSymbol(), unitName)) {
                return unitEnum;
            }
        }

        try {
            File file = new File("/data/public/units");
            if (file.exists()) {
                List<String> lines = FileUtils.readLines(file, "UTF-8");
                for (String line : lines) {
                    String[] split = line.split(",");
                    if (split == null || split.length != 4) {
                        throw new RuntimeException(line + " is readed error!");
                    }
                    UnitEnum unitEnum = new UnitEnum(split[0], split[1], split[2], Integer.parseInt(split[3]));
                    units.add(unitEnum);
                    if (unitEnum.getName().equalsIgnoreCase(unitName) || unitEnum.getSymbol().equalsIgnoreCase(unitName)) {
                        return unitEnum;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("/data/public/units " + e.getMessage());
        }

        throw new RuntimeException("计量单位【" + unitName + "】不在系统可用列表");
    }


    public final static List<UnitEnum> units = Lists.newArrayList();

}
