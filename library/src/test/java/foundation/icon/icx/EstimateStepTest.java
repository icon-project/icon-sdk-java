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

import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.Converters;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EstimateStepTest {
    private static final BigInteger ICX = BigInteger.TEN.pow(18);

    private IconService iconService;
    private TransactionHandler txHandler;
    private KeyWallet owner;

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
    }

    @Test
    void testGetMethod() throws Exception {
        Provider provider = mock(Provider.class);
        Wallet from = KeyWallet.create();
        Wallet to = KeyWallet.create();

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(from.getAddress())
                .to(to.getAddress())
                .value(ICX)
                .build();

        IconService iconService = new IconService(provider);
        iconService.estimateStep(transaction);
        verify(provider).request(
                argThat(request -> request.getMethod().equals("debug_estimateStep")),
                argThat(converter -> converter.equals(Converters.BIG_INTEGER)));
    }

    @Test
    void testWithLegacyProvider() throws Exception {
        Wallet from = KeyWallet.create();
        Wallet to = KeyWallet.create();

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(from.getAddress())
                .to(to.getAddress())
                .value(ICX)
                .build();

        IconService iconService = new IconService(new HttpProvider("http://localhost:9000/api/v3"));
        assertThrows(UnsupportedOperationException.class, () -> {
            iconService.estimateStep(transaction).execute();
        });
    }

    @Tag("integration")
    @Test
    void testSimpleTransfer() throws Exception {
        Wallet to = KeyWallet.create();

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(owner.getAddress())
                .to(to.getAddress())
                .value(ICX)
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
        assertEquals(estimatedStep, result.getStepUsed());

        Transaction transaction2 = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(owner.getAddress())
                .to(to.getAddress())
                .stepLimit(estimatedStep)
                .value(ICX)
                .build();

        // this should override the existing stepLimit
        BigInteger customStep = BigInteger.valueOf(200000);
        signedTransaction = new SignedTransaction(transaction2, owner, customStep);
        RpcObject properties = signedTransaction.getProperties();
        assertEquals(customStep, properties.getItem("stepLimit").asInteger());
        txHash = iconService.sendTransaction(signedTransaction).execute();
        result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
        // the actual stepUsed should still be the estimatedStep
        assertEquals(estimatedStep, result.getStepUsed());
    }

    @Tag("integration")
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
