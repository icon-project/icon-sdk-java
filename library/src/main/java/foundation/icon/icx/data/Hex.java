/*
 * Copyright 2018 ICON Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.icon.icx.data;

import org.web3j.utils.Strings;

import java.math.BigInteger;

public class Hex {
    public static final String HEX_PREFIX = "0x";

    private final static char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static String toHexStringZeroPadded(BigInteger value, int size, boolean withPrefix) {
        String result = toHexString(value, false);

        int length = result.length();
        if (length > size) {
            throw new UnsupportedOperationException(
                    "Value " + result + "is larger then length " + size);
        } else if (value.signum() < 0) {
            throw new UnsupportedOperationException("Value cannot be negative");
        }

        if (length < size) {
            result = Strings.zeros(size - length) + result;
        }

        if (withPrefix) {
            return HEX_PREFIX + result;
        } else {
            return result;
        }
    }

    public static byte[] toBytesPadded(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }

    public static byte[] hexStringToByteArray(String input) {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(cleanInput.charAt(i), 16) << 4)
                    + Character.digit(cleanInput.charAt(i + 1), 16));
        }
        return data;
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    /**
     * Convert BigInteger to hex string
     *
     * @param value      a BigInteger value to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(BigInteger value, boolean withPrefix) {
        if (withPrefix) return HEX_PREFIX + value.toString(16);
        else return value.toString(16);
    }

    /**
     * Convert byte array to hex string
     *
     * @param value      a byte array to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(byte[] value, boolean withPrefix) {
        StringBuilder sb = new StringBuilder(value.length * 2);
        if (withPrefix) sb.append("0x");
        for (byte aByte : value) {
            sb.append(HEX_DIGITS[aByte >> 4 & 0x0f]);
            sb.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Convert boolean to hex string
     *
     * @param value      a boolean value to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(boolean value, boolean withPrefix) {
        if (withPrefix) return value ? "0x1" : "0x0";
        else return value ? "1" : "0";
    }

    /**
     * Convert hex string to BigInteger
     *
     * @param value      a hex string to convert
     * @return BigInteger
     */
    public static BigInteger toBigInteger(String value) {
        String body = "";
        if (value.charAt(0) == '-') {
            body = value.substring(0,1);
            value = value.substring(1);
        }
        body = body + cleanHexPrefix(value);
        return new BigInteger(body, 16);
    }

}
