package foundation.icon.icx;

import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;

/**
 * IconService which provides APIs of ICON network
 */
public class IconService {

    private Provider provider;

    public IconService(Provider provider) {
        this.provider = provider;
    }

    /**
     * Get the total number of issued coins.
     * @return A BigNumber instance of the total number of coins.
     */
    public Call<BigInteger> getTotalSupply() {
        Request<RpcObject> request = new Request<>("icx_getTotalSupply", null);
        return provider.request(request, BigInteger.class);
    }

    /**
     * Get the balance of an address.
     * @param address The address to get the balance of.
     * @return A BigNumber instance of the current balance for the given address in loop.
     */
    public Call<BigInteger> getBalance(String address) {
        RpcObject params = new RpcObject.Builder()
                .put("address", new RpcValue(address))
                .build();
        Request<RpcObject> request = new Request<>("icx_getBalance", params);
        return provider.request(request, BigInteger.class);
    }

    /**
     * Calls a SCORE API just for reading
     * @param icxCall instance of IcxCall
     * @param <I> input type of the parameter
     * @return the Call object can execute a request
     */
    public <I> Call<?> query(IcxCall<I> icxCall) {
        Request<IcxCall<I>> request = new Request<>("icx_call", icxCall);
        return provider.request(request, icxCall.responseType());
    }

}
