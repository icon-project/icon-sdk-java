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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;


/**
 * Original Code
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/WalletUtils.java
 * Utility functions for working with Keystore files.
 */
public class KeyStoreUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static String generateWalletFile(
            KeystoreFile file, File destinationDirectory) throws IOException {

        String fileName = getWalletFileName(file);
        File destination = new File(destinationDirectory, fileName);
        objectMapper.writeValue(destination, file);
        return fileName;
    }

    public static Credentials loadCredentials(String password, File source)
            throws IOException, CipherException {
        ObjectMapper mapper = new ObjectMapper();
        KeystoreFile keystoreFile = mapper.readValue(source, KeystoreFile.class);
        if (keystoreFile.getCoinType() == null || !keystoreFile.getCoinType().equalsIgnoreCase("icx"))
            throw new InputMismatchException("Invalid Keystore file");
        return Credentials.create(Keystore.decrypt(password, keystoreFile));
    }

    private static String getWalletFileName(KeystoreFile keystoreFile) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(
                "'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        return now.format(format) + keystoreFile.getAddress() + ".json";
    }

}
