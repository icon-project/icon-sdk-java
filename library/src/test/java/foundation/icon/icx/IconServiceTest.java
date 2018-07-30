package foundation.icon.icx;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import foundation.icon.icx.IcxCall.Builder;
import foundation.icon.icx.transport.jsonrpc.Request;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IconServiceTest {
    @Test
    void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <I, O> Call<O> request(Request<I> request, Class<O> responseType) {
                return null;
            }
        });
        assertNotNull(iconService);
    }

    @Test
    void testQuery() {
        Provider provider = mock(Provider.class);


        IconService iconService = new IconService(provider);

        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");
        person.hasPermission = false;

        IcxCall<Person> icxCall = new Builder<Person>()
                .from("0x01")
                .to("0x02")
                .method("addUser")
                .params(person)
                .responseType(PersonResponse.class)
                .build();

        iconService.query(icxCall);

        verify(provider).request(
                argThat(argument -> argument.getParams().equals(icxCall)),
                argThat(argument -> argument.equals(icxCall.responseType())));


    }

    @SuppressWarnings("WeakerAccess")
    class Person {
        public String name;
        public BigInteger age;
        public boolean hasPermission;
    }

    @SuppressWarnings("unused")
    class PersonResponse{
        public boolean isOk;
        public String message;
    }
}
