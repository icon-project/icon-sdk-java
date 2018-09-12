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

import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.CommonData;
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
import java.util.Timer;
import java.util.TimerTask;

public class DeployTokenExample {

    private IconService iconService;
    private Timer timer = new Timer();
    private long terminatedTime = 60 * 1000L;
    private boolean isRunningCheckResult = false;

    public DeployTokenExample() {
        // Logs HTTP request and response data
        // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
//				.addInterceptor(loggning)
                .build();

        // Creates an instance of IconService using the HTTP provider
        iconService = new IconService(new HttpProvider(httpClient, CommonData.URL));
    }

    public static void main(String[] args) throws IOException {
        DeployTokenExample example = new DeployTokenExample();
        // Loads a wallet from bytes of the private key
        Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));

        // Read binary data of the SCORE
        byte[] content = example.readFile();

        // Enter information about tokens to deploy
        BigInteger initialSupply = new BigInteger("100000000000");
        BigInteger decimals = new BigInteger("18");
        String tokenName = "StandardToken";
        String tokenSymbol = "ST";

        try {
            // Create request object to send transaction.
            Request<Bytes> request = example.sendTransaction(wallet, content,
                    initialSupply, decimals, tokenName, tokenSymbol);

            // Synchronized request execution
            Bytes txHash = request.execute();
            System.out.println("######### sendTransaction #########");
            System.out.println(String.format("Deploy Token name:%s(%s), creator:%s, txHash:%s",
                    tokenName, tokenSymbol, wallet.getAddress(), txHash));

            // Check the transaction result requested by transaction hash
            example.checkResult(txHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request<Bytes> sendTransaction(Wallet wallet, byte[] content, BigInteger initialSupply,
                                          BigInteger decimals, String tokenName, String tokenSymbol) {

        // networkId of node 1:mainnet, 2:testnet, 3~:private id
        BigInteger networkId = new BigInteger("3");
        // Maximum step allowance that can be used by the transaction
        BigInteger stepLimit = new BigInteger("2013265920");
        // Transaction creation time (timestamp is in the microsecond)
        long timestamp = System.currentTimeMillis() * 1000L;
        // Content's mime-type
        String contentType = "application/zip";

        // Convert information to object for the request.
        RpcObject params = new RpcObject.Builder()
                .put("initialSupply", new RpcValue(initialSupply))
                .put("decimals", new RpcValue(decimals))
                .put("name", new RpcValue(tokenName))
                .put("symbol", new RpcValue(tokenSymbol))
                .build();

        // Create transaction to deploy token
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(wallet.getAddress())
                .to(CommonData.SCORE_INSTALL_ADDRESS)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .deploy(contentType, content)
                .params(params)
                .build();

        // Create signedTransaction for signature of transaction
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        return iconService.sendTransaction(signedTransaction);
    }

    public void checkResult(Bytes hash) {
        // Set timer to abort operation after {terminatedTime}
        startTimer();
        isRunningCheckResult = true;
        System.out.println("######### check Result #########");
        while (isRunningCheckResult) {

            try {
                // Get the transaction result with a transaction hash.
                TransactionResult result = iconService.getTransactionResult(hash).execute();
                System.out.println("confirm transaction txHash:" + hash);
                System.out.println("transaction status(1:success, 0:failure):" + result.getStatus());
                System.out.println("created score address:" + result.getScoreAddress());
                System.out.println("waiting accept score...");
                System.out.println("transaction result:" + result);

                break;
            } catch (Exception e) {
                // If execute as synchronized, occur exception when transaction is pending
                System.out.println("pending Transaction.....");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timer.cancel();
        System.out.println("######### end #########");
    }

    void startTimer() {
        stopTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isRunningCheckResult = false;
            }
        }, terminatedTime);
    }

    void stopTimer() {
        timer.cancel();
        timer = new Timer();
    }

    private byte[] readFile() throws IOException {
        // sample token : resource/test.zi
        File file = new File(getClass().getClassLoader().getResource("test.zi").getFile());
        return readBytes(file);
    }

    private byte[] readBytes(File file) throws IOException {
        int length = (int) file.length();
        if (length > Integer.MAX_VALUE) throw new OutOfMemoryError("File is too big!!");
        byte[] result = new byte[length];
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
        inputStream.readFully(result);
        return result;
    }
}
