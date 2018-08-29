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
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.ConfirmedTransaction;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class GetTransaction {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public GetTransaction() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void getTransaction() throws IOException {
        Bytes txHash = new Bytes("0xe8c167e2333eca73f10e1de03c9e616b655064aec2540913504cf0a4bab34db7");
        ConfirmedTransaction tx = iconService.getTransaction(txHash).execute();
        System.out.println("transaction:" + tx);
    }

    public void getTransactionResult() throws IOException {
        Bytes txHash = new Bytes("0xe8c167e2333eca73f10e1de03c9e616b655064aec2540913504cf0a4bab34db7");
        TransactionResult tx = iconService.getTransactionResult(txHash).execute();
        System.out.println("transaction:" + tx);
    }

    public static void main(String[] args) throws IOException {
        GetTransaction transaction = new GetTransaction();
        transaction.getTransaction();
        transaction.getTransactionResult();
    }
}
