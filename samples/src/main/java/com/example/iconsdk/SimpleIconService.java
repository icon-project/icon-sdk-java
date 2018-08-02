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

import foundation.icon.icx.Call;
import foundation.icon.icx.IconService;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class SimpleIconService {


    public static final String URL = "http://localhost:9000/api/v3";

    public static void main(String... args) throws IOException {

        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggning)
                .build();
        IconService iconService = new IconService(new HttpProvider(httpClient, URL));

        BigInteger balance = iconService.getBalance("hx0000000000000000000000000000000000000000").execute();
        System.out.println("balance:"+balance);

        BigInteger totalSupply = iconService.getTotalSupply().execute();
        System.out.println("totalSupply:"+totalSupply);

    }


}
