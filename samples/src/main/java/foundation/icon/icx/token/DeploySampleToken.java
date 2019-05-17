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
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class DeploySampleToken {

    private IconService iconService;
    private Wallet wallet;

    public DeploySampleToken() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
        wallet = KeyWallet.load(Constants.privateKey);
    }

    public TransactionResult sendTransaction() throws IOException {
        String contentType = "application/zip";
        byte[] content = readFile();
        BigInteger networkId = new BigInteger("3");
        Address fromAddress = wallet.getAddress();
        Address toAddress = new Address("cx0000000000000000000000000000000000000000");
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger nonce = new BigInteger("1");

        BigInteger initialSupply = new BigInteger("10000");
        BigInteger decimals = new BigInteger("18");

        RpcObject params = new RpcObject.Builder()
                .put("_initialSupply", new RpcValue(initialSupply))
                .put("_decimals", new RpcValue(decimals))
                .build();

        // make a raw transaction without the stepLimit
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(fromAddress)
                .to(toAddress)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(nonce)
                .deploy(contentType, content)
                .params(params)
                .build();

        // get an estimated step value
        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

        // set some margin value for the operation of `on_install`
        BigInteger margin = BigInteger.valueOf(10000);

        // make a signed transaction with the same raw transaction and the estimated step
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep.add(margin));
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash: " + hash);
        TransactionResult result = Utils.getTransactionResult(iconService, hash);
        System.out.println("Status: " + result.getStatus());
        return result;
    }

    private byte[] readFile() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("sampleToken.zip").getFile());
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

    public static void main(String[] args) throws IOException {
        new DeploySampleToken().sendTransaction();
    }
}
