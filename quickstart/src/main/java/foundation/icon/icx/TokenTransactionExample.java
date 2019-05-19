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
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;

public class TokenTransactionExample {

    private IconService iconService;
    private Address tokenAddress;
    private Timer timer = new Timer();
    private long terminatedTime = 5 * 1000L;
    private boolean isRunningCheckResult = false;

    private TokenTransactionExample(Address tokenAddress) {
        // Logs HTTP request and response data
        // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
//				.addInterceptor(logging)
                .build();

        // Creates an instance of IconService using the HTTP provider
        iconService = new IconService(new HttpProvider(httpClient, CommonData.SERVER_URI, 3));

        this.tokenAddress = tokenAddress;
        printTitle();
    }

    private void printTitle() {
        try {
            String tokenName = getTokenName(tokenAddress);
            String tokenSymbol = getTokenSymbol(tokenAddress);
            System.out.println(String.format("Token:%s(%s)", tokenName, tokenSymbol));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Loads a wallet from bytes of the private key
        Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
        // Deploy a token SCORE and get the SCORE address
        Address tokenAddress = new DeployTokenExample().deploy(wallet);
        // Address to receive token
        Address toAddress = new Address(CommonData.ADDRESS_1);
        // decimal of token
        int tokenDecimals = 18;
        // The amount of token to be sent (Convert unit : 1 -> 1000000000000000000)
        BigInteger value = IconAmount.of("1", tokenDecimals).toLoop();

        TokenTransactionExample example = new TokenTransactionExample(tokenAddress);

        try {
            // Create request object to send transaction.
            Request<Bytes> request = example.sendTransaction(wallet, toAddress, value);

            // Print balances for EOA before sending
            example.printBalance(wallet, toAddress);

            // Synchronized request execution
            Bytes txHash = request.execute();
            System.out.println("######### sendTransaction #########");
            System.out.println(String.format("from:%s, to:%s, icx amount:%s, txHash : %s",
                    wallet.getAddress(), toAddress, value, txHash));

            // Check the transaction result requested by transaction hash
            example.checkResult(txHash);

            // Print token balances for EOA after sending
            example.printBalance(wallet, toAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request<Bytes> sendTransaction(Wallet wallet, Address toAddress, BigInteger value) throws IOException {
        // Network ID ("1" for Mainnet, "2" for Testnet, etc)
        BigInteger networkId = new BigInteger("3");
        // Transaction creation time (timestamp is in the microsecond)
        long timestamp = System.currentTimeMillis() * 1000L;
        // 'transfer' as a methodName means to transfer token
        // https://github.com/icon-project/IIPs/blob/master/IIPS/iip-2.md
        String methodName = "transfer";

        // Convert information to object for the request.
        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_value", new RpcValue(value))
                .build();

        // Create a raw transaction to transfer token (without stepLimit)
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(wallet.getAddress())
                .to(tokenAddress)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .call(methodName)
                .params(params)
                .build();

        // Get an estimated step value
        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

        // Create a signedTransaction with the sender's wallet and the estimatedStep
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep);
        return iconService.sendTransaction(signedTransaction);
    }

    private void checkResult(Bytes hash) {
        // Set timer to abort operation after {terminatedTime}
        startTimer();
        isRunningCheckResult = true;
        System.out.println("######### check Result #########");
        while (isRunningCheckResult) {

            try {
                // Get the transaction result with a transaction hash.
                TransactionResult result = iconService.getTransactionResult(hash).execute();
                System.out.println("confirm transaction txHash:"+hash);
                System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());
                System.out.println("transaction:"+result);

                break;
            } catch (Exception e ) {
                // If execute as synchronized, occur exception when transaction is pending
                System.out.println("pending Transaction.....");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
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

    void printBalance(Wallet wallet, Address toAddress) throws IOException {
        BigInteger fromBalance = getTokenBalance(wallet.getAddress());
        BigInteger toBalance = getTokenBalance(toAddress);
        System.out.println("######### print token balance #########");
        System.out.println(String.format("from:%s, balance:%s", wallet.getAddress(), fromBalance));
        System.out.println(String.format("to:%s, balance:%s", toAddress, toBalance));
    }

    BigInteger getTokenBalance(Address address) throws IOException {
        // 'balanceOf' as a methodName means to get the balance of address
        // https://github.com/icon-project/IIPs/blob/master/IIPS/iip-2.md
        String methodName = "balanceOf";

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(address))
                .build();

        Call<RpcItem> call = new Call.Builder()
                .to(tokenAddress)
                .method(methodName)
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();
        return result.asInteger();
    }

    String getTokenName(Address tokenAddress) throws IOException {
        Call<RpcItem> call = new Call.Builder()
                .to(tokenAddress)
                .method("name")
                .build();

        RpcItem result = iconService.call(call).execute();
        return result.asString();
    }

    String getTokenSymbol(Address tokenAddress) throws IOException {
        Call<RpcItem> call = new Call.Builder()
                .to(tokenAddress)
                .method("symbol")
                .build();

        RpcItem result = iconService.call(call).execute();
        return result.asString();
    }
}
