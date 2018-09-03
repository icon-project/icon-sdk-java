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

import foundation.icon.icx.data.*;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;

public class IcxTransactionExample {

    private IconService iconService;
    private Wallet wallet;
    private Address toAddress;
    private Timer timer = new Timer();
    private long terminatedTime = 5 * 1000L;
    private boolean isRunningCheckResult = false;

    public IcxTransactionExample() {
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

    public static void main(String[] args) {
        IcxTransactionExample example = new IcxTransactionExample();
        // Loads a wallet from bytes of the private key
        Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));

        // Address to receive icx
        Address toAddress = new Address(CommonData.ADDRESS_1);
        // The amount of ICX to be sent (Convert unit : 1 ICX -> 1000000000000000000)
        BigInteger value = IconAmount.of("1", IconAmount.Unit.ICX).toLoop();


        try {
            // Create request object to send transaction.
            Request<Bytes> request = example.sendTransaction(wallet, toAddress, value);

            // Print balances for EOA before sending
            example.printBalance();

            // Synchronized request execution
            Bytes txHash = request.execute();
            System.out.println("######### sendTransaction #########");
            System.out.println(String.format("from:%s, to:%s, icx amount:%s, txHash : %s",
                    wallet.getAddress(), toAddress, value, txHash));

            // Check the transaction result requested by transaction hash
            example.checkResult(txHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request<Bytes> sendTransaction(Wallet wallet, Address toAddress, BigInteger value) {
        this.wallet = wallet;
        this.toAddress = toAddress;

        // networkId of node 1:mainnet, 2:testnet, 3~:private id
        BigInteger networkId = new BigInteger("3");
        // Recommended step limit to transfer icx: 10000
        BigInteger stepLimit = new BigInteger("1000000");
        // Transaction creation time (timestamp is in the microsecond)
        long timestamp = System.currentTimeMillis() * 1000L;

        // Create transaction to transfer icx
        Transaction transaction = TransactionBuilder.of(networkId)
                .from(wallet.getAddress())
                .to(toAddress)
                .value(value)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
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
                System.out.println("transaction:" + result);

                // Print balances for EOA after sending
                printBalance();

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

    void printBalance() throws IOException {
        BigInteger fromBalance = iconService.getBalance(wallet.getAddress()).execute();
        BigInteger toBalance = iconService.getBalance(toAddress).execute();
        System.out.println("######### print icx balance #########");
        System.out.println(String.format("from:%s, balance:%s", wallet.getAddress(), fromBalance));
        System.out.println(String.format("to:%s, balance:%s", toAddress, toBalance));
    }
}
