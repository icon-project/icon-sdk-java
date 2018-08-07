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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.icon.icx.crypto.IconKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.CipherException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static foundation.icon.icx.SampleKeys.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyWalletTest {

    public File tempDir;

    private static File createTempDir() throws Exception {
        return Files.createTempDirectory(
                "testkeys").toFile();
    }

    @BeforeEach
    void setUp() throws Exception {
        tempDir = createTempDir();
    }

    @Test
    public void testLoadWithPrivateKey() {
        KeyWallet wallet = KeyWallet.load(PRIVATE_KEY_STRING);
        assertEquals(ADDRESS, wallet.getAddress().asString());
    }

    @Test
    public void testCreate() {
        KeyWallet wallet = KeyWallet.create();
        assertTrue(IconKeys.isValidAddress(wallet.getAddress().asString()));
        Wallet loadWallet = KeyWallet.load(wallet.getEcKeyPair().getPrivateKey().toString(16));
        assertEquals(wallet.getAddress().asString(), loadWallet.getAddress().asString());
    }

    @Test
    public void testKeyStore() throws CipherException, IOException {
        KeyWallet wallet = KeyWallet.load(PRIVATE_KEY_STRING);
        String fileName = KeyWallet.store(wallet, PASSWORD, tempDir);
        Matcher matcher = Pattern.compile("hx[0-9a-fA-F]{40}").matcher(fileName);
        if (matcher.find()) {
            assertEquals(wallet.getAddress().asString(), matcher.group());
        }
    }

    @Test
    public void testLoadKeyStore() throws IOException, CipherException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"address\":\"hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31\",\"id\":\"764c8ba3-cd89-47d0-adac-d02fec33d347\",\"coinType\":\"icx\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"5ebdd58daf9bc3c009d2895bc4ca56c7766dd1b10cb1b2da7373b8a719210522\",\"cipherparams\":{\"iv\":\"d076e8a0db81fdfcbbd2c09232bdd8d4\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"4379292775301f9139224c3c8456bb4cf5ae8525abac3a9fb96f1cbce7cb80f7\"},\"mac\":\"9ab4daa90ce8dfeb10503969bce3f4ab58acfca2f31b302fa2ed55848f6ec441\"}}";
        File file = new File(tempDir, "keystore.json");

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> map = mapper.readValue(json, typeReference);
        mapper.writeValue(file, map);

        Wallet wallet = KeyWallet.load(PASSWORD, file);
        assertEquals("hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31", wallet.getAddress().asString());
    }

    @Test
    public void testLoadEtherKeyStore() throws IOException, CipherException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"version\":3,\"id\":\"99208193-bc50-4733-8405-683b540023f7\",\"address\":\"278a5fabbd428e5f80e2119e8764960528f408e8\",\"Crypto\":{\"ciphertext\":\"f9c7c61029560dbf8983206af11e6f635fdcdd6b22ad69bdd4c7aa8c7c5bb6d4\",\"cipherparams\":{\"iv\":\"61991abea9255f9c5902a6867c01ffe6\"},\"cipher\":\"aes-128-ctr\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"salt\":\"856b7e16ce3145793493df4a2086f17ee183c73bc17da35a09d2d79098a94fff\",\"n\":8192,\"r\":8,\"p\":1},\"mac\":\"205b94b85d661dc0173307b6b214534a121533d88cdcc1d93b2a366dd42ba589\"}}";
        File file = new File(tempDir, "keystore.json");

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> map = mapper.readValue(json, typeReference);
        mapper.writeValue(file, map);

        assertThrows(InputMismatchException.class, ()-> {
            KeyWallet.load(PASSWORD, file);
        });
    }

}
