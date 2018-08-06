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

import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;

public class SendMessageTransaction {

    public final String URL = "http://localhost:9000/api/v3";
    public final String PRIVATE_KEY_STRING =
            "2d42994b2f7735bbc93a3e64381864d06747e574aa94655c516f9ad0a74eed79";

    private IconService iconService;
    private Wallet wallet;

    public SendMessageTransaction() {
        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggning)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
        wallet = KeyWallet.load(PRIVATE_KEY_STRING);
    }

    public void sendTransaction() throws IOException {
        BigInteger networkId = new BigInteger("3");
        String fromAddress = wallet.getAddress().asString();
        String toAddress = "hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";
        BigInteger stepLimit = new BigInteger("75000");
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger nonce = new BigInteger("1");
        String message = "Hello World";

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(fromAddress)
                .to(toAddress)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(nonce)
                .message(message)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        String hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash:"+hash);
    }

    public static void main(String[] args) throws IOException {
        new SendMessageTransaction().sendTransaction();
    }
}
