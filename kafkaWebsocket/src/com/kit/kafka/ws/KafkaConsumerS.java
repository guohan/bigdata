package com.kit;


import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

/**
 * 
 * @author guohan
 * @createTime2017-08-31
 * @description kafka消费者多主题消费
 *
 */
public class KafkaConsumerS {
	
	private static Logger logger = Logger.getLogger(KafkaConsumerS.class);

    public static void main(String[] args) {  
        Properties props = new Properties();  
        props.put("bootstrap.servers", "kit-b1:9092");  
        props.put("group.id", "test");  
        props.put("enable.auto.commit", "true");  
        props.put("auto.commit.interval.ms", "1000");  
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);  
        consumer.subscribe(Arrays.asList( "CHUANDA.WFRT_TASK_EXEC_INFO"    , "CHUANDA.FW_KFGDXX", "kf_workorder"));
        while (true) {  
            ConsumerRecords<String, String> records = consumer.poll(10);  
            for (ConsumerRecord<String, String> record : records)  {
            	
              	logger.debug("topic="+record.topic()+"-----offset = "+ record.offset()+ "---key="+record.key()+ "-----value"+ record.value()+ "-----pattition"+record.partition());
                System.out.printf("offset = %d, key = %s, value = %s%n,  partition = %s%n", record.offset(), record.key(), record.value(),record.partition());  
       
            }
       }  
      }  
}
