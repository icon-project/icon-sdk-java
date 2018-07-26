package foundation.icon.icx;

import java.io.IOException;

/**
 *  Call class executes the request that has been prepared
 */
public interface Call<T> {

    /**
     * Executes synchronously
     * @return Response
     * @throws IOException an exception if there exist errors
     */
    Response<T> execute() throws IOException;

    /**
     * Executes asynchronously
     * @param callback the callback is invoked when the execution is completed
     */
    void execute(Callback<T> callback);
}
