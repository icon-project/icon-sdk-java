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
import foundation.icon.icx.Constants;
import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class GetTokenBalance {

    private IconService iconService;

    private GetTokenBalance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void query(Address scoreAddress) throws IOException {
        Address ownerAddress = Constants.testAddress1;

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(ownerAddress))
                .build();

        Call<RpcItem> call = new Call.Builder()
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();
        System.out.println("balance: "+ result.asInteger());
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new DeploySampleToken().sendTransaction();
        if (!BigInteger.ONE.equals(result.getStatus())) {
            System.out.println("Deploy failed!");
            return;
        }
        Address scoreAddress = new Address(result.getScoreAddress());
        new SendTokenTransaction().sendTransaction(scoreAddress);
        new GetTokenBalance().query(scoreAddress);
    }
}
