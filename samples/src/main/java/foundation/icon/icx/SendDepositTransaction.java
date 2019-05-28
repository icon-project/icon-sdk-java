/*
 * Copyright 2019 ICON Foundation
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

import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.token.DeploySampleToken;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcArray;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class SendDepositTransaction {

    private final IconService iconService;
    private final KeyWallet wallet;
    private final Address scoreAddress;

    private SendDepositTransaction(Address address) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
        wallet = KeyWallet.load(Constants.privateKey);
        scoreAddress = address;
    }

    private Bytes addDeposit(BigInteger depositAmount) throws IOException {
        System.out.println("addDeposit: " + depositAmount);
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(wallet.getAddress())
                .to(scoreAddress)
                .value(depositAmount)
                .stepLimit(new BigInteger("200000"))
                .deposit()
                .add()
                .build();
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash: " + txHash);
        TransactionResult result = Utils.getTransactionResult(iconService, txHash);
        if (!BigInteger.ONE.equals(result.getStatus())) {
            throw new IOException("Add deposit failed!");
        }
        return txHash;
    }

    private Bytes withdrawDeposit(Bytes depositId) throws IOException {
        System.out.println("withdrawDeposit: " + depositId);
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(wallet.getAddress())
                .to(scoreAddress)
                .stepLimit(new BigInteger("200000"))
                .deposit()
                .withdraw(depositId)
                .build();
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash: " + txHash);
        TransactionResult result = Utils.getTransactionResult(iconService, txHash);
        if (!BigInteger.ONE.equals(result.getStatus())) {
            throw new IOException("Withdraw deposit failed!");
        }
        return txHash;
    }

    private void printDepositInfo(Address scoreAddress) throws IOException {
        RpcItem status = GovScore.getScoreStatus(iconService, scoreAddress);
        RpcItem item = status.asObject().getItem("depositInfo");
        if (item != null) {
            System.out.println("depositInfo: {");
            RpcObject info = item.asObject();
            for (String key : info.keySet()) {
                String M1 = "    ";
                if (key.equals("deposits")) {
                    RpcArray deposits = info.getItem("deposits").asArray();
                    System.out.println(M1 + "deposits: {");
                    String M2 = M1 + M1;
                    RpcObject deposit = deposits.get(0).asObject();
                    for (String key2 : deposit.keySet()) {
                        if (key2.equals("id") || key2.equals("sender")) {
                            System.out.printf(M2 + "%s: %s\n", key2, deposit.getItem(key2).asValue());
                        } else {
                            System.out.printf(M2 + "%s: %s\n", key2, deposit.getItem(key2).asInteger());
                        }
                    }
                    System.out.println(M1 + "}");
                } else if (key.equals("scoreAddress")){
                    System.out.printf(M1 + "%s: %s\n", key, info.getItem(key).asAddress());
                } else {
                    System.out.printf(M1 + "%s: %s\n", key, info.getItem(key).asInteger());
                }
            }
            System.out.println("}");
        } else {
            System.out.println("depositInfo NULL");
        }
    }

    private static class GovScore {
        private final static Address govAddress = new Address("cx0000000000000000000000000000000000000001");

        static RpcItem getScoreStatus(IconService iconService, Address scoreAddress) throws IOException {
            RpcObject params = new RpcObject.Builder()
                    .put("address", new RpcValue(scoreAddress))
                    .build();
            Call<RpcItem> call = new Call.Builder()
                    .to(govAddress)
                    .method("getScoreStatus")
                    .params(params)
                    .build();
            return iconService.call(call).execute();
        }
    }

    public static void main(String[] args) throws IOException {
        TransactionResult result = new DeploySampleToken().sendTransaction();
        if (!BigInteger.ONE.equals(result.getStatus())) {
            throw new IOException("Deploy failed!");
        }
        Address scoreAddress = new Address(result.getScoreAddress());
        System.out.println("scoreAddress: " + scoreAddress);
        SendDepositTransaction depositHandler = new SendDepositTransaction(scoreAddress);

        // deposit ICX
        BigInteger depositAmount = IconAmount.of("5000", IconAmount.Unit.ICX).toLoop();
        Bytes depositId = depositHandler.addDeposit(depositAmount);
        depositHandler.printDepositInfo(scoreAddress);

        // withdraw the deposit
        Bytes txHash = depositHandler.withdrawDeposit(depositId);
        depositHandler.printDepositInfo(scoreAddress);
    }
}
