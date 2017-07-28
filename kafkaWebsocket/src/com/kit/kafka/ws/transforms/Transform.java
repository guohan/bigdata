
package com.kit.kafka.ws.transforms;

import  com.kit.kafka.ws.messages.AbstractMessage;
import  com.kit.kafka.ws.messages.BinaryMessage;
import  com.kit.kafka.ws.messages.TextMessage;

import javax.websocket.Session;

public class Transform {
    public void initialize() { }

    public AbstractMessage transform(TextMessage message, final Session session) {
        return message;
    }

    public AbstractMessage transform(BinaryMessage message, final Session session) {
        return message;
    }
}
