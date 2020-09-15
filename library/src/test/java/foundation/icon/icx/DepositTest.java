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
import foundation.icon.icx.data.TransactionResult.EventLog;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class DepositTest {
    private static final long BLOCKS_IN_ONE_MONTH = 1296000;

    private IconService iconService;
    private KeyWallet owner;
    private TransactionHandler txHandler;

    @BeforeEach
    void init() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                //.addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
        txHandler = new TransactionHandler(iconService);
        owner = KeyWallet.load(Constants.PRIVATE_KEY);
    }

    @Test
    void testCheckArguments() {
        Address scoreAddress = new Address("cxaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        BigInteger depositAmount = IconAmount.of("5000", IconAmount.Unit.ICX).toLoop();

        assertThrows(IllegalArgumentException.class, () -> {
            TransactionBuilder.newBuilder()
                    .nid(BigInteger.valueOf(3))
                    .from(owner.getAddress())
                    .to(scoreAddress)
                    .value(depositAmount)
                    .stepLimit(new BigInteger("200000"))
                    .deposit()
                    .build();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionBuilder.newBuilder()
                    .nid(BigInteger.valueOf(3))
                    .from(owner.getAddress())
                    .to(scoreAddress)
                    .value(depositAmount)
                    .stepLimit(new BigInteger("200000"))
                    .deposit()
                    .add()
                    .withdraw(new Bytes("0x55cce746e97c7fe9dc0ffcd6b590f7917e7282c18dc65c9121e66ceda9710541"))
                    .build();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionBuilder.newBuilder()
                    .nid(BigInteger.valueOf(3))
                    .from(owner.getAddress())
                    .to(scoreAddress)
                    .value(depositAmount)
                    .stepLimit(new BigInteger("200000"))
                    .deposit()
                    .withdraw(new Bytes("0x55cce746e97c7fe9dc0ffcd6b590f7917e7282c18dc65c9121e66ceda9710541"))
                    .add()
                    .build();
        });
    }

    @Tag("integration")
    @Test
    void testSimpleAddAndWithdraw() throws IOException {
        // deploy a sample token first and get the address
        Address scoreAddress = deploySampleToken();

        // deposit ICX
        BigInteger depositAmount = IconAmount.of("5000", IconAmount.Unit.ICX).toLoop();
        Bytes depositId = depositICX(scoreAddress, depositAmount);

        // withdraw the deposit
        withdrawDeposit(scoreAddress, depositId, depositAmount);
    }

    private Address deploySampleToken() throws IOException {
        RpcObject params = new RpcObject.Builder()
                .put("_initialSupply", new RpcValue(new BigInteger("1000")))
                .put("_decimals", new RpcValue(new BigInteger("18")))
                .build();
        Bytes txHash = txHandler.install(owner, "sampleToken.zip", params);
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
        return new Address(result.getScoreAddress());
    }

    private Bytes depositICX(Address scoreAddress, BigInteger depositAmount) throws IOException {
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(owner.getAddress())
                .to(scoreAddress)
                .value(depositAmount)
                .stepLimit(new BigInteger("200000"))
                .deposit()
                .add()
                .build();
        SignedTransaction signedTransaction = new SignedTransaction(transaction, owner);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());

        // check if 'DepositAdded' eventlog is raised
        ensureDepositAdded(result, scoreAddress, "DepositAdded(bytes,Address,int,int)",
                txHash, owner.getAddress(), depositAmount);

        return txHash;
    }

    private void withdrawDeposit(Address scoreAddress, Bytes depositId, BigInteger depositAmount) throws IOException {
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(owner.getAddress())
                .to(scoreAddress)
                .stepLimit(new BigInteger("200000"))
                .deposit()
                .withdraw(depositId)
                .build();
        SignedTransaction signedTransaction = new SignedTransaction(transaction, owner);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());

        // check if 'DepositWithdrawn' eventlog is raised
        ensureDepositWithdrawn(result, scoreAddress, "DepositWithdrawn(bytes,Address,int,int)",
                depositId, owner.getAddress(), depositAmount);
    }

    private void ensureDepositAdded(TransactionResult result, Address scoreAddress, String funcSig,
                                    Bytes txHash, Address sender, BigInteger depositAmount) {
        EventLog event = findEventLog(result, scoreAddress, funcSig);
        assertNotNull(event);

        Bytes id = event.getIndexed().get(1).asBytes();
        Address from = event.getIndexed().get(2).asAddress();
        BigInteger amount = event.getData().get(0).asInteger();
        BigInteger term = event.getData().get(1).asInteger();
        assertEquals(txHash, id);
        assertEquals(sender, from);
        assertEquals(depositAmount, amount);
        assertEquals(BigInteger.valueOf(BLOCKS_IN_ONE_MONTH), term);
    }

    private void ensureDepositWithdrawn(TransactionResult result, Address scoreAddress, String funcSig,
                                        Bytes txHash, Address sender, BigInteger depositAmount) {
        EventLog event = findEventLog(result, scoreAddress, funcSig);
        assertNotNull(event);

        Bytes id = event.getIndexed().get(1).asBytes();
        Address from = event.getIndexed().get(2).asAddress();
        BigInteger amount = event.getData().get(0).asInteger();
        BigInteger penalty = event.getData().get(1).asInteger();
        assertEquals(txHash, id);
        assertEquals(sender, from);
        assertEquals(depositAmount, amount);
        assertEquals(BigInteger.valueOf(0), penalty);
    }

    private EventLog findEventLog(TransactionResult result, Address scoreAddress, String funcSig) {
        for (EventLog e : result.getEventLogs()) {
            if (e.getScoreAddress().equals(scoreAddress.toString())) {
                String signature = e.getIndexed().get(0).asString();
                System.out.println("function sig: " + signature);
                if (funcSig.equals(signature)) {
                    return e;
                }
            }
        }
        return null;
    }
}
