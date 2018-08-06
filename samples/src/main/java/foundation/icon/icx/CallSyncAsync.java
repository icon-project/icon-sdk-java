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

package foundation.icon.icx;

import foundation.icon.icx.data.Block;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class CallSyncAsync {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public CallSyncAsync() {
        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggning)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void sync() throws IOException {
        Block block = iconService.getBlock("latest").execute();
        System.out.println("sync call block hash:"+block.getBlockHash());
    }

    public void async() {
        iconService.getBlock("latest").execute(new Callback<Block>() {
            @Override
            public void onSuccess(Block block) {
                System.out.println("aysnc call block hash:"+block.getBlockHash());
            }

            @Override
            public void onFailure(Exception exception) {
                // exception
                System.out.println("exception:"+exception.getMessage());
            }
        });
    }

    public static void main(String[] args) throws IOException {
        CallSyncAsync call = new CallSyncAsync();
        call.sync();
        call.async();
    }
}
