package org.web3j.utils.exceptions;

/**
 * Original Code
 * https://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/exceptions/MessageDecodingException.java
 * Encoding exception.
 */
public class MessageDecodingException extends RuntimeException {
    public MessageDecodingException(String message) {
        super(message);
    }

    public MessageDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
