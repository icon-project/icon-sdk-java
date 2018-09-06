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
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncBlockExample {

    private IconService iconService;
    private Timer timer = new Timer();
    private long terminatedTime = 30 * 1000L;
    private boolean isRunningCheckResult = false;
    private BigInteger syncedBlockHeight;

    public SyncBlockExample() {
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
        SyncBlockExample example = new SyncBlockExample();
        example.observableBlock();
    }

    void observableBlock() {
        if (!isRunningCheckResult) {
            System.out.println("######### block observable start #########");
            // Set timer to abort operation after {terminatedTime}
            startTimer();
            isRunningCheckResult = true;
            while (isRunningCheckResult) {
                try {
                    // Get the last block information
                    Block block = iconService.getLastBlock().execute();
                    BigInteger currentHeight = block.getHeight();
                    if (syncedBlockHeight == null || currentHeight.compareTo(syncedBlockHeight) > 0) {
                        System.out.println("######### Sync Block #########");
                        if (syncedBlockHeight == null) syncedBlockHeight = currentHeight;

                        // Print transaction list of block.
                        for (BigInteger b = syncedBlockHeight; b.compareTo(currentHeight) < 0; b = b.add(BigInteger.ONE)) {
                            syncBlock(iconService.getBlock(b).execute());
                        }

                        // Print transaction list of block. (To use return object of "getLastBlock")
                        syncBlock(block);
                    } else {
                        // There is no block creation
                        System.out.println(String.format("Synced block height:%s, Last block height:%s", syncedBlockHeight, currentHeight));
                    }

                    Thread.sleep(5000);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("######### block observable end #########");
        }
    }

    void syncBlock(Block block) throws IOException {
        System.out.println("block height:" + block.getHeight());

        // the transaction list of blocks
        List<ConfirmedTransaction> txList = block.getTransactions();
        for (ConfirmedTransaction transaction : txList) {
            System.out.println("### transaction hash:" + transaction.getTxHash());

            boolean isPrintLog = false;
            TransactionResult txResult = iconService.getTransactionResult(transaction.getTxHash()).execute();

            // Print icx transaction
            if ((transaction.getValue() != null) &&
                    (transaction.getValue().compareTo(BigInteger.ZERO) > 0)) {
                isPrintLog = true;
                System.out.println("[Icx] status:" + txResult.getStatus() +
                        ",from:" + transaction.getFrom() + ",to:" + transaction.getTo() + ",amount:" + transaction.getValue());
            }

            // Print token transaction
            if (transaction.getDataType() != null && transaction.getDataType().equals("call")) {
                RpcObject data = transaction.getData().asObject();
                String methodName = data.getItem("method").asString();

                if (methodName != null && methodName.equals("transfer")) {
                    isPrintLog = true;

                    // Get value and address from parameter of data field
                    RpcObject params = data.getItem("params").asObject();
                    BigInteger value = params.getItem("_value").asInteger();
                    Address toAddr = params.getItem("_to").asAddress();

                    // Get the name of token
                    String tokenName = getTokenName(transaction.getTo());
                    // Get the symbol of token
                    String symbol = getTokenSymbol(transaction.getTo());
                    String token = String.format("[%s Token(%s)]", tokenName, symbol);
                    System.out.println(token + ",tokenAddress:" + transaction.getTo() + ",status:" + txResult.getStatus() +
                            ",from:" + transaction.getFrom() + ",to:" + toAddr + ",amount:" + value);
                }
            }

            // Pring event log
            if (isPrintLog) {
                List<TransactionResult.EventLog> logs = txResult.getEventLogs();
                for (TransactionResult.EventLog log : logs) {
                    System.out.println("[EventLogs] scoreAddress:" + log.getScoreAddress() + ",indexed:" + log.getIndexed() + " ,data:" + log.getData());
                }
            }
        }
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

}
