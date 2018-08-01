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

    public static class RpcFieldSerializer extends JsonSerializer<RpcField> {

        final char[] escapeChars = { '\\', '.', '{', '}', '[', ']' };
        final Map escapeMap = new HashMap();
        boolean useEscape = false;

        public RpcFieldSerializer() {
        }

        public RpcFieldSerializer(boolean useEscape) {
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
                RpcField field, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            serialize(field, gen);
        }

        private void serialize(RpcField field, JsonGenerator gen)
                throws IOException {

            if (field instanceof RpcObject) {
                RpcObject object = (RpcObject) field;
                gen.writeStartObject();
                for (String key : object.keySet()) {
                    gen.writeFieldName(key);
                    serialize(object.getValue(key), gen);
                }
                gen.writeEndObject();
            } else if (field instanceof RpcArray) {
                RpcArray array = (RpcArray) field;
                gen.writeStartArray();
                for (Iterator<RpcField> it = array.iterator(); it.hasNext(); ) {
                    serialize(it.next(), gen);
                }
                gen.writeEndArray();
            } else {
                RpcValue value = (RpcValue) field;
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
            gen.writeRaw('"');
            for (char c : str.toCharArray()) {
                if (escapeMap.containsKey(c)) {
                    writeShortEscape(gen, c);
                } else {
                    gen.writeRaw(c);
                }
            }
            gen.writeRaw('"');
        }
    }
}