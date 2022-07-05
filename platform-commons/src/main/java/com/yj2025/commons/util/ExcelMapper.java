package com.yj2025.commons.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by serv on 15/9/8.
 */
public class ExcelMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelMapper.class);


    @Deprecated
    private String fileName;
    private InputStream inputStream;
    private File file;
    private byte[] bytes;
    private String sheetName;
    private Set<String> headValidate;
    private int maxRows = 0;
    private int headerRowIndex = 1;
    private boolean headerHandlerInvoke = false;

    /**
     * @param excelFile
     * @throws IOException
     */
    public ExcelMapper(File excelFile) {
        if (excelFile.length() > 5 * 1024 * 1000) {
            throw new RuntimeException("excel文件超过5M大小,请删除不必要的信息后重试");
        }
        this.file = excelFile;
    }

    @Deprecated
    public ExcelMapper(String fileName, byte[] bytes) {
        if (bytes.length > 5 * 1024 * 1000) {
            throw new RuntimeException("excel文件超过5M大小,请删除不必要的信息后重试");
        }
        this.bytes = bytes;
    }

    public ExcelMapper(byte[] bytes) {
        if (bytes.length > 5 * 1024 * 1000) {
            throw new RuntimeException("excel文件超过5M大小,请删除不必要的信息后重试");
        }
        this.bytes = bytes;
    }

    public ExcelMapper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 要读取的sheet名称
     *
     * @param sheetName
     * @return
     */
    public ExcelMapper withSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * head头所在的行索引 从1开始
     *
     * @param headerRowIndex
     * @return
     */
    public ExcelMapper withHeaderRowIndex(int headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    public ExcelMapper withHeadValidate(Set<String> headValidate) {
        this.headValidate = headValidate;
        return this;
    }

    public ExcelMapper maxRows(int maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    protected ExcelReaderSheetBuilder excelReaderSheetBuilder() {
        ExcelReaderBuilder builder = null;
        if (inputStream != null) {
            builder = EasyExcel.read(inputStream);
        } else if (file != null) {
            builder = EasyExcel.read(file);
        } else if (bytes != null) {
            builder = EasyExcel.read(new ByteArrayInputStream(bytes));
        }
        Assert.notNull(builder, "请传入正确的文件信息");
        if (StringUtils.isNotEmpty(sheetName)) {
            return builder.sheet(sheetName).headRowNumber(headerRowIndex);
        } else {
            return builder.sheet().headRowNumber(headerRowIndex);
        }
    }

    /**
     * 开始读取excel文件内容到一个List<Map>
     *
     * @return
     * @throws IOException
     */
    public <T> List<T> read(HeaderFormatter formatter, Convertor<T> convertor) {
        Assert.notNull(convertor, "convertor 转换器不能为空");
        Map<Integer, String> header = new HashMap<>();
        List<LinkedHashMap<Integer, String>> dataList = readDataList(formatter, header);
        if (this.maxRows != 0) {
            Assert.isTrue(dataList.size() <= maxRows, "超过最大允许导入行数" + this.maxRows + "，请精简后再导入！");
        }
        validateHead(header);
        return getResultDataList(convertor, header, dataList);
    }

    private void validateHead(Map<Integer, String> header) {
        if (!headerHandlerInvoke && !CollectionUtils.isEmpty(headValidate)) { // true once invoke
            Long totalCount = 0L;
            StringBuilder sb = new StringBuilder("列名");
            for (String headKey : headValidate) {
                long count = header.values().stream().filter(excelHeadKey -> headKey.equals(excelHeadKey)).count();
                if (count == 0) {
                    sb.append("【").append(headKey).append("】");
                }
                totalCount += count;
            }
            sb.append("不存在，列名从【").append(headerRowIndex).append("】行开始解析，请检查excel");
            Assert.isTrue(totalCount.intValue() == headValidate.size(), sb.toString());
            headerHandlerInvoke = true;
        }
    }

    private List<LinkedHashMap<Integer, String>> readDataList(HeaderFormatter formatter, Map<Integer, String> header) {
        return excelReaderSheetBuilder()
                .registerReadListener(new SyncReadListener() {
                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        headMap.forEach((integer, head) -> {
                            if (formatter != null) {
                                header.put(integer, formatter.formatting(head));
                            } else {
                                header.put(integer, head);
                            }
                        });
                    }
                })
                .doReadSync();
    }

    private <T> List<T> getResultDataList(Convertor<T> convertor, Map<Integer, String> header, List<LinkedHashMap<Integer, String>> dataList) {
        List<T> resultDataList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            LinkedHashMap<Integer, String> row = dataList.get(i);
            LinkedHashMap<String, String> data = new LinkedHashMap<>();
            header.forEach((columnIndex, title) -> {
                data.put(title, row.get(columnIndex));
            });
            if (ExcelDataListener.isNullColumns(data)) {
                continue;
            }
            T convert = convertor.convert(data, i + 1 + headerRowIndex, dataList.size() + headerRowIndex);
            if (convert != null) {
                resultDataList.add(convert);
            }
        }
        return resultDataList;
    }


    /**
     * 分段读取excel文件内容,建议文件过大使用该方法,能有效避免oom的发生<Map>
     */
    public <T> void readPartition(int batchCount, HeaderFormatter formatter, Convertor<T> convertor, Consumer<List<T>> consumer) {
        Assert.notNull(convertor, "convertor 转换器不能为空");
        excelReaderSheetBuilder()
                .registerReadListener(new ExcelDataListener(batchCount, consumer, formatter, convertor, headValidate))
                .doRead();
    }

    public interface HeaderFormatter {
        String formatting(String key);
    }

    public interface Convertor<T> {
        T convert(final LinkedHashMap<String, String> rowData, final int rowNum, final int totalRowNum);

    }

}
