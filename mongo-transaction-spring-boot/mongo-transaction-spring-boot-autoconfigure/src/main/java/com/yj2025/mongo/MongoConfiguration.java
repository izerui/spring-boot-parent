package com.yj2025.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class MongoConfiguration {

    /**
     * MongoDB 4.4 和对集合创建的支持
     * 这是最近添加到 MongoDB 的功能之一。MongoDB 4.4 及更高版本现在支持隐式创建集合。您现在可以在事务范围内创建索引。
     *
     * MongoDB 4.2 及更早版本 – 不支持创建集合
     * 对于 MongoDB 4.2 和更早版本，不允许创建或删除集合或索引，  因为它们会影响数据库目录。
     *
     * 如果需要，您可以在任何事务之前执行集合创建。
     * @param factory
     * @return
     */
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }

}
