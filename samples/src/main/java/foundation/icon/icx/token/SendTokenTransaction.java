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

package foundation.icon.icx.token;

import foundation.icon.icx.*;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class SendTokenTransaction {

    private IconService iconService;
    private Wallet wallet;

    public SendTokenTransaction() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
        wallet = KeyWallet.load(Constants.privateKey);
    }

    public void sendTransaction(Address scoreAddress) throws IOException {
        BigInteger networkId = new BigInteger("3");
        Address fromAddress = wallet.getAddress();
        Address toAddress = Constants.testAddress1;
        BigInteger value = IconAmount.of("1", 18).toLoop();
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger nonce = new BigInteger("1");
        String methodName = "transfer";

        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_value", new RpcValue(value))
                .build();

        // make a raw transaction without the stepLimit
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(fromAddress)
                .to(scoreAddress)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(nonce)
                .call(methodName)
                .params(params)
                .build();

        // get an estimated step value
        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

        // make a signed transaction with the same raw transaction and the estimated step
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash: " + hash);
        TransactionResult result = Utils.getTransactionResult(iconService, hash);
        System.out.println("status: " + result.getStatus());
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new DeploySampleToken().sendTransaction();
        if (BigInteger.ONE.equals(result.getStatus())) {
            Address scoreAddress = new Address(result.getScoreAddress());
            new SendTokenTransaction().sendTransaction(scoreAddress);
        } else {
            System.out.println("Deploy failed!");
        }
    }
}
