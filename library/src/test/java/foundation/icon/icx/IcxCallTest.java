package foundation.icon.icx;

import foundation.icon.icx.transport.jsonrpc.RpcObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class IcxCallTest {
    @Test
    void testIcxCallBuilder() {
        String from = "hx0000000000000000000000000000000000000000";
        String to = "cx1111111111111111111111111111111111111111";
        String method = "myMethod";
        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");

        IcxCall<PersonResponse> icxCall = new IcxCall.Builder()
                .from(from)
                .to(to)
                .method(method)
                .params(person)
                .buildWith(PersonResponse.class);

        RpcObject properties = icxCall.getProperties();
        RpcObject data = properties.getItem("data").asObject();
        RpcObject dataParams = data.getItem("params").asObject();

        Assertions.assertEquals(from, properties.getItem("from").asString());
        Assertions.assertEquals(to, properties.getItem("to").asString());
        Assertions.assertEquals(method, data.getItem("method").asString());
        Assertions.assertEquals(person.name, dataParams.getItem("name").asString());
        Assertions.assertEquals(person.age, dataParams.getItem("age").asInteger());
        Assertions.assertEquals(PersonResponse.class, icxCall.responseType());
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
