package foundation.icon.icx;

/**
 * A response of the request
 */
public interface Response<T> {

    /**
     * Returns the result of the request
     * @return the result of the request
     */
    T getResult();
}
