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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import foundation.icon.icx.transport.jsonrpc.Serializers.BigIntegerSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.BooleanSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.BytesSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcItemSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializerTest {
    private ObjectMapper mapper;

    @BeforeEach
    void initAll() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RpcItem.class, new RpcItemSerializer());
        module.addSerializer(BigInteger.class, new BigIntegerSerializer());
        module.addSerializer(boolean.class, new BooleanSerializer());
        module.addSerializer(Boolean.class, new BooleanSerializer());
        module.addSerializer(byte[].class, new BytesSerializer());
        mapper.registerModule(module);
    }

    @Test
    void testRpcSerializer() throws JsonProcessingException {

        RpcItem intValue = new RpcValue(new BigInteger("1234"));
        RpcItem booleanValue = new RpcValue(false);
        RpcItem stringValue = new RpcValue("string");
        RpcItem bytesValue = new RpcValue(new byte[]{0x1, 0x2, 0x3});
        RpcItem escapeValue = new RpcValue("\\.{}[]");

        RpcItem object = new RpcObject.Builder()
                .put("intValue", intValue)
                .put("booleanValue", booleanValue)
                .put("stringValue", stringValue)
                .put("bytesValue", bytesValue)
                .put("escapeValue", escapeValue)
                .build();

        RpcItem array = new RpcArray.Builder()
                .add(object)
                .add(intValue)
                .add(booleanValue)
                .add(stringValue)
                .add(bytesValue)
                .build();

        RpcItem root = new RpcObject.Builder()
                .put("object", object)
                .put("array", array)
                .put("intValue", intValue)
                .put("booleanValue", booleanValue)
                .put("stringValue", stringValue)
                .put("bytesValue", bytesValue)
                .build();

        String json = mapper.writeValueAsString(root);
        assertTrue(json.length() > 0);
    }

    @Test
    void testBigIntegerSerializer() throws JsonProcessingException {
        BigInteger intValue = new BigInteger("1234", 16);
        String json = mapper.writeValueAsString(intValue);
        System.out.print(json);
        assertEquals("\"0x1234\"", json);
    }

    @Test
    void testBooleanSerializer() throws JsonProcessingException {
        String json = mapper.writeValueAsString(false);
        System.out.print(json);
        assertEquals("\"0x0\"", json);
    }

    @Test
    void testBytesSerializer() throws JsonProcessingException {
        String json = mapper.writeValueAsString(new byte[]{0x1, 0x2, 0x3, 0x4});
        System.out.print(json);
        assertEquals("\"0x01020304\"", json);
    }

    @Test
    void testObject() throws IOException {
        String json = mapper.writeValueAsString(new Custom());
        System.out.print(json);
        TypeReference<Map<String, Object>> type =
                new TypeReference<Map<String, Object>>() {
                };
        Map<String, Object> value = mapper.readValue(json, type);
        assertEquals("stringValue", value.get("stringValue"));
        assertEquals("0x1234", value.get("intValue"));
        assertEquals("0x0", value.get("booleanValue"));
        assertEquals("0x01020304", value.get("bytesValue"));
        assertEquals("0x1234", ((List) value.get("intArrayValue")).get(0));
        assertEquals("0x1234", ((List) value.get("intArrayValue")).get(1));
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Custom {
        public String stringValue = "stringValue";
        public BigInteger intValue = new BigInteger("1234", 16);
        public boolean booleanValue = false;
        public byte[] bytesValue = new byte[]{0x1, 0x2, 0x3, 0x4};
        public BigInteger[] intArrayValue = new BigInteger[]{intValue, intValue};
    }
}
