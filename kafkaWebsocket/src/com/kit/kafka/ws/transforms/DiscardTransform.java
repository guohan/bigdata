package com.kit.kafka.ws.transforms;

import com.kit.kafka.ws.messages.AbstractMessage;
import com.kit.kafka.ws.messages.BinaryMessage;
import com.kit.kafka.ws.messages.TextMessage;

import javax.websocket.Session;

public class DiscardTransform extends Transform {
    public AbstractMessage transform(TextMessage message, final Session session) {
        message.setDiscard(true);
        return message;
    }

    public AbstractMessage transform(BinaryMessage message, final Session session) {
        message.setDiscard(true);
        return message;
    }
}
