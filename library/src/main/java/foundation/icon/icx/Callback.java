package foundation.icon.icx;

/**
 *  A callback of the asynchronous execution
 */
public interface Callback<T> {

    /**
     * Invoked when the execution is completed
     * @param response a result of the execution
     */
    void onResponse(Response<T> response);
}
