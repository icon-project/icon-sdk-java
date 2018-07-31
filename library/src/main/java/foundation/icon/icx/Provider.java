package foundation.icon.icx;

import foundation.icon.icx.transport.jsonrpc.Request;

/**
 * Provider class transports the request and receives the response
 */
public interface Provider {

    /**
     * Prepares to execute the request
     *
     * @param request A request to send
     * @param responseType the data type of the response
     * @return a Call object to execute
     */
    <I, O> Call<O> request(Request<I> request, Class<O> responseType);
}
