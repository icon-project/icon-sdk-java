/*
 * Copyright 2018 ICON Foundation
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

package foundation.icon.icx;

import foundation.icon.icx.crypto.IconKeys;
import foundation.icon.icx.crypto.KeyStoreUtils;
import foundation.icon.icx.crypto.Keystore;
import foundation.icon.icx.crypto.KeystoreFile;
import foundation.icon.icx.data.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * An implementation of Wallet which uses of the key pair.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class KeyWallet implements Wallet {

    private ECKeyPair ecKeyPair;

    KeyWallet(ECKeyPair ecKeyPair) {
        this.ecKeyPair = ecKeyPair;
    }

    /**
     * @see Wallet#getAddress()
     */
    @Override
    public Address getAddress() {
        return IconKeys.getAddress(ecKeyPair);
    }

    /**
     * @see Wallet#signMessage(byte[])
     */
    @Override
    public byte[] signMessage(byte[] hash) {
        return signMessage(hash, ecKeyPair);
    }

    ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    private byte[] signMessage(byte[] bHash, ECKeyPair ecKeyPair) {
        Sign.SignatureData data = Sign.signMessage(bHash, ecKeyPair, false);

        ByteBuffer buffer = ByteBuffer.allocate(data.getR().length + data.getS().length + 1);
        buffer.put(data.getR());
        buffer.put(data.getS());
        buffer.put((byte) (data.getV() - 27));
        return buffer.array();
    }

    /**
     * Creates a new KeyWallet with generating a new key pair.
     *
     * @return new KeyWallet
     */
    public static KeyWallet create() throws
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = IconKeys.createEcKeyPair();
        return new KeyWallet(ecKeyPair);
    }

    /**
     * Loads a key wallet from the private key
     *
     * @param privateKey the private key to load
     * @return KeyWallet
     */
    public static KeyWallet load(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        return new KeyWallet(credentials.getEcKeyPair());
    }

    /**
     * Loads a key wallet from the KeyStore file
     *
     * @param password the password of KeyStore
     * @param file     the KeyStore file
     * @return KeyWallet
     */
    public static KeyWallet load(String password, File file) throws IOException, CipherException {
        Credentials credentials = KeyStoreUtils.loadCredentials(password, file);
        return new KeyWallet(credentials.getEcKeyPair());
    }

    /**
     * Stores the KeyWallet as a KeyStore
     *
     * @param wallet               the wallet to store
     * @param password             the password of KeyStore
     * @param destinationDirectory the KeyStore file is stored at.
     * @return name of the KeyStore file
     */
    public static String store(KeyWallet wallet, String password, File destinationDirectory) throws
            CipherException, IOException {
        KeystoreFile keystoreFile = Keystore.createLight(password, wallet.getEcKeyPair());
        return KeyStoreUtils.generateWalletFile(keystoreFile, destinationDirectory);
    }

}
