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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Serializers for jsonrpc value
 */
public class Serializers {
    public static class BigIntegerSerializer extends JsonSerializer<BigInteger> {

        @Override
        public void serialize(
                BigInteger value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(new RpcValue(value).asString());
        }
    }

    public static class BooleanSerializer extends JsonSerializer<Boolean> {

        @Override
        public void serialize(
                Boolean value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(new RpcValue(value).asString());
        }
    }

    public static class BytesSerializer extends JsonSerializer<byte[]> {

        @Override
        public void serialize(
                byte[] value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(new RpcValue(value).asString());
        }
    }

    public static class RpcItemSerializer extends JsonSerializer<RpcItem> {

        final char[] escapeChars = { '\\', '.', '{', '}', '[', ']' };
        final Map escapeMap = new HashMap();
        boolean useEscape = false;

        public RpcItemSerializer() {
        }

        public RpcItemSerializer(boolean useEscape) {
            initEscapeMap();
            this.useEscape = useEscape;
        }

        private void initEscapeMap() {
            for (char c : escapeChars) {
                escapeMap.put(c, c);
            }
        }

        private void writeShortEscape(JsonGenerator gen, char c) throws IOException {
            gen.writeRaw('\\');
            gen.writeRaw(c);
        }

        @Override
        public void serialize(
                RpcItem item, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            serialize(item, gen);
        }

        private void serialize(RpcItem item, JsonGenerator gen)
                throws IOException {

            if (item instanceof RpcObject) {
                RpcObject object = (RpcObject) item;
                gen.writeStartObject();
                for (String key : object.keySet()) {
                    RpcItem value = object.getItem(key);
                    if(value != null) {
                        gen.writeFieldName(key);
                        serialize(value, gen);
                    }
                }
                gen.writeEndObject();
            } else if (item instanceof RpcArray) {
                RpcArray array = (RpcArray) item;
                gen.writeStartArray();
                for (Iterator<RpcItem> it = array.iterator(); it.hasNext(); ) {
                    serialize(it.next(), gen);
                }
                gen.writeEndArray();
            } else {
                RpcValue value = (RpcValue) item;
                if (useEscape)
                    writeWithEscape(value.asString(), gen);
                else
                    gen.writeString(value.asString());
            }
        }

        private void writeWithEscape(String str, JsonGenerator gen)  throws IOException {
            int status = ((JsonWriteContext) gen.getOutputContext()).writeValue();
            switch (status) {
                case JsonWriteContext.STATUS_OK_AFTER_COLON:
                    gen.writeRaw('.');
                    break;
                case JsonWriteContext.STATUS_OK_AFTER_COMMA:
                    gen.writeRaw('.');
                    break;
                case JsonWriteContext.STATUS_EXPECT_NAME:
                    throw new JsonGenerationException("Can not write string value here");
            }
            for (char c : str.toCharArray()) {
                if (escapeMap.containsKey(c)) {
                    writeShortEscape(gen, c);
                } else {
                    gen.writeRaw(c);
                }
            }
        }
    }
}