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

import java.math.BigInteger;
import java.nio.ByteBuffer;

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

    private String noPrefixValue;

    public Hex(String value) {
        if (isValidHex(value)) this.noPrefixValue = cleanHexPrefix(value);
        else throw new IllegalArgumentException("The value is not hex string.");
    }

    public String asString() {
        return withPrefix(noPrefixValue, true);
    }

    public String asString(boolean withPrefix) {
        return withPrefix(noPrefixValue, withPrefix);
    }

    public byte[] asBytes() {
        return hexStringToByteArray(noPrefixValue);
    }

    public static boolean isValidHex(String value) {
        String v = cleanHexPrefix(value);
        return v.matches("^[0-9a-fA-F]+$");
    }

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
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size - length; i++) {
                sb.append('0');
            }
            result = sb.append(result).toString();
        }

        return withPrefix(result, withPrefix);
    }

    public static byte[] toBytesPadded(BigInteger value, int length) {
        byte[] bytes = value.toByteArray();
        int bytesLength = bytes.length;
        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        byte[] zero = new byte[length - bytesLength];
        return ByteBuffer.wrap(new byte[length]).put(zero).put(bytes).array();
    }

    public static byte[] hexStringToByteArray(String input) {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[]{};
        }

        byte[] bytes;
        int start;
        if (len % 2 != 0) {
            bytes = new byte[(len / 2) + 1];
            bytes[0] = (byte) Integer.parseInt("0", 16);
            start = 1;
        } else {
            bytes = new byte[len / 2];
            start = 0;
        }

        for (int i = start; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(
                    cleanInput.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return input.matches("^0x.*");
    }

    /**
     * Convert BigInteger to hex string
     *
     * @param value      a BigInteger value to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(BigInteger value, boolean withPrefix) {
        return withPrefix(value.toString(16), withPrefix);
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
        String v = value ? "1" : "0";
        return withPrefix(v, withPrefix);
    }

    /**
     * Convert hex string to BigInteger
     *
     * @param value a hex string to convert
     * @return BigInteger
     */
    public static BigInteger toBigInteger(String value) {
        String body = "";
        if (value.charAt(0) == '-') {
            body = value.substring(0, 1);
            value = value.substring(1);
        }
        body = body + cleanHexPrefix(value);
        return new BigInteger(body, 16);
    }

    public static String withPrefix(String value, boolean withPrefix) {
        if (withPrefix) {
            return HEX_PREFIX + value;
        } else {
            return value;
        }
    }

}
