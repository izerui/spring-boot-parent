package com.yj2025.cost;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.Assert;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自制件-加权平均单价计算 step scope
 * 成本计算逻辑:
 * 1. (初始化)当月发生登数的自制件列表
 * 2. (初始化)当月发生登数的自制件所使用的物料列表
 * 3. (异常)当物料表中未补全单价的货，在待计算自制件列表中也不存在的情况下，则抛出异常，指示用户手动填写月初单价
 * 4. (计算)循环自制件-未计算成本的,通过其物料总成本之和(通过单价列与用量)补全到自制件列表行,如果碰到物料单价缺失的忽略，继续下一个自制件
 * 5. (计算)将相同的自制件均已计算出单价再跟期初的单价做个加权平均后的补全到物料列表单价列中
 * 6. (重复)程序将继续重复3、4。直到所有自制件的成本均已经计算出来
 */
@Slf4j
public class HomeMadeCalculationTasklet implements Tasklet {

    private final StepExecution stepExecution;
    private final JdbcTemplate jdbcTemplate;
    private final String entCode;
    private final String yearMonth;

    // 自制件待计算列表
    private transient Table homeMades;
    // 自制件的物料列表
    private transient Table materials;

    public HomeMadeCalculationTasklet(StepExecution stepExecution, JdbcTemplate jdbcTemplate) {
        this.stepExecution = stepExecution;
        this.jdbcTemplate = jdbcTemplate;
        this.entCode = stepExecution.getJobParameters().getString("entCode");
        this.yearMonth = stepExecution.getJobParameters().getString("yearMonth");
        initData();
        validateData();
    }

    // TODO 获取期初价格信息
    private List<InventoryPriceVO> getInitPrice(String entCode, List<String> inventoryIds) {
        return inventoryIds.stream().map(s -> {
            InventoryPriceVO priceVO = new InventoryPriceVO();
            priceVO.setInventoryId(s);
            priceVO.setEntCode(entCode);
            priceVO.setQuantity(new BigDecimal(RandomUtils.nextDouble(1.0, 50.0)));
            priceVO.setTotalAmount(new BigDecimal(RandomUtils.nextDouble(1.0, 500.0)));
            Assert.notNull(priceVO.getPrice(), "单价不能为NULL");
            return priceVO;
        }).collect(Collectors.toList());
    }

    private void initData() {
        initHomeMade();
        initMaterials();
    }

    /**
     * 初始化待计算成本的自制件列表
     */
    private void initHomeMade() {
        // 获取当月登数的自制件列表信息
        String sql0 = String.format("select * from manufacture.year_month_made_finished where ent_code = '%s' and ym = '%s' order by level_label desc", entCode, yearMonth);
        homeMades = jdbcTemplate.query(sql0, (ResultSetExtractor<Table>) rs -> Table.read().db(rs));
        // 自制件列表信息增加待计算成本列
        homeMades.addColumns(DoubleColumn.create("amount"));
        // 自制件列表信息增加待计算单价列
        homeMades.addColumns(DoubleColumn.create("price"));
        Assert.state(homeMades.containsColumn("inventory_id"), "自制件列表必须有[inventory_id]列");
        Assert.state(homeMades.containsColumn("quantity"), "自制件列表必须有[quantity]列");
        Assert.state(homeMades.containsColumn("demand_id"), "物料列表必须有[demand_id]列");
    }

    /**
     * 初始化自制件对应的当月物料领用列表信息
     */
    private void initMaterials() {
        // 获取自制件对应的当月物料领用列表信息
        String sql1 = String.format("select * from manufacture.year_month_material_used where ent_code = '%s' and ym = '%s'", entCode, yearMonth);
        materials = jdbcTemplate.query(sql1, (ResultSetExtractor<Table>) rs -> Table.read().db(rs));
        // 增加期初单价列
        materials.addColumns(DoubleColumn.create("init_price"));
        // 增加最终单价列，最终计算成本使用本列
        materials.addColumns(DoubleColumn.create("price"));
        // 本月发生登数的自制件货品ID集合
        var madeInventoryIdSets = homeMades.stringColumn("inventory_id").asSet();
        // 通过月初成本补全物料领用列表里面的期初, 部分自制件可能没有，需要计算后再补全
        materials.forEach(row -> {
            String inventoryId = row.getString("inventory_id");
            String attributeCode = row.getString("attribute_code");
            // TODO begin: 这里应该是使用物料表的一个字段, 本示例: 模拟数据
            Double initPrice = RandomUtils.nextDouble(0.0, 500.0);
            // TODO :end
            if (attributeCode != "1") {
                row.setDouble("init_price", initPrice);
                row.setDouble("price", initPrice);
            } else {
                row.setDouble("init_price", 0.0);
                // 本月未发生登数的自制物料直接使用期初单价
                if (!madeInventoryIdSets.contains(inventoryId)) {
                    row.setDouble("price", initPrice);
                }
            }
        });
        Assert.state(homeMades.containsColumn("inventory_id"), "物料列表必须有[inventory_id]列");
        Assert.state(homeMades.containsColumn("attribute_code"), "物料列表必须有[attribute_code]列");
        Assert.state(homeMades.containsColumn("quantity"), "物料列表必须有[quantity]列");
        Assert.state(homeMades.containsColumn("init_price"), "物料列表必须有[init_price]列");
        Assert.state(homeMades.containsColumn("demand_id"), "物料列表必须有[demand_id]列");
    }

    /**
     * 如果自制件没有期初单价，并且本月未发生登数, 则抛出异常
     */
    private void validateData() {
        Set<String> unPricedInventoryIds = materials
                .stringColumn("inventory_id")
                .where(materials.stringColumn("attribute_code").isEqualTo("1"))
                .where(materials.doubleColumn("init_price").isMissing())
                .asSet();
        StringColumn column = homeMades.stringColumn("inventory_id");
        column = column.where(column.isIn(unPricedInventoryIds));
        Sets.SetView<String> difference = Sets.difference(unPricedInventoryIds, column.asSet());
        Assert.state(difference.size() == 0, "物料表中没有单价的自制件，在本月中未发现生产登数,请补充期初单价。相关货品ID:" + difference.toString());
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 循环自制件的每行进行成本计算并得出其成本
        homeMades.forEach(row -> {
            // 如果当前自制件未计算成本
            if (row.isMissing("amount")) {
                String demandId = row.getString("demand_id");
                // 构造新的当前自制件的物料列表
                Table materialTable = materials.where(materials.stringColumn("demand_id").isEqualTo(demandId));
                // 如果当前自制件的物料中有未完善单价列的物料
                if (materialTable.doubleColumn("price").countMissing() > 0) {
                    return;
                }
                DoubleColumn quantityCol = materialTable.doubleColumn("quantity");
                DoubleColumn priceCol = materialTable.doubleColumn("price");
                DoubleColumn amoutCol = quantityCol.multiply(priceCol);
                row.setDouble("amount", amoutCol.sum());
            }
        });

        // 针对自制件列表按货品id分组，将所有算过成本的算出单价
        Table inventoryTable = homeMades
                .summarize("inventory_id", "amount", "quantity", AggregateFunctions.countNonMissing, AggregateFunctions.sum)
                .by("inventory_id");
        // 找到已经计算完成本的货品列表
        Table amountCompletedTable = inventoryTable
                .where(inventoryTable.doubleColumn("Count [amount]").isEqualTo(inventoryTable.doubleColumn("Count [inventory_id]")));
        // 把已计算完的货品列表计算出单价
        DoubleColumn priceColumn = amountCompletedTable.doubleColumn("Sum [amount]")
                .divide(amountCompletedTable.doubleColumn("Sum [quantity]"))
                .setName("price");
        amountCompletedTable.addColumns(priceColumn);
        Map<String, Double> invPriceMap = new HashMap<>();
        amountCompletedTable.forEach(row -> {
            invPriceMap.put(row.getString("inventory_id"), row.getDouble("price"));
        });

        // 将计算出单价的货品单价信息回写到自制件列表
        homeMades.forEach(row -> {
            String inventoryId = row.getString("inventory_id");
            if (invPriceMap.containsKey(inventoryId)) {
                row.setDouble("price", invPriceMap.get(inventoryId));
            }
        });

        // 将单价跟物料表的期初价进行加权平均后，写入price列
        materials.forEach(row -> {
            String inventoryId = row.getString("inventory_id");
            Double initPrice = row.getDouble("init_price");
            if (invPriceMap.containsKey(inventoryId)) {
                row.setDouble("price", (invPriceMap.get(inventoryId) + initPrice) / 2);
            }
        });

        // 如果自制件列表有未算完成本的行，则继续循环，否则将结果返回
        if (homeMades.column("amount").countMissing() > 0) {
            return RepeatStatus.CONTINUABLE;
        } else {
            System.out.println(homeMades.print());
            // TODO 写入结果
            return RepeatStatus.FINISHED;
        }
    }
}
