package foundation.icon.icx;

/**
 *  A callback of the asynchronous execution
 */
public interface Callback<T> {

    /**
     * Invoked when the execution is successful
     * @param result a result of the execution
     */
    void onSuccess(T result);

    /**
     * Invoked when the execution is completed with an exception
     * @param exception an exception thrown during the execution
     */
    void onFailure(Exception exception);
}
