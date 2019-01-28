package com.mood.messagebus;

import com.mood.base.Message;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    @Qualifier("kafkaProducerProp")
    Producer producer;
    public  void sendMessage(Message messageBody,int partition,String messageKey){
        StringBuilder sb=new StringBuilder();
        ProducerRecord<String,Message> message= new ProducerRecord<>
                ("stream_topic", partition,messageKey,messageBody);
        sb.append("消息内容:"+message);
        producer.send(message,(recordMetadata,e)->{
            if (null == e) {
                String meta = "topic:" + recordMetadata.topic() + ", partition:"
                        + recordMetadata.topic() + ", offset:" + recordMetadata.offset();
                sb.append("消息发送结果:"+meta);
            }
        });
        producer.flush();
        logger.info(sb.toString());
    }
}
