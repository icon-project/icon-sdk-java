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

package foundation.icon.icx;

import foundation.icon.icx.crypto.KeystoreException;
import foundation.icon.icx.data.Bytes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class GenerateWallet {

    private String PRIVATE_KEY_STRING =
            "2d42994b2f7735bbc93a3e64381864d06747e574aa94655c516f9ad0a74eed79";
    private String PASSWORD = "Pa55w0rd";
    private File tempDir;

    public GenerateWallet() throws IOException {
        tempDir = Files.createTempDirectory(
                "testkeys").toFile();
    }

    public KeyWallet create() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return KeyWallet.create();
    }

    public KeyWallet loadPrivateKey() {
        return KeyWallet.load(new Bytes(PRIVATE_KEY_STRING));
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, KeystoreException {
        GenerateWallet sample = new GenerateWallet();

        KeyWallet wallet = sample.create();
        System.out.println("address:"+wallet.getAddress());
        System.out.println("privateKey:"+wallet.getPrivateKey().toHexString(false));

        wallet = sample.loadPrivateKey();
        System.out.println("address:"+wallet.getAddress());
        System.out.println("privateKey:"+wallet.getPrivateKey().toHexString(false));

        String fileName = sample.storeKeyStore(wallet);
        System.out.println("keystore fileName:"+fileName);

        wallet = sample.loadKeyStore(fileName);
        System.out.println("address:"+wallet.getAddress());
        System.out.println("privateKey:"+wallet.getPrivateKey().toHexString(false));
    }

    public String storeKeyStore(KeyWallet wallet) throws KeystoreException, IOException {
        return KeyWallet.store(wallet, PASSWORD, tempDir);
    }

    public KeyWallet loadKeyStore(String fileName) throws IOException, KeystoreException {
        File file = new File(tempDir, fileName);
        return KeyWallet.load(PASSWORD, file);
    }

}

