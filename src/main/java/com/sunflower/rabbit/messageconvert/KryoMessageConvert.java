package com.sunflower.rabbit.messageconvert;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author xzhen
 * @created 16:27 27/01/2019
 * @description 对象序列化
 */
public class KryoMessageConvert implements MessageConverter {

    public static final String CONTENT_TYPE = "application/x-kryo";
    public static final String DEFAULT_CHARSET = "UTF-8";
    private KryoFactory kryoFactory;

    public KryoMessageConvert() {
        kryoFactory = new DefaultKryoFactory();
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        Kryo kryo = kryoFactory.create();
        byte[] bytes;
        try (Output output = new ByteBufferOutput(1024, 1024 * 10)) {
            kryo.writeClassAndObject(output, object);
            bytes = output.toBytes();
        }
        if (messageProperties == null) {
            messageProperties = new MessageProperties();
        }
        messageProperties.setContentType(CONTENT_TYPE);
        messageProperties.setContentLength(bytes.length);
        messageProperties.setContentEncoding(DEFAULT_CHARSET);
        return new Message(bytes, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        Object content = null;
        MessageProperties properties = message.getMessageProperties();
        if (properties != null) {
            if (properties.getContentType() != null && properties.getContentType().contains("x-kryo")) {
                Kryo kryo = kryoFactory.create();
                content = kryo.readClassAndObject(new ByteBufferInput(message.getBody()));
            } else {
                throw new MessageConversionException("Converter not applicable to this message");
            }
        }
        return content;
    }

    class DefaultKryoFactory implements KryoFactory {

        @Override
        public Kryo create() {
            return new Kryo();
        }
    }

}
