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
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncBlockExample {
    private final int EVENT_FUNCTION_INDEX = 0;
    private final int EVENT_TRANSFER_FROM_INDEX = 1;
    private final int EVENT_TRANSFER_TO_INDEX = 2;
    private final int EVENT_TRANSFER_VALUE_INDEX = 3;

    private IconService iconService;
    private Timer timer = new Timer();
    private long terminatedTime = 30 * 1000L;
    private boolean isRunningCheckResult = false;
    private BigInteger syncedBlockHeight;

    public SyncBlockExample() {
        // Logs HTTP request and response data
        // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
//				.addInterceptor(logging)
                .build();

        // Creates an instance of IconService using the HTTP provider
        iconService = new IconService(new HttpProvider(httpClient, CommonData.URL));
    }

    public static void main(String[] args) throws IOException {
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
                        if (syncedBlockHeight == null) syncedBlockHeight = currentHeight.subtract(BigInteger.ONE);

                        // Print transaction list of block.
                        for (BigInteger b = syncedBlockHeight.add(BigInteger.ONE); b.compareTo(currentHeight) < 0; b = b.add(BigInteger.ONE)) {
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
                    break;
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

            // Check event
            List<TransactionResult.EventLog> logs = txResult.getEventLogs();
            for (TransactionResult.EventLog log : logs) {
                System.out.println("##### check Event #####");

                RpcItem function = log.getIndexed().get(EVENT_FUNCTION_INDEX);

                // Check event for token transfer
                if (function.asString().equals("Transfer(Address,Address,int,bytes)")) {
                    Address score = new Address(log.getScoreAddress());
                    Address from = log.getIndexed().get(EVENT_TRANSFER_FROM_INDEX).asAddress();
                    Address to = log.getIndexed().get(EVENT_TRANSFER_TO_INDEX).asAddress();
                    BigInteger value = log.getIndexed().get(EVENT_TRANSFER_VALUE_INDEX).asInteger();

                    // Get the name of token
                    String tokenName = getTokenName(score);
                    // Get the symbol of token
                    String symbol = getTokenSymbol(score);
                    String token = String.format("[%s Token(%s)]", tokenName, symbol);

                    System.out.println(token + ",tokenAddress:" + score + ",status:" + txResult.getStatus() +
                            ",from:" + from + ",to:" + to + ",amount:" + value);
                }
                // Check evnent for icx transfer
                else if (function.asString().equals("ICXTransfer(Address,Address,int)")) {
                    Address from = log.getIndexed().get(EVENT_TRANSFER_FROM_INDEX).asAddress();
                    Address to = log.getIndexed().get(EVENT_TRANSFER_TO_INDEX).asAddress();
                    BigInteger value = log.getIndexed().get(EVENT_TRANSFER_VALUE_INDEX).asInteger();

                    System.out.println("[Icx] status:" + txResult.getStatus() + ",from:" + from + ",to:" + to + ",amount:" + value);
                }
                // event other than those for sending icx and token
                else {
                    System.out.println("[EventLogs] scoreAddress:" + log.getScoreAddress() + ",indexed:" + log.getIndexed() + " ,data:" + log.getData());
                }
            }
        }
        syncedBlockHeight = block.getHeight();
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
