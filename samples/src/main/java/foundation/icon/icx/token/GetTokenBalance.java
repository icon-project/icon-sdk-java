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

package foundation.icon.icx.token;

import foundation.icon.icx.Call;
import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class GetTokenBalance {

    public final String URL = "http://localhost:9000/api/v3";
    private final Address scoreAddress = new Address("cx2e6032c7598b882da4b156ed9334108a5b87f2dc");

    private IconService iconService;

    public GetTokenBalance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void query() throws IOException {
        Address fromAddress = new Address("hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31");

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(fromAddress))
                .build();

        Call<RpcItem> call = new Call.Builder()
                .from(fromAddress)
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();
        System.out.println("result:"+result.asInteger());
    }

    public static void main(String[] args) throws IOException {
        new GetTokenBalance().query();
    }
}
