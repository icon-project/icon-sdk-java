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


import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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

    public static ECKeyPair createEcKeyPair(String privateKey) {
        return ECKeyPair.create(Numeric.toBigInt(privateKey));
    }

    public static String getAddress(ECKeyPair ecKeyPair) {
        return getAddress(ecKeyPair.getPublicKey());
    }

    public static String getAddress(BigInteger publicKey) {
        return getAddress(Numeric.toHexStringWithPrefixZeroPadded(publicKey, PUBLIC_KEY_LENGTH_IN_HEX));
    }

    public static String getAddress(String publicKey) {
        String publicKeyNoPrefix = Numeric.cleanHexPrefix(publicKey);
        byte[] b = getAddress(Numeric.hexStringToByteArray(publicKeyNoPrefix));
        return "hx" + Numeric.toHexString(b, 0, b.length, false);
    }

    public static byte[] getAddress(byte[] publicKey) {
        byte[] hash = new SHA3.Digest256().digest(publicKey);

        int length = 20;
        byte[] result = new byte[20];
        System.arraycopy(hash, hash.length - 20, result, 0, length);
        return result;
    }

    public static boolean isValidAddress(String address) {
        return address.matches("^hx[0-9a-fA-F]{40}$");
    }
}
