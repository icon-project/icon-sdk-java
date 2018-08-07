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
 */

package foundation.icon.icx.crypto;


import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.AddressPrefix;
import foundation.icon.icx.data.Hex;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static org.web3j.crypto.Keys.PUBLIC_KEY_LENGTH_IN_HEX;

/**
 * Implementation from
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Keys.java
 * Crypto key utilities.
 */
public class IconKeys {

    public static ECKeyPair createEcKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = Keys.createSecp256k1KeyPair();
        return ECKeyPair.create(keyPair);
    }

    public static Address getAddress(ECKeyPair ecKeyPair) {
        return new Address.Builder()
                .prefix(AddressPrefix.EOA)
                .body(getAddressHash(ecKeyPair.getPublicKey()))
                .build();
    }

    public static byte[] getAddressHash(BigInteger publicKey) {
        return getAddressHash(Hex.toHexStringZeroPadded(publicKey, PUBLIC_KEY_LENGTH_IN_HEX, true));
    }

    public static byte[] getAddressHash(String publicKey) {
        String publicKeyNoPrefix = cleanHexPrefix(publicKey);
        return getAddressHash(Hex.hexStringToByteArray(publicKeyNoPrefix));
//        return "hx" + RpcValue.toHexString(b, false);
    }

    public static byte[] getAddressHash(byte[] publicKey) {
        byte[] hash = new SHA3.Digest256().digest(publicKey);

        int length = 20;
        byte[] result = new byte[20];
        System.arraycopy(hash, hash.length - 20, result, 0, length);
        return result;
    }

    public static boolean isValidAddress(Address input) {
        return isValidAddress(input.asString());
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = cleanHexPrefix(input);
        try {
            cleanInput.matches("^[0-9a-fA-F]{40}$");
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return getAddressHexPrefix(input) != null;
    }

    public static AddressPrefix getAddressHexPrefix(String input) {
        return AddressPrefix.fromString(input.substring(0, 2));
    }

    public static byte[] getHexAddress(String input) {
        String cleanInput = cleanHexPrefix(input);
        return Hex.hexStringToByteArray(cleanInput);
    }

    public static String getHexAddress(byte[] input) {
        return Hex.toHexString(input, false);
    }

}
