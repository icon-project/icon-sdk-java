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
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstimateStepTest {

    private IconService iconService;
    private TransactionHandler txHandler;
    private KeyWallet owner;
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
        txHandler = new TransactionHandler(iconService);
        owner = KeyWallet.load(Constants.PRIVATE_KEY);

        fromAddress = owner.getAddress();
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

    @Test
    void testWithLegacyProvider() {
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(fromAddress)
                .to(toAddress)
                .value(new BigInteger("de0b6b3a7640000", 16))
                .nonce(BigInteger.valueOf(1))
                .build();

        IconService iconService = new IconService(new HttpProvider("http://localhost:9000/api/v3"));
        assertThrows(UnsupportedOperationException.class, () -> {
            iconService.estimateStep(transaction).execute();
        });
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
            new SignedTransaction(transaction, owner);
        });
        assertDoesNotThrow(() -> {
            new SignedTransaction(transaction, owner, estimatedStep);
        });
        SignedTransaction signedTransaction = new SignedTransaction(transaction, owner, estimatedStep);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());

        Transaction transaction2 = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(fromAddress)
                .to(toAddress)
                .stepLimit(BigInteger.valueOf(200000))
                .value(new BigInteger("de0b6b3a7640000", 16))
                .nonce(BigInteger.ONE)
                .build();

        signedTransaction = new SignedTransaction(transaction2, owner, estimatedStep);
        RpcObject properties = signedTransaction.getProperties();
        assertEquals(estimatedStep, properties.getItem("stepLimit").asInteger());
        txHash = iconService.sendTransaction(signedTransaction).execute();
        result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
    }

    @Disabled
    @Test
    void testDeploy() throws IOException {
        // deploy sample token
        RpcObject params = new RpcObject.Builder()
                .put("_initialSupply", new RpcValue(new BigInteger("1000")))
                .put("_decimals", new RpcValue(new BigInteger("18")))
                .build();
        Bytes txHash = txHandler.install(owner, "sampleToken.zip", params);
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
    }
}
