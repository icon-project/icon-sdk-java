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
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.ScoreApi;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.List;

public class GetScoreApi {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public GetScoreApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void getScoreApi() throws IOException {
        Address scoreAddress = new Address("cx59f7016d2a13ff9e667c7a26cabda844a58782ae");
        List<ScoreApi> apis = iconService.getScoreApi(scoreAddress).execute();
        System.out.println("Score apis:" + apis);
    }

    public static void main(String[] args) throws IOException {
        new GetScoreApi().getScoreApi();
    }

}
