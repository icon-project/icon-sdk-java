package foundation.icon.icx;

import foundation.icon.icx.IcxCall.Builder;
import foundation.icon.icx.data.Block;
import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

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

    @Test
    void testGetBalance() {
        Provider provider = mock(Provider.class);

        String address = "hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";

        IconService iconService = new IconService(provider);
        iconService.getBalance(address);


        verify(provider).request(
                argThat( request ->{
                    boolean isMethodMatches = request.getMethod().equals("icx_getBalance");
                    RpcValue value = ((RpcValue)((RpcObject) request.getParams()).getValue("address"));
                    boolean isAddressMatches = value.asString().equals(address);
                    return isMethodMatches && isAddressMatches;
                }),
                argThat(responseType -> responseType.equals(BigInteger.class)));
    }

    @Test
    void testGetBlockByHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBlock(BigInteger.ONE);

        verify(provider).request(
                argThat( request ->{
                    boolean isMethodMatches = request.getMethod().equals("icx_getBlockByHeight");
                    RpcValue value = ((RpcValue)((RpcObject) request.getParams()).getValue("height"));
                    boolean isAddressMatches = value.asInteger().equals(BigInteger.ONE);
                    return isMethodMatches && isAddressMatches;
                }),
                argThat(responseType -> responseType.equals(Block.class)));
    }

    @Test
    void testGetBlockByHash() {
        Provider provider = mock(Provider.class);

        String hash = "033f8d96045eb8301fd17cf078c28ae58a3ba329f6ada5cf128ee56dc2af26f7";

        IconService iconService = new IconService(provider);
        iconService.getBlock(hash);

        verify(provider).request(
                argThat( request ->{
                    boolean isMethodMatches = request.getMethod().equals("icx_getBlockByHash");
                    RpcValue value = ((RpcValue)((RpcObject) request.getParams()).getValue("hash"));
                    boolean isAddressMatches = value.asString().equals(hash);
                    return isMethodMatches && isAddressMatches;
                }),
                argThat(responseType -> responseType.equals(Block.class)));

        iconService.getBlock("latest");

        verify(provider).request(
                argThat( request ->{
                    boolean isMethodMatches = request.getMethod().equals("icx_getLastBlock");
                    boolean isParamMatches = request.getParams() == null;
                    return isMethodMatches && isParamMatches;
                }),
                argThat(responseType -> responseType.equals(Block.class)));
    }

    @Test
    void testGetLastBlock() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBlock("latest");

        verify(provider).request(
                argThat( request ->{
                    boolean isMethodMatches = request.getMethod().equals("icx_getLastBlock");
                    boolean isParamMatches = request.getParams() == null;
                    return isMethodMatches && isParamMatches;
                }),
                argThat(responseType -> responseType.equals(Block.class)));
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
