package foundation.icon.icx;

import java.io.InputStream;

/**
 * A request to be execute
 */
interface Request {

    /**
     * Returns the request data stream
     * @return the request data stream
     */
    InputStream getInputStream();
}
