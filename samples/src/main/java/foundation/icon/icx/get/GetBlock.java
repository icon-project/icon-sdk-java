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

package foundation.icon.icx.get;

import foundation.icon.icx.Constants;
import foundation.icon.icx.IconService;
import foundation.icon.icx.SendIcxTransaction;
import foundation.icon.icx.Transaction;
import foundation.icon.icx.data.Block;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class GetBlock {

    private IconService iconService;

    private GetBlock() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void getLastBlock() throws IOException {
        Block block = iconService.getLastBlock().execute();
        System.out.println("getLastBlock: " + block);
    }

    private void getBlockByHash(Bytes blockHash) throws IOException {
        Block block = iconService.getBlock(blockHash).execute();
        System.out.println("getBlockByHash: " + block);
    }

    private void getBlockByHeight(BigInteger blockHeight) throws IOException {
        Block block = iconService.getBlock(blockHeight).execute();
        System.out.println("getBlockByHeight:" + block);
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new SendIcxTransaction().sendTransaction();
        if (!BigInteger.ONE.equals(result.getStatus())) {
            System.out.println("SendIcxTransaction failed!");
            return;
        }
        GetBlock block = new GetBlock();
        block.getLastBlock();
        block.getBlockByHash(result.getBlockHash());
        block.getBlockByHeight(result.getBlockHeight());
    }
}
