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

package com.example.iconsdk;

import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Block;
import foundation.icon.icx.data.ConfirmedTransaction;
import foundation.icon.icx.data.ScoreApi;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class BasicGetMethods {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public BasicGetMethods() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void getBalance() throws IOException {
        BigInteger balance = iconService.getBalance("hx0000000000000000000000000000000000000000").execute();
        System.out.println("balance:"+balance);
    }

    public void getTotalSupply() throws IOException {
        BigInteger totalSupply = iconService.getTotalSupply().execute();
        System.out.println("totalSupply:"+totalSupply);
    }

    public void getBlockByHeight() throws IOException {
        BigInteger height = BigInteger.ONE;
        Block block = iconService.getBlock(height).execute();
        System.out.println("block:"+block);
    }

    public void getBlockByHash() throws IOException {
        String hash = "0x980d74c90094c78f1dfaa60c396f5b91e5021de2b6cd6a17caa9d941aa4b0c60";
        Block block = iconService.getBlock(hash).execute();
        System.out.println("block:"+block);
    }

    public void getLastBlock() throws IOException {
        Block block = iconService.getBlock("latest").execute();
        System.out.println("block:"+block);
    }

    public void getTransaction() throws IOException {
        String txHash = "0xe8c167e2333eca73f10e1de03c9e616b655064aec2540913504cf0a4bab34db7";
        ConfirmedTransaction tx = iconService.getTransaction(txHash).execute();
        System.out.println("transaction:"+tx);
    }

    public void getTransactionResult() throws IOException {
        String txHash = "0x864cac2cbbde571116f4a8390047dfc88239168a2ddf70cb96601eb987a97cb7";
        TransactionResult tx = iconService.getTransactionResult(txHash).execute();
        System.out.println("transaction:"+tx);
    }

    public void getScoreApi() throws IOException {
        String scoreAddress = "cx2e6032c7598b882da4b156ed9334108a5b87f2dc";
        List<ScoreApi> apis = iconService.getScoreApi(scoreAddress).execute();
        System.out.println("apis:"+apis);
    }

    public static void main(String... args) throws IOException {
        BasicGetMethods sample = new BasicGetMethods();

        sample.getBalance();
        sample.getTotalSupply();
        sample.getBlockByHeight();
        sample.getBlockByHash();
        sample.getLastBlock();
        sample.getTransaction();
        sample.getTransactionResult();
        sample.getScoreApi();
    }
}
