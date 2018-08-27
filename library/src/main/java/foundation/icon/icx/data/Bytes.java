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

import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * A wrapper class of byte array
 */
public class Bytes {
    private byte[] data;

    /**
     * Creates an instance using hex string
     *
     * @param hexString hex string of bytes
     */
    public Bytes(String hexString) {
        if (!isValidHex(hexString))
            throw new IllegalArgumentException("The value is not hex string.");
        this.data = Numeric.hexStringToByteArray(hexString);
    }

    /**
     * Creates an instance using byte array
     *
     * @param data byte array to wrap
     */
    public Bytes(byte[] data) {
        this.data = data;
    }

    public Bytes(BigInteger value) {
        this.data = value.toByteArray();
    }

    /**
     * Gets the data as a byte array
     *
     * @return byte array
     */
    public byte[] toByteArray() {
        return data;
    }

    /**
     * Gets the data as a byte array given size
     *
     * @return byte array given size
     */
    public byte[] toByteArray(int size) {
        return Numeric.toBytesPadded(new BigInteger(data), size);
    }

    /**
     * Gets the data as a hex string
     *
     * @param withPrefix whether 0x prefix included
     * @return hex string
     */
    public String toHexString(boolean withPrefix) {
        return toHexString(withPrefix, data.length);
    }

    /**
     * Gets the data as a hex string given size
     *
     * @param withPrefix whether 0x prefix included
     * @return hex string given size
     */
    public String toHexString(boolean withPrefix, int size) {
        String result = Numeric.toHexStringNoPrefix(data);
        int length = result.length();
        if (length < size) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size - length; i++) {
                sb.append('0');
            }
            result = sb.append(result).toString();
        }

        if (withPrefix) {
            return "0x" + result;
        } else {
            return result;
        }
    }

    @Override
    public String toString() {
        return toHexString(true, data.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Bytes) {
            return Arrays.equals(((Bytes) obj).data, data);
        }
        return false;
    }

    private boolean isValidHex(String value) {
        String v = Numeric.cleanHexPrefix(value);
        return v.matches("^[0-9a-fA-F]+$");
    }
}
