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

package foundation.icon.icx;

import foundation.icon.icx.crypto.IconKeys;
import foundation.icon.icx.crypto.KeyStoreUtils;
import foundation.icon.icx.crypto.Keystore;
import foundation.icon.icx.crypto.KeystoreFile;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

public class KeyWallet implements Wallet {

    private ECKeyPair ecKeyPair;

    public KeyWallet(ECKeyPair ecKeyPair) {
        this.ecKeyPair = ecKeyPair;
    }

    @Override
    public String getAddress() {
        return IconKeys.getAddress(ecKeyPair);
    }

    @Override
    public String signMessage(String hash) {
        return signMessage(hash, ecKeyPair);
    }

    private ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    private String signMessage(String hash, ECKeyPair ecKeyPair) {
        byte[] bHash=Numeric.hexStringToByteArray(hash);
        Sign.SignatureData data = Sign.signMessage(bHash, ecKeyPair, false);

        ByteBuffer buffer = ByteBuffer.allocate(data.getR().length + data.getS().length + 1);
        buffer.put(data.getR());
        buffer.put(data.getS());
        buffer.put((byte)(data.getV() - 27));
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    public static KeyWallet create() {
        try {
            ECKeyPair ecKeyPair = IconKeys.createEcKeyPair();
            return new KeyWallet(ecKeyPair);
        } catch (InvalidAlgorithmParameterException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static KeyWallet load(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        return new KeyWallet(credentials.getEcKeyPair());
    }

    public static String store(KeyWallet wallet, String password, File destinationDirectory) throws CipherException, IOException {
        KeystoreFile keystoreFile = Keystore.createLight(password, wallet.getEcKeyPair());
        return KeyStoreUtils.generateWalletFile(keystoreFile, destinationDirectory);
    }

    public static KeyWallet load(String password, File file) throws IOException, CipherException {
        Credentials credentials = KeyStoreUtils.loadCredentials(password, file);
        return new KeyWallet(credentials.getEcKeyPair());
    }
}
