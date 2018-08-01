package org.web3j.utils.exceptions;

/**
 * Original Code
 * https://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/exceptions/MessageEncodingException.java
 * Encoding exception.
 */
public class MessageEncodingException extends RuntimeException {
    public MessageEncodingException(String message) {
        super(message);
    }

    public MessageEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
