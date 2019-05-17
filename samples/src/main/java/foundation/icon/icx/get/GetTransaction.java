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

import foundation.icon.icx.Constants;
import foundation.icon.icx.IconService;
import foundation.icon.icx.SendIcxTransaction;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.ConfirmedTransaction;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class GetTransaction {

    private IconService iconService;

    private GetTransaction() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void getTransaction(Bytes txHash) throws IOException {
        ConfirmedTransaction tx = iconService.getTransaction(txHash).execute();
        System.out.println("ConfirmedTransaction: " + tx);
    }

    private void getTransactionResult(Bytes txHash) throws IOException {
        TransactionResult result = iconService.getTransactionResult(txHash).execute();
        System.out.println("TransactionResult: " + result);
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new SendIcxTransaction().sendTransaction();
        if (!BigInteger.ONE.equals(result.getStatus())) {
            System.out.println("SendIcxTransaction failed!");
            return;
        }
        GetTransaction transaction = new GetTransaction();
        transaction.getTransaction(result.getTxHash());
        transaction.getTransactionResult(result.getTxHash());
    }
}
