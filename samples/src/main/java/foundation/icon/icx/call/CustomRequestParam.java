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
import foundation.icon.icx.Constants;
import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.token.DeploySampleToken;
import foundation.icon.icx.token.SendTokenTransaction;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class CustomRequestParam {

    private IconService iconService;

    private CustomRequestParam() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void getBalance(Address scoreAddress) throws IOException {
        Address address = Constants.testAddress1;
        Param params = new Param();
        params._owner = address;

        Call<RpcItem> call = new Call.Builder()
                .from(address)
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();

        RpcItem result = iconService.call(call).execute();
        System.out.println("balance: " + result);
    }

    class Param {
        Address _owner;
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new DeploySampleToken().sendTransaction();
        if (BigInteger.ONE.equals(result.getStatus())) {
            Address scoreAddress = new Address(result.getScoreAddress());
            new SendTokenTransaction().sendTransaction(scoreAddress);
            new CustomRequestParam().getBalance(scoreAddress);
        } else {
            System.out.println("Deploy failed!");
        }
    }
}
