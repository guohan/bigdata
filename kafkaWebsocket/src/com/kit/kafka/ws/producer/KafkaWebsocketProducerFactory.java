package com.kit.kafka.ws.producer;

import  com.kit.kafka.ws.transforms.Transform;

import java.util.Properties;
/**
 * 
 * @author Administrator
 * @CreateTime 2017/06/24
 * @description
 * kafkawebsocket工厂类
 *
 */
public class KafkaWebsocketProducerFactory {
    private final Properties configProps;
    private final Transform inputTransform;
    private KafkaWebsocketProducer producer;

    static public KafkaWebsocketProducerFactory create(Properties configProps, Class inputTransformClass) throws IllegalAccessException, InstantiationException {
        Transform inputTransform = (Transform)inputTransformClass.newInstance();
        inputTransform.initialize();

        return new KafkaWebsocketProducerFactory(configProps, inputTransform);
    }

    private KafkaWebsocketProducerFactory(Properties configProps, Transform inputTransform) {
        this.configProps = configProps;
        this.inputTransform = inputTransform;
    }

    public KafkaWebsocketProducer getProducer() {
        if (producer == null) {
            producer = new KafkaWebsocketProducer(configProps, inputTransform);
            producer.start();
        }
        return producer;
    }
}
