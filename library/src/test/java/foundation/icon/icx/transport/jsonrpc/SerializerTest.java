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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcItemSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializerTest {
    private ObjectMapper mapper;

    @BeforeEach
    void initAll() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RpcItem.class, new RpcItemSerializer());
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

}
