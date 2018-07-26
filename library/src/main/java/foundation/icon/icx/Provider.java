package foundation.icon.icx;

/**
 * Provider class transports the request and receives the response
 */
public interface Provider {

    /**
     * Prepares to execute the request
     * @param request A request to send
     * @return a Call object to execute
     */
    <T> Call<T> request(Request request);
}
