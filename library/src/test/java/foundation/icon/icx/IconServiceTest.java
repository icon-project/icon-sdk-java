package foundation.icon.icx;

import foundation.icon.icx.IcxCall.Builder;
import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IconServiceTest {
    @Test
    void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <O> Call<O> request(Request request, RpcConverter<O> converter) {
                return null;
            }

        });
        assertNotNull(iconService);
    }

    @SuppressWarnings("unused")
    @Test
    void testQuery() {
        Provider provider = mock(Provider.class);


        IconService iconService = new IconService(provider);
        iconService.addConverterFactory(new RpcConverter.RpcConverterFactory() {
            @Override
            public <T> RpcConverter<T> create(Class<T> type) {
                if(PersonResponse.class == type){
                    return new RpcConverter<T>() {
                        @Override
                        public T convertTo(RpcItem object) {
                            return null;
                        }

                        @Override
                        public RpcItem convertFrom(T object) {
                            return null;
                        }
                    };
                }
                return null;
            }
        });

        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");
        person.hasPermission = false;

        IcxCall<PersonResponse> icxCall = new Builder()
                .from("0x01")
                .to("0x02")
                .method("addUser")
                .params(person)
                .buildWith(PersonResponse.class);

        Call<PersonResponse> query = iconService.query(icxCall);

        verify(provider).request(
                argThat(Objects::nonNull),
                argThat(Objects::nonNull));

    }

    @Test
    void testGetTotalSupply() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getTotalSupply();

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTotalSupply", null)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBalance() {
        Provider provider = mock(Provider.class);

        String address = "hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";

        IconService iconService = new IconService(provider);
        iconService.getBalance(address);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBalance", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBlockByHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBlock(BigInteger.ONE);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(BigInteger.ONE));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBlockByHeight", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBlockByHash() {
        Provider provider = mock(Provider.class);

        String hash = "033f8d96045eb8301fd17cf078c28ae58a3ba329f6ada5cf128ee56dc2af26f7";

        IconService iconService = new IconService(provider);
        iconService.getBlock(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("hash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBlockByHash", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetLastBlock() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBlock("latest");

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getLastBlock", null)),
                argThat(Objects::nonNull));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetScoreApi() {
        Provider provider = mock(Provider.class);

        String address = "cx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";

        IconService iconService = new IconService(provider);
        iconService.getScoreApi(address);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getScoreApi", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetTransaction() {
        Provider provider = mock(Provider.class);

        String hash = "0x2600770376fbf291d3d445054d45ed15280dd33c2038931aace3f7ea2ab59dbc";

        IconService iconService = new IconService(provider);
        iconService.getTransaction(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("txHash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTransactionByHash", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetTransactionResult() {
        Provider provider = mock(Provider.class);

        String hash = "0x2600770376fbf291d3d445054d45ed15280dd33c2038931aace3f7ea2ab59dbc";

        IconService iconService = new IconService(provider);
        iconService.getTransactionResult(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("txHash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTransactionResult", params)),
                argThat(Objects::nonNull));
    }

    @SuppressWarnings("unchecked")
    private boolean isRequestMatches(Request request, String method, Map<String, RpcValue> params) {

        boolean isMethodMatches = request.getMethod().equals(method);

        boolean isParamMatches = (request.getParams() == null && params == null);
        if (!isParamMatches && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                RpcValue value = ((RpcValue) (request.getParams()).getItem(key));
                isParamMatches = value.asString().equals(params.get(key).asString());
                if (!isParamMatches) break;
            }
        }
        return isMethodMatches && isParamMatches;
    }

    @SuppressWarnings("WeakerAccess")
    class Person {
        public String name;
        public BigInteger age;
        public boolean hasPermission;
    }

    @SuppressWarnings("unused")
    class PersonResponse {
        public boolean isOk;
        public String message;
    }
}
