/*
 * Copyright 2018 ICON Foundation
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

import static org.junit.jupiter.api.Assertions.*;

class DeserializerTest {
    private ObjectMapper mapper;

    @BeforeEach
    void initAll() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RpcItem.class, new RpcItemDeserializer());
        mapper.registerModule(module);
    }

    @Test
    void testRpcDeserializer() throws IOException {

        String json = "{\"stringValue\":\"string\",\"array\":[{\"longValue\":1533018344753765,\"stringValue\":\"string\",\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\"},\"0x4d2\",\"0x0\",\"string\",\"0x010203\"],\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\",\"object\":{\"stringValue\":\"string\",\"intValue\":\"0x4d2\",\"booleanValue\":\"0x0\",\"bytesValue\":\"0x010203\"}}";
        RpcObject root = (RpcObject) mapper.readValue(json, RpcItem.class);

        RpcValue rpcValue;

        RpcArray array = (RpcArray) root.getItem("array");

        RpcObject obj = (RpcObject) array.get(0);
        rpcValue = (RpcValue) obj.getItem("intValue");
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) obj.getItem("booleanValue");
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) obj.getItem("stringValue");
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) obj.getItem("bytesValue");
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asByteArray());
        rpcValue = (RpcValue) obj.getItem("longValue");
        assertEquals(new BigInteger(String.valueOf(1533018344753765L)), rpcValue.asInteger());

        rpcValue = (RpcValue) array.get(1);
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) array.get(2);
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) array.get(3);
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) array.get(4);
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asByteArray());

        rpcValue = (RpcValue) root.getItem("intValue");
        assertEquals(new BigInteger("4d2", 16), rpcValue.asInteger());
        rpcValue = (RpcValue) root.getItem("booleanValue");
        assertEquals(false, rpcValue.asBoolean());
        rpcValue = (RpcValue) root.getItem("stringValue");
        assertEquals("string", rpcValue.asString());
        rpcValue = (RpcValue) root.getItem("bytesValue");
        assertArrayEquals(new byte[]{0x1, 0x2, 0x3}, rpcValue.asByteArray());
    }

    @Test
    void testRpcValue() throws IOException {
        String json = "\"0x1234\"";
        RpcItem rpcItem = mapper.readValue(json, RpcItem.class);

        assertTrue(rpcItem instanceof RpcValue);
        assertEquals("0x1234", ((RpcValue) rpcItem).asString());
    }

}
