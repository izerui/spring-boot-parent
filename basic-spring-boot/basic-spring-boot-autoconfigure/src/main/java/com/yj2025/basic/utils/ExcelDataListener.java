package com.yj2025.basic.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

class ExcelDataListener<T> extends AnalysisEventListener<LinkedHashMap<Integer, String>> {

    // 每隔 batchCount 条存开始触发业务回调，然后清理 datas ，方便内存回收.
    private int batchCount;
    private Map<Integer, String> _heads;
    // 存储每批读取的数组
    private List<T> _datas = new ArrayList<>();
    // 业务回调
    private Consumer<List<T>> consumer;
    private ExcelMapper.HeaderFormatter formatter;
    private ExcelMapper.Convertor<T> convertor;
    private Set<String> headValidate;
    private boolean headerHandlerInvoke = false;

    public ExcelDataListener(int batchCount, Consumer<List<T>> consumer, ExcelMapper.HeaderFormatter formatter, ExcelMapper.Convertor<T> convertor, Set<String> headValidate) {
        this.batchCount = batchCount;
        this.consumer = consumer;
        this.formatter = formatter;
        this.convertor = convertor;
        this.headValidate = headValidate;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        Assert.state(headMap.size() <= 1000, "excel的空列太多，请检查文文件");
        if (this._heads != null) {
            this._heads.clear();
        } else {
            this._heads = new LinkedHashMap<>();
        }
        headMap.forEach((integer, head) -> {
            if (formatter != null) {
                this._heads.put(integer, formatter.formatting(head));
            } else {
                this._heads.put(integer, head);
            }
        });
    }

    @Override
    public void invoke(LinkedHashMap<Integer, String> row, AnalysisContext analysisContext) {
        Assert.notNull(this._heads, "未获取到标题行,请指定excel文件的标题行索引,从0开始.");
        validateHead();
        LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
        this._heads.forEach((columnIndex, head) -> {
            rowMap.put(head, row.get(columnIndex));
        });
        if (isNullColumns(rowMap)) {
            return;
        }
        T convert = this.convertor.convert(rowMap, analysisContext.readRowHolder().getRowIndex() + 1, analysisContext.readSheetHolder().getApproximateTotalRowNumber());
        if (convert != null) {
            this._datas.add(convert);
        }
        if (this._datas.size() >= batchCount) {
            this.consumer.accept(this._datas);
            this._datas.clear();
        }
    }

    private void validateHead() {
        if (!headerHandlerInvoke && !CollectionUtils.isEmpty(headValidate)) { // true once invoke
            Long totalCount = 0L;
            StringBuilder sb = new StringBuilder("列名");
            for (String headKey : headValidate) {
                long count = _heads.values().stream().filter(excelHeadKey -> headKey.equals(excelHeadKey)).count();
                if (count == 0) {
                    sb.append("【").append(headKey).append("】");
                }
                totalCount += count;
            }
            sb.append("不存在，请检查excel");
            Assert.isTrue(totalCount.intValue() == headValidate.size(), sb.toString());
            headerHandlerInvoke = true;
        }
    }

    /**
     * 如果只有序号列，其他列全部为空则不再返回数据给业务
     *
     * @param rowMap
     * @return
     */
    public static boolean isNullColumns(LinkedHashMap<String, String> rowMap) {
        long count = rowMap.keySet().stream().filter(key -> !key.equals("序号")).count();
        AtomicLong atomicCount = new AtomicLong(0);
        rowMap.forEach((key, val) -> {
            if (!key.equals("序号") && null == val) {
                atomicCount.getAndAdd(1);
            }
        });
        if (count == atomicCount.get()) {
            return true;
        }
        return false;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!this._datas.isEmpty()) {
            this.consumer.accept(this._datas);
        }
        this._datas.clear();
        if (this._heads != null) {
            this._heads.clear();
        }
    }
}
