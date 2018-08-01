package org.web3j.utils;

/**
 * Original Code
 * https://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/utils/Assertions.java
 * Assertion utility functions.
 */
public class Assertions {

    /**
     * Verify that the provided precondition holds true.
     *
     * @param assertionResult assertion value
     * @param errorMessage error message if precondition failure
     */
    public static void verifyPrecondition(boolean assertionResult, String errorMessage) {
        if (!assertionResult) {
            throw new RuntimeException(errorMessage);
        }
    }
}
