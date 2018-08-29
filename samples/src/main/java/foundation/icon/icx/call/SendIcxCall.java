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

package foundation.icon.icx.call;

import foundation.icon.icx.Call;
import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class SendIcxCall {

    public final String URL = "http://localhost:9000/api/v3";
    private final Address scoreAddress = new Address("cx0000000000000000000000000000000000000001");

    private IconService iconService;

    public SendIcxCall() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        this.iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public static void main(String[] args) throws IOException {
        new SendIcxCall().getStepCosts();
    }

    public void getStepCosts() throws IOException {
        Call<RpcItem> call = new Call.Builder()
                .to(scoreAddress)
                .method("getStepCosts")
                .build();

        RpcItem result = iconService.call(call).execute();
        RpcObject object = result.asObject();

        System.out.println("default:"+object.getItem("default").asInteger());
        System.out.println("contractCall:"+object.getItem("contractCall").asInteger());
        System.out.println("contractUpdate:"+object.getItem("contractUpdate").asInteger());
        System.out.println("contractDestruct:"+object.getItem("contractDestruct").asInteger());
        System.out.println("contractCreate:"+object.getItem("contractCreate").asInteger());
        System.out.println("contractSet:"+object.getItem("contractSet").asInteger());
        System.out.println("set:"+object.getItem("set").asInteger());
        System.out.println("replace:"+object.getItem("replace").asInteger());
        System.out.println("input:"+object.getItem("input").asInteger());
        System.out.println("eventLog:"+object.getItem("eventLog").asInteger());
    }
}
