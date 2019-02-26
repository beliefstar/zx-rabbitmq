package com.zx.rabbit.common.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.zx.rabbit.common.serialize.ObjectSerializer;
import org.springframework.stereotype.Component;

/**
 * @author xzhen
 * @created 16:27 27/01/2019
 * @description 对象序列化
 */
@Component
public class KryoSerializer implements ObjectSerializer {

    private KryoFactory kryoFactory;

    public KryoSerializer() {
        kryoFactory = new DefaultKryoFactory();
    }

    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = kryoFactory.create();
        try (Output output = new ByteBufferOutput(1024, 1024 * 10)) {
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        }
    }

    @Override
    public Object deserialize(byte[] body) {
        Kryo kryo = kryoFactory.create();
        Input input = new ByteBufferInput(body);
        return kryo.readClassAndObject(input);
    }

    class DefaultKryoFactory implements KryoFactory {

        @Override
        public Kryo create() {
            return new Kryo();
        }
    }

}
