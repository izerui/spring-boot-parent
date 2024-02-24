package com.yj2025.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj2025.customizer.bean.BeanDefinitionRegistryCustomizer;
import com.yj2025.kafka.impl.MessageProducerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.TransactionManager;

/**
 * # spring.kafka.listener.ack-mode=record
 * <pre>
 *     MANUAL	            poll()拉取一批消息，处理完业务后，手动调用Acknowledgment.acknowledge()先将offset存放到map本地缓存，在下一次poll之前从缓存拿出来批量提交
 *     MANUAL_IMMEDIATE	    每处理完业务手动调用Acknowledgment.acknowledge()后立即提交
 *     RECORD	            当每一条记录被消费者监听器（ListenerConsumer）处理之后提交
 *     BATCH	            当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后提交
 *     TIME	                当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，距离上次提交时间大于TIME时提交
 *     COUNT	            当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，被处理record数量大于等于COUNT时提交
 *     COUNT_TIME	        TIME或COUNT满足其中一个时提交
 * </pre>
 */

/**
 * @author liuyuhua
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("${spring.application.name:null}")
    private String applicationName;

    @Bean
    public RecordMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new StringJsonMessageConverter(objectMapper);
    }


    @Bean
    public MessageProducer messageProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        return new MessageProducerImpl(kafkaTemplate, kafkaProperties, applicationName);
    }

    /**
     * 无需配置声明，设置kafka自身事务管理器的id前缀
     * @return
     */
    @Bean
    public DefaultKafkaProducerFactoryCustomizer factoryCustomizer() {
        return producerFactory -> {
            producerFactory.setTransactionIdPrefix("kafka-tx-");
        };
    }

    /**
     * 如果没有jdbc事务再创建自身kafka事务管理器
     * @param producerFactory
     * @return
     */
    @Bean
	@ConditionalOnMissingBean(TransactionManager.class)
	public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
		return new KafkaTransactionManager<>(producerFactory);
	}

    /**
     * 补偿机制： 防止重复创建事务管理器(可选)
     * 如果已经存在jdbc的事务管理器，则移除掉kafka自动创建的事务管理器
     * {@link #kafkaTransactionManager(ProducerFactory)}
     */
    @Bean
    public BeanDefinitionRegistryCustomizer kafkaTransactionManagerCustomizer() {
        return (registry, applicationContext) -> {
            if (registry.isBeanNameInUse("transactionManager")) {
                if (registry.isBeanNameInUse("kafkaTransactionManager")) {
                    registry.removeBeanDefinition("kafkaTransactionManager");
                }
            }
        };
    }


//    @Bean
//    public DefaultKafkaConsumerFactoryCustomizer consumerFactoryCustomizer() {
//        return consumerFactory -> {
//            consumerFactory.addListener(new ConsumerContextHolder());
//        };
//    }


}
