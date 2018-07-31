/*
 * Copyright 2018 theloop Inc.
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
 *
 */

package foundation.icon.icx.transport.jsonrpc;

import java.math.BigInteger;

/**
 * RpcValue contains a leaf value such as string, bytes, integer, boolean
 */
public class RpcValue implements RpcField {
    private static final String HEX_PREFIX = "0x";

    private final static char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    private String value;

    public RpcValue(String value) {
        this.value = value;
    }

    public RpcValue(byte[] value) {
        this.value = toHexString(value, true);
    }

    public RpcValue(BigInteger value) {
        this.value = toHexString(value, true);
    }

    public RpcValue(Boolean value) {
        this.value = toHexString(value, true);
    }

    /**
     * Returns the value as string
     *
     * @return the value as string
     */
    public String asString() {
        return value;
    }

    /**
     * Returns the value as bytes
     *
     * @return the value as bytes
     */
    public byte[] asBytes() {
        if (!value.startsWith(HEX_PREFIX)) {
            throw new RpcValueException("The value is not hex string.");
        }

        // bytes should be even length of hex string
        if (value.length() % 2 != 0) {
            throw new RpcValueException(
                    "The hex value is not bytes format.");
        }

        String body = value.substring(2);

        byte[] bytes = new byte[body.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(
                    body.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    /**
     * Returns the value as integer
     *
     * @return the value as integer
     */
    public BigInteger asInteger() {
        String body;
        int indexOfPrefix = value.lastIndexOf(HEX_PREFIX);
        if (indexOfPrefix == 0) {
            body = value.substring(2);
        } else if (indexOfPrefix == 1 && value.charAt(0) == '-') {
            body = value.substring(0, 1) + value.substring(3);
        } else {
            throw new RpcValueException("The value is not hex string.");
        }
        try {
            return new BigInteger(body, 16);
        } catch (NumberFormatException e) {
            throw new RpcValueException("The value is not hex string.");
        }
    }

    /**
     * Returns the value as boolean
     *
     * @return the value as boolean
     */
    public boolean asBoolean() {
        switch (value) {
            case "0x0":
                return false;
            case "0x1":
                return true;
            default:
                throw new RpcValueException("The value is not boolean format.");
        }
    }

    @Override
    public String toString() {
        return "RpcValue(" +
                "value=" + value +
                ')';
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
     * Convert byte array to hex string
     *
     * @param value      a BigInteger value to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(BigInteger value, boolean withPrefix) {
        if (withPrefix) return "0x" + value.toString(16);
        else return value.toString(16);
    }

    /**
     * Convert byte array to hex string
     *
     * @param value      a boolean value to convert
     * @param withPrefix whether '0x' prefix is included to the output
     * @return hex string
     */
    public static String toHexString(boolean value, boolean withPrefix) {
        if (withPrefix) return value ? "0x1" : "0x0";
        else return value ? "1" : "0";
    }

    public static class RpcValueException extends IllegalArgumentException {
        RpcValueException(String message) {
            super(message);
        }
    }
}
