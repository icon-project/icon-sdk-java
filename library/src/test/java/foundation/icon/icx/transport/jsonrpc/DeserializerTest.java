/*
 * Copyright 2018 theloop Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package foundation.icon.icx.transport.jsonrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import foundation.icon.icx.transport.jsonrpc.Deserializers.BigIntegerDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.BooleanDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.BytesDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.RpcFieldDeserializer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeserializerTest {
    private ObjectMapper mapper;

    @BeforeEach
    void initAll() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RpcField.class, new RpcFieldDeserializer());
        module.addDeserializer(BigInteger.class, new BigIntegerDeserializer());
        module.addDeserializer(boolean.class, new BooleanDeserializer());
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        module.addDeserializer(byte[].class, new BytesDeserializer());
        mapper.registerModule(module);
    }

    @Test
    void testRpcDeserializer() throws IOException {

        String json = "{\"stringValue\":\"string\",\"array\":[{\"longValue\":1533018344753765,\"stringValue\":\"string\",\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\"},\"0x4d2\",\"0x0\",\"string\",\"0x010203\"],\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\",\"object\":{\"stringValue\":\"string\",\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\"}}";
        RpcObject root = (RpcObject) mapper.readValue(json, RpcField.class);

        RpcValue rpcValue;

        RpcArray array = (RpcArray) root.getValue("array");

        RpcObject obj = (RpcObject) array.get(0);
        rpcValue = (RpcValue) obj.getValue("intValue");
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) obj.getValue("booleanValue");
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) obj.getValue("stringValue");
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) obj.getValue("bytesValue");
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asBytes());
        rpcValue = (RpcValue) obj.getValue("longValue");
        assertEquals(new BigInteger(String.valueOf(1533018344753765L)), rpcValue.asInteger());

        rpcValue = (RpcValue) array.get(1);
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) array.get(2);
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) array.get(3);
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) array.get(4);
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asBytes());

        rpcValue = (RpcValue) root.getValue("intValue");
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) root.getValue("booleanValue");
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) root.getValue("stringValue");
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) root.getValue("bytesValue");
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asBytes());
    }

    @Test
    void testObject() throws IOException {
        String json = "{\"longValue\":1533018344753765,\"stringValue\":\"stringValue\",\"intValue\":\"0x1234\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x01020304\",\"intArrayValue\":[\"0x1234\",\"0x1234\"]}";
        Custom custom = mapper.readValue(json, Custom.class);

        assertEquals("stringValue", custom.stringValue);
        assertEquals(new BigInteger("1234", 16), custom.intValue);
        assertEquals(false, custom.booleanValue);
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3, 0x4}, custom.bytesValue);
        assertEquals(new BigInteger("1234", 16), custom.intArrayValue[0]);
        assertEquals(new BigInteger("1234", 16), custom.intArrayValue[1]);
        assertEquals(new BigInteger(String.valueOf(1533018344753765L)), custom.longValue);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Custom {
        public String stringValue;
        public BigInteger intValue;
        public boolean booleanValue;
        public byte[] bytesValue;
        public BigInteger[] intArrayValue;
        public BigInteger longValue;
    }
}
