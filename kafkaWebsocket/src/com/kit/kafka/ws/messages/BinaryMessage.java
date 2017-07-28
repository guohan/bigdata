

package com.kit.kafka.ws.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BinaryMessage extends AbstractMessage {
    private static Logger LOG = LoggerFactory.getLogger(BinaryMessage.class);

    private String topic;
    private byte[] message;

    public BinaryMessage(String topic, byte[] message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String getKey() {
        return "";
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public Boolean isKeyed() {
        return false;
    }

    @Override
    public byte[] getMessageBytes() {
        return message;
    }

    static public class BinaryMessageDecoder implements Decoder.Binary<BinaryMessage> {
        public BinaryMessageDecoder() {

        }

        @Override
        public BinaryMessage decode(ByteBuffer byteBuffer) throws DecodeException {
            int bufLen = byteBuffer.array().length;
            int topicLen = byteBuffer.get(0);
            String topic = new String(byteBuffer.array(), 1, topicLen, Charset.forName("UTF-8"));
            ByteBuffer messageBuf = ByteBuffer.allocate(bufLen - topicLen - 1);
            System.arraycopy(byteBuffer.array(), topicLen + 1, messageBuf.array(), 0, bufLen - topicLen - 1);
            return new BinaryMessage(topic, messageBuf.array());
        }

        @Override
        public boolean willDecode(ByteBuffer byteBuffer) {
            return true;
        }

        @Override
        public void init(EndpointConfig endpointConfig) {

        }

        @Override
        public void destroy() {

        }
    }

    static public class BinaryMessageEncoder implements Encoder.Binary<BinaryMessage> {
        public BinaryMessageEncoder() {

        }

        @Override
        public ByteBuffer encode(BinaryMessage binaryMessage) throws EncodeException {
            ByteBuffer buf =
                    ByteBuffer.allocate(binaryMessage.getTopic().length() + binaryMessage.getMessage().length + 1);
            buf.put((byte)binaryMessage.getTopic().length())
               .put(binaryMessage.getTopic().getBytes(Charset.forName("UTF-8")))
               .put(binaryMessage.getMessage());
            return buf;
        }

        @Override
        public void init(EndpointConfig endpointConfig) {

        }

        @Override
        public void destroy() {

        }
    }
}
