package com.mood.messagebus;

import com.mood.base.Message;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Value("${kafka.key.serializer}")
    private String keySerializer;
    @Value("${kafka.value.serializer}")
    private String valueSerializer;
    @Value("${kafka.producer.bootstrap.servers}")
    private String producerServer;
    @Value("${kafka.producer.acks}")
    private String acks;
    @Value("${kafka.producer.retries}")
    private String retries;
    @Value("${kafka.producer.batch.size}")
    private String batchSize;
    @Value("${kafka.producer.linger.ms}")
    private String lingerMs;
    @Value("${kafka.producer.buffer.memory}")
    private String bufferMemory;
    @Value("${kafka.consumer.bootstrap.servers}")
    private String consumerServer;
    @Value("${kafka.consumer.group.id}")
    private String groupId;
    @Value("${kafka.consumer.enable.auto.commit}")
    private String autoCommit;
    @Value("${kafka.consumer.auto.commit.interval.ms}")
    private String autoCommitInterval;
    @Value("${kafka.key.deserializer}")
    private String keyDeserializer;
    @Value("${kafka.value.deserializer}")
    private String valueDeserializer;
    @Bean(name="kafkaProducerProp")
    public Properties createKafkaProducerProp(){
        Properties props = new Properties();
        props.put("bootstrap.servers", producerServer);
        props.put("acks", acks);
        props.put("retries",retries);
        props.put("batch.size", batchSize);
        props.put("linger.ms", lingerMs);
        props.put("buffer.memory", bufferMemory);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);
        return props;
    }
    @Bean(name="kafkaConsumerProp")
    public Properties createKafkaConsumerProp(){
        Properties props = new Properties();
        props.put("bootstrap.servers", consumerServer);
        props.put("group.id", groupId);
        props.put("enable.auto.commit",autoCommit);
        props.put("auto.commit.interval.ms", autoCommitInterval);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        return props;
    }
    @Bean(name = "kafkaProducer")
    public Producer createKafkaProducer(@Qualifier("kafkaProducerProp") Properties props){
        Producer<String, Message> producer = new KafkaProducer<>(props);
        return producer;
    }
    @Bean(name = "kafkaConsumer")
    public KafkaConsumer createKafkaConsumer(@Qualifier("kafkaConsumerProp") Properties props){
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
}
