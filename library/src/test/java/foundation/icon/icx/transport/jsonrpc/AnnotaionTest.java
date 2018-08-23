/*
 * Copyright 2018 ICON Foundation.
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
 */

package foundation.icon.icx.transport.jsonrpc;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;


public class AnnotaionTest {

    @Test
    void testConvert() {

        RpcObject rpcObject = new RpcObject.Builder()
                .put("boolean", new RpcValue(true))
                .put("string", new RpcValue("string value"))
                .put("BigInteger", new RpcValue(new BigInteger("1234")))
                .put("Address", new RpcValue(new Address("hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31")))
                .put("bytes", new RpcValue(new Bytes("0xf123")))
                .put("byteArray", new RpcValue(new byte[]{1, 2, 3, 4, 5}))
                .build();

        RpcConverter<AnnotaionClass> converter = new AnnotatedConverterFactory().create(AnnotaionClass.class);
        AnnotaionClass result = converter.convertTo(rpcObject);

        Assertions.assertEquals(rpcObject.getItem("boolean").asBoolean(), result.booleanType);
        Assertions.assertEquals(rpcObject.getItem("string").asString(), result.stringType);
        Assertions.assertEquals(rpcObject.getItem("BigInteger").asInteger(), result.bigIntegerType);
        Assertions.assertEquals(rpcObject.getItem("Address").asAddress(), result.addressType);
        Assertions.assertEquals(rpcObject.getItem("bytes").asBytes(), result.bytesType);
        Assertions.assertArrayEquals(rpcObject.getItem("byteArray").asByteArray(), result.byteArrayType);

    }


    @AnnotationConverter
    public class AnnotaionClass {

        @ConverterName("boolean")
        boolean booleanType;
        @ConverterName("string")
        String stringType;
        @ConverterName("BigInteger")
        BigInteger bigIntegerType;
        @ConverterName("Address")
        Address addressType;
        @ConverterName("bytes")
        Bytes bytesType;
        @ConverterName("byteArray")
        byte[] byteArrayType;

        public AnnotaionClass() {
        }

        @Override
        public String toString() {
            return "AnnotaionClass{" +
                    "booleanType=" + booleanType +
                    ", stringType='" + stringType + '\'' +
                    ", bigIntegerType=" + bigIntegerType +
                    ", addressType=" + addressType +
                    ", bytesType=" + bytesType +
                    ", byteArrayType=" + Arrays.toString(byteArrayType) +
                    '}';
        }
    }
}
