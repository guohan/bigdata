
package com.kit.kafka.ws.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import  com.kit.kafka.ws.messages.AbstractMessage;
import  com.kit.kafka.ws.messages.BinaryMessage;
import  com.kit.kafka.ws.messages.TextMessage;
import  com.kit.kafka.ws.transforms.Transform;

import javax.websocket.Session;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author guohan
 * @createTime 2017/06/24
 * @desc kafka websocket生产
 *
 */
public class KafkaWebsocketProducer {
    private static Logger LOG = LoggerFactory.getLogger(KafkaWebsocketProducer.class);

    private Map<String, Object> producerConfig;
    private KafkaProducer producer;
    private Transform inputTransform;

    @SuppressWarnings("unchecked")
    public KafkaWebsocketProducer(Properties configProps) {
        this.producerConfig = new HashMap<String, Object>((Map)configProps);
    }

    @SuppressWarnings("unchecked")
    public KafkaWebsocketProducer(Properties configProps, Transform inputTransform) {
        this.producerConfig = new HashMap<String, Object>((Map)configProps);
        this.inputTransform = inputTransform;
    }

    @SuppressWarnings("unchecked")
    public void start() {
        if (producer == null) {
            producer = new KafkaProducer(producerConfig, new StringSerializer(), new ByteArraySerializer());
        }
    }

    public void stop() {
        producer.close();
        producer = null;
    }

    /**
     * 发送消息
     * @param message
     */
    private void send(final AbstractMessage message) {
        if(!message.isDiscard()) {
            if (message.isKeyed()) {
                send(message.getTopic(), message.getKey(), message.getMessageBytes());
            } else {
                send(message.getTopic(), message.getMessageBytes());
            }
        }
    }

    public void send(final BinaryMessage message, final Session session) {
        send(inputTransform.transform(message, session));
    }

    public void send(final TextMessage message, final Session session) {
        send(inputTransform.transform(message, session));
    }

    @SuppressWarnings("unchecked")
    public void send(String topic, byte[] message) {
        final ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, message);
        producer.send(record);
    }

    @SuppressWarnings("unchecked")
    public void send(String topic, String key, byte[] message) {
        final ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, message);
        producer.send(record);
    }
}
