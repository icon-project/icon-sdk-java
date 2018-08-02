package foundation.icon.icx;

import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;

/**
 * Provider class transports the request and receives the response
 */
public interface Provider {

    /**
     * Prepares to execute the request
     *
     * @param request   A request to send
     * @param converter converter for the responseType
     * @return a Call object to execute
     */
    <O> Call<O> request(Request request, RpcConverter<O> converter);
}
