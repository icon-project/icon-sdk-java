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

package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class CallTest {
    @Test
    void testCallBuilder() {
        Address from = new Address("hx0000000000000000000000000000000000000000");
        Address to = new Address("cx1111111111111111111111111111111111111111");
        String method = "myMethod";
        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");

        Call<PersonResponse> call = new Call.Builder()
                .from(from)
                .to(to)
                .method(method)
                .params(person)
                .buildWith(PersonResponse.class);

        RpcObject properties = call.getProperties();
        RpcObject data = properties.getItem("data").asObject();
        RpcObject dataParams = data.getItem("params").asObject();

        Assertions.assertEquals(from, properties.getItem("from").asAddress());
        Assertions.assertEquals(to, properties.getItem("to").asAddress());
        Assertions.assertEquals(method, data.getItem("method").asString());
        Assertions.assertEquals(person.name, dataParams.getItem("name").asString());
        Assertions.assertEquals(person.age, dataParams.getItem("age").asInteger());
        Assertions.assertEquals(PersonResponse.class, call.responseType());
    }


    @SuppressWarnings("WeakerAccess")
    static class Person {
        public String name;
        public BigInteger age;
    }

    @SuppressWarnings("unused")
    static class PersonResponse {
        public boolean isOk;
        public String message;
    }


}
