package foundation.icon.icx;

import foundation.icon.icx.transport.jsonrpc.Request;

/**
 * IconService which provides APIs of ICON network
 */
public class IconService {

    private Provider provider;

    public IconService(Provider provider) {
        this.provider = provider;
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
