package com.yj2025.sequence.storage;

import com.google.common.collect.Maps;
import com.yj2025.lock.Lock;
import com.yj2025.sequence.PeriodType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class MysqlNumberStorage implements NumberStorage {

    private Lock lock;
    private PlatformTransactionManager transactionManager;
    private JdbcTemplate jdbcTemplate;

    public MysqlNumberStorage(PlatformTransactionManager transactionManager, Lock lock, JdbcTemplate jdbcTemplate) {
        this.transactionManager = transactionManager;
        this.lock = lock;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer getNumber(String groupId, PeriodType.Period period) {
        Map<String, Integer> valueMap = Maps.newHashMap();
        lock.execute("sequence_number" + groupId, 60, () -> {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.execute(status -> {
                Integer integer = 1;
                //优先使用已回收的,回收的用完后取最大值+1 生成一个新的，如果最大值的取不到 就创建一个从1开始的
//                List query = jdbcTemplate.query("select id,group_id,reverse from mysql_sequence_number", new BeanPropertyRowMapper(MysqlSequenceVo.class));
                List<SequenceNoEntity> reversList = jdbcTemplate.query("select seq_num as seqNum from mysql_sequence_number force index(index_3) where group_id='" + groupId + "' and reverse = 1 order by seq_num asc limit 1", new BeanPropertyRowMapper(SequenceNoEntity.class));
                if (CollectionUtils.isEmpty(reversList)) {
                    List<SequenceNoEntity> maxSeqNum = jdbcTemplate.query("select seq_num as seqNum from mysql_sequence_number force index(index_3) where group_id='" + groupId + "' and reverse = 0", new BeanPropertyRowMapper(SequenceNoEntity.class));
                    if (CollectionUtils.isEmpty(maxSeqNum)) {
                        //如果没有，就从1开始生存一个
                        jdbcTemplate.execute(" insert into mysql_sequence_number(seq_num,group_id,reverse,create_time,update_time) value (" + integer + ",'" + groupId + "',0,now(),now())");
                    } else {
                        //如果已存在，就把已有的+1
                        Integer seqNum = maxSeqNum.get(0).getSeqNum();
                        integer = seqNum + 1;
                        final Integer fInteger = integer;
                        int updateCount = jdbcTemplate.execute(new StatementCallback<Integer>() {
                            @Override
                            public Integer doInStatement(Statement stmt) throws SQLException {
                                String sql = (" update mysql_sequence_number set seq_num = '" + fInteger + "' ,update_time = now() where group_id = '" + groupId + "' and seq_num = " + seqNum);
                                return stmt.executeUpdate(sql);
                            }
                        });
                        Assert.isTrue(updateCount == 1, "生成新的序列号失败！！！【1】");
                    }
                } else {
                    //删除已回收的，表示回收的已经被使用了。
                    final Integer fInteger = reversList.get(0).getSeqNum();
                    int updateCount = jdbcTemplate.execute(new StatementCallback<Integer>() {
                        @Override
                        public Integer doInStatement(Statement stmt) throws SQLException {
                            String sql = (" delete from mysql_sequence_number where group_id = '" + groupId + "' and seq_num = " + fInteger);
                            return stmt.executeUpdate(sql);
                        }
                    });
                    Assert.isTrue(updateCount == 1, "使用回收的序列号失败！！！");
                }
                valueMap.put("sequence_number", integer);
                return true;
            });
        });

        Integer integer = valueMap.get("sequence_number");
        if (integer == null) {
            throw new RuntimeException("序列号生产失败！！！【2】");
        }
        return integer;
    }

    @Override
    public void recycleNumber(String groupId, PeriodType.Period period, final Integer number) {
        Integer seqNum = jdbcTemplate.queryForObject("select seq_num from mysql_sequence_number where group_id='" + groupId + "' and reverse = 0", Integer.class);
        if (number > seqNum) {
            throw new RuntimeException("回收的序列号不存在！！！");
        }

        int updateCount = jdbcTemplate.execute(new StatementCallback<Integer>() {
            @Override
            public Integer doInStatement(Statement stmt) throws SQLException {
                String sql = " insert into mysql_sequence_number(seq_num,group_id,reverse,create_time,update_time) value (" + number + ",'" + groupId + "',1,now(),now())";
                return stmt.executeUpdate(sql);
            }
        });
        Assert.isTrue(updateCount == 1, "回收序列号失败！！！");
    }

    @Override
    public boolean verifyValidNumber(String groupId, PeriodType.Period period, Integer number) {
        Integer seqNum = jdbcTemplate.queryForObject("select seq_num from mysql_sequence_number where group_id='" + groupId + "' and reverse = 0", Integer.class);
        if (number <= seqNum) {
            return true;
        }
        return false;
    }
}
