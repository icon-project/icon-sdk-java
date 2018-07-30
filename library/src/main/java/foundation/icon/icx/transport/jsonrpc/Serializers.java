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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

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
                gen.writeString(value.asString());
            }
        }
    }
}