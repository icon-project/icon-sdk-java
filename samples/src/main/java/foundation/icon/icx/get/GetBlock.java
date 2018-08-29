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

import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Block;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class GetBlock {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public GetBlock() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void getBlockByHeight() throws IOException {
        BigInteger height = new BigInteger("10");
        Block block = iconService.getBlock(height).execute();
        System.out.println("block:" + block);
    }

    public void getBlockByHash() throws IOException {
        Bytes hash = new Bytes("0x980d74c90094c78f1dfaa60c396f5b91e5021de2b6cd6a17caa9d941aa4b0c60");
        Block block = iconService.getBlock(hash).execute();
        System.out.println("block:" + block);
    }

    public void getLastBlock() throws IOException {
        Block block = iconService.getLastBlock().execute();
        System.out.println("block:" + block);
    }


    public static void main(String[] args) throws IOException {
        GetBlock block = new GetBlock();
        block.getLastBlock();
        block.getBlockByHash();
        block.getBlockByHeight();
    }

}
