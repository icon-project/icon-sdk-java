/*
 * Copyright 2019 ICON Foundation
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

import foundation.icon.icx.data.*;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcError;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstimateStepTest {

    private IconService iconService;
    private KeyWallet godWallet;
    private Address fromAddress;
    private Address toAddress;

    @BeforeEach
    void init() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, "http://localhost:9000", 3));
        godWallet = KeyWallet.load(new Bytes("592eb276d534e2c41a2d9356c0ab262dc233d87e4dd71ce705ec130a8d27ff0c"));

        fromAddress = godWallet.getAddress();
        toAddress = new Address("hxca1b18d749e4339e9661061af7e1e6cabcef8a19");
    }

    @Test
    void testGetMethod() {
        Provider provider = mock(Provider.class);
        Address fromAddress = new Address("hxe7af5fcfd8dfc67530a01a0e403882687528dfcb");
        Address toAddress = new Address("hxca1b18d749e4339e9661061af7e1e6cabcef8a19");

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(NetworkId.MAIN)
                .from(fromAddress)
                .to(toAddress)
                .value(new BigInteger("de0b6b3a7640000", 16))
                .timestamp(new BigInteger("563a6cf330136", 16))
                .nonce(new BigInteger("1"))
                .build();

        IconService iconService = new IconService(provider);
        iconService.estimateStep(transaction);
        verify(provider).request(
                argThat(request -> request.getMethod().equals("debug_estimateStep")),
                argThat(converter -> converter.equals(Converters.BIG_INTEGER)));
    }

    @Disabled
    @Test
    void testSimpleTransfer() throws IOException {
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(fromAddress)
                .to(toAddress)
                .value(new BigInteger("de0b6b3a7640000", 16))
                .nonce(BigInteger.valueOf(1))
                .build();

        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();
        assertEquals(new BigInteger("100000"), estimatedStep);

        assertThrows(IllegalArgumentException.class, () -> {
            new SignedTransaction(transaction, godWallet);
        });
        assertDoesNotThrow(() -> {
            new SignedTransaction(transaction, godWallet, estimatedStep);
        });
        SignedTransaction signedTransaction = new SignedTransaction(transaction, godWallet, estimatedStep);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = getTransactionResult(iconService, txHash);
        assertEquals(BigInteger.ONE, result.getStatus());

        Transaction transaction2 = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(fromAddress)
                .to(toAddress)
                .stepLimit(BigInteger.valueOf(200000))
                .value(new BigInteger("de0b6b3a7640000", 16))
                .nonce(BigInteger.ONE)
                .build();

        signedTransaction = new SignedTransaction(transaction2, godWallet, estimatedStep);
        RpcObject properties = signedTransaction.getProperties();
        assertEquals(estimatedStep, properties.getItem("stepLimit").asInteger());
        txHash = iconService.sendTransaction(signedTransaction).execute();
        result = getTransactionResult(iconService, txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
    }

    @Disabled
    @Test
    void testDeploy() throws IOException {
        // deploy sample token
        byte[] content = readFile("sampleToken.zip");
        RpcObject params = new RpcObject.Builder()
                .put("_initialSupply", new RpcValue(new BigInteger("1000")))
                .put("_decimals", new RpcValue(new BigInteger("18")))
                .build();
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(fromAddress)
                .to(new Address("cx0000000000000000000000000000000000000000"))
                .deploy("application/zip", content)
                .params(params)
                .build();

        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();
        BigInteger margin = BigInteger.valueOf(10000);

        SignedTransaction signedTransaction = new SignedTransaction(transaction, godWallet, estimatedStep.add(margin));
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = getTransactionResult(iconService, txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
    }

    private byte[] readFile(String name) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(name).getFile());
        return readBytes(file);
    }

    private byte[] readBytes(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) throw new OutOfMemoryError("File is too big!!");
        byte[] result = new byte[(int) length];
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {
            inputStream.readFully(result);
        }
        return result;
    }

    static TransactionResult getTransactionResult(IconService iconService, Bytes txHash) throws IOException {
        TransactionResult result = null;
        while (result == null) {
            try {
                result = iconService.getTransactionResult(txHash).execute();
            } catch (RpcError e) {
                System.out.println("RpcError: code: " + e.getCode() + ", message: " + e.getMessage());
                try {
                    // wait until block confirmation
                    System.out.println("Sleep 1 second.");
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        return result;
    }
}
