/*
 * Copyright 2018 ICON Foundation
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

import foundation.icon.icx.Call.Builder;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Address.AddressPrefix;
import foundation.icon.icx.data.BTPNotification;
import foundation.icon.icx.data.Base64;
import foundation.icon.icx.data.BlockNotification;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.EventNotification;
import foundation.icon.icx.data.NetworkId;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import foundation.icon.icx.transport.monitor.Monitor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IconServiceTest {
    private static SecureRandom secureRandom;

    @BeforeAll
    static void setup() {
        secureRandom = new SecureRandom();
    }

    private byte[] getRandomBytes(int size) {
        byte[] bytes = new byte[size];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    @Test
    void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <T> Request<T> request(foundation.icon.icx.transport.jsonrpc.Request request, RpcConverter<T> converter) {
                return null;
            }

        });
        assertNotNull(iconService);
    }

    @Test
    void testGetTotalSupply() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getTotalSupply();

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTotalSupply", null)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetTotalSupplyWithHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = BigInteger.ONE;
        iconService.getTotalSupply(height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTotalSupply", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBalance() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Address address = new Address(AddressPrefix.EOA, getRandomBytes(20));
        iconService.getBalance(address);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBalance", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBalanceWithHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Address address = new Address(AddressPrefix.EOA, getRandomBytes(20));
        BigInteger height = BigInteger.ONE;
        iconService.getBalance(address, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));
        params.put("height", new RpcValue(height));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBalance", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBlockByHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBlock(BigInteger.ONE);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(BigInteger.ONE));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBlockByHeight", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBlockByHash() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        iconService.getBlock(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("hash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBlockByHash", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetLastBlock() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getLastBlock();

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getLastBlock", null)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetScoreApi() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Address address = new Address(AddressPrefix.CONTRACT, getRandomBytes(20));
        iconService.getScoreApi(address);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getScoreApi", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetScoreApiWithHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Address address = new Address(AddressPrefix.CONTRACT, getRandomBytes(20));
        BigInteger height = BigInteger.ONE;
        iconService.getScoreApi(address, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("address", new RpcValue(address));
        params.put("height", new RpcValue(height));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getScoreApi", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetTransaction() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        iconService.getTransaction(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("txHash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTransactionByHash", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetTransactionResult() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        iconService.getTransactionResult(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("txHash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getTransactionResult", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testSendTransactionAndWait() {
        Provider provider = mock(Provider.class);

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(NetworkId.MAIN)
                .from(new Address(SampleKeys.ADDRESS))
                .to(new Address(AddressPrefix.EOA, getRandomBytes(20)))
                .value(BigInteger.TEN.pow(18))
                .stepLimit(new BigInteger("20000", 16))
                .build();
        Wallet wallet = KeyWallet.load(new Bytes(SampleKeys.PRIVATE_KEY_STRING));
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

        IconService iconService = new IconService(provider);
        Request<TransactionResult> req = iconService.sendTransactionAndWait(signedTransaction);

        verify(provider).request(
                argThat(request -> request.getMethod().equals("icx_sendTransactionAndWait")),
                argThat(Objects::nonNull));
    }

    @Test
    void testWaitTransactionResult() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        Request<TransactionResult> req = iconService.waitTransactionResult(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("txHash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_waitTransactionResult", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetDataByHash() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        Request<Base64> req = iconService.getDataByHash(hash);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("hash", new RpcValue(hash));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getDataByHash", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetBlockHeaderByHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(8));
        Request<Base64> req = iconService.getBlockHeaderByHeight(height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getBlockHeaderByHeight", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetVotesByHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(8));
        Request<Base64> req = iconService.getVotesByHeight(height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getVotesByHeight", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testGetProofForResult() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Bytes hash = new Bytes(getRandomBytes(32));
        BigInteger index = new BigInteger(getRandomBytes(2));
        Request<Base64[]> req = iconService.getProofForResult(hash, index);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("hash", new RpcValue(hash));
        params.put("index", new RpcValue(index));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "icx_getProofForResult", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetNetworkInfo() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger id = new BigInteger(getRandomBytes(2));
        iconService.getBTPNetworkInfo(id);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("id", new RpcValue(id));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getNetworkInfo", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetNetworkInfoWithHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(2));
        BigInteger id = new BigInteger(getRandomBytes(2));
        iconService.getBTPNetworkInfo(id, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));
        params.put("id", new RpcValue(id));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getNetworkInfo", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetNetworkTypeInfo() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger id = new BigInteger(getRandomBytes(2));
        iconService.getBTPNetworkTypeInfo(id);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("id", new RpcValue(id));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getNetworkTypeInfo", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetNetworkTypeInfoWithHeight() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(2));
        BigInteger id = new BigInteger(getRandomBytes(2));
        iconService.getBTPNetworkTypeInfo(id, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));
        params.put("id", new RpcValue(id));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getNetworkTypeInfo", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetMessages() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(2));
        BigInteger networkID = new BigInteger(getRandomBytes(2));
        iconService.getBTPMessages(networkID, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));
        params.put("networkID", new RpcValue(networkID));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getMessages", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetHeader() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(2));
        BigInteger networkID = new BigInteger(getRandomBytes(2));
        iconService.getBTPHeader(networkID, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));
        params.put("networkID", new RpcValue(networkID));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getHeader", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetProof() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger height = new BigInteger(getRandomBytes(2));
        BigInteger networkID = new BigInteger(getRandomBytes(2));
        iconService.getBTPProof(networkID, height);

        HashMap<String, RpcValue> params = new HashMap<>();
        params.put("height", new RpcValue(height));
        params.put("networkID", new RpcValue(networkID));

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getProof", params)),
                argThat(Objects::nonNull));
    }

    @Test
    void testBTPGetSourceInformation() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.getBTPSourceInformation();

        verify(provider).request(
                argThat(request -> isRequestMatches(request, "btp_getSourceInformation", null)),
                argThat(Objects::nonNull));
    }

    @Test
    void testMonitorBlocks() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger startHeight = new BigInteger(getRandomBytes(10));
        Monitor<BlockNotification> bm = iconService.monitorBlocks(startHeight);

        verify(provider).monitor(
                argThat(monitorSpec -> "block".equals(monitorSpec.getPath())),
                argThat(Objects::nonNull));
    }

    @Test
    void testMonitorEvents() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger startHeight = new BigInteger(getRandomBytes(10));
        String event = "Event(Address)";
        Address addr = new Address(AddressPrefix.EOA, getRandomBytes(20));
        Monitor<EventNotification> em = iconService.monitorEvents(startHeight, event, addr, null, null);

        verify(provider).monitor(
                argThat(monitorSpec -> "event".equals(monitorSpec.getPath())
                        && startHeight.equals(monitorSpec.getParams().getItem("height").asInteger())
                        && event.equals(monitorSpec.getParams().getItem("event").asString())
                        && addr.equals(monitorSpec.getParams().getItem("addr").asAddress())
                ),
                argThat(Objects::nonNull));
    }

    @Test
    void testMonitorBTP() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        BigInteger startHeight = new BigInteger(getRandomBytes(10));
        BigInteger networkId = BigInteger.valueOf(100);
        Monitor<BTPNotification> bm = iconService.monitorBTP(startHeight, networkId, true);

        verify(provider).monitor(
                argThat(monitorSpec -> "btp".equals(monitorSpec.getPath())
                        && startHeight.equals(monitorSpec.getParams().getItem("height").asInteger())
                        && networkId.equals(monitorSpec.getParams().getItem("networkID").asInteger())
                        && "0x1".equals(monitorSpec.getParams().getItem("proofFlag").asString())
                ),
                argThat(Objects::nonNull));
    }

    @Test
    void testQuery() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        iconService.addConverterFactory(new RpcConverter.RpcConverterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public RpcConverter<PersonResponse> create(Class type) {
                if (PersonResponse.class == type) {
                    return new RpcConverter<PersonResponse>() {
                        @Override
                        public PersonResponse convertTo(RpcItem object) {
                            return new PersonResponse();
                        }

                        @Override
                        public RpcItem convertFrom(PersonResponse object) {
                            return null;
                        }
                    };
                }
                return null;
            }
        });

        Address from = new Address(AddressPrefix.EOA, getRandomBytes(20));
        Address to = new Address(AddressPrefix.CONTRACT, getRandomBytes(20));
        BigInteger height = BigInteger.ONE;
        String method = "addUser";

        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");
        person.hasPermission = false;

        Call<PersonResponse> call = new Builder()
                .from(from)
                .to(to)
                .height(height)
                .method(method)
                .params(person)
                .buildWith(PersonResponse.class);

        @SuppressWarnings("unused")
        Request<PersonResponse> query = iconService.call(call);

        verify(provider).request(
                argThat(request -> {
                    if (!request.getMethod().equals("icx_call")) return false;
                    RpcObject params = request.getParams();
                    if (!params.getItem("from").asAddress().equals(from) ||
                            !params.getItem("to").asAddress().equals(to) ||
                            !params.getItem("height").asInteger().equals(height)) {
                        return false;
                    }
                    RpcObject data = params.getItem("data").asObject();
                    if (!data.getItem("method").asString().equals(method))
                        return false;
                    RpcObject dataParams = data.getItem("params").asObject();
                    return dataParams.getItem("name").asString().equals(person.name) &&
                            dataParams.getItem("age").asInteger().equals(person.age) &&
                            dataParams.getItem("hasPermission").asBoolean() == person.hasPermission;
                }),
                argThat(Objects::nonNull));
    }

    @Test
    void testSendIcxTransaction() {
        Provider provider = mock(Provider.class);
        Address fromAddress = new Address("hxbe258ceb872e08851f1f59694dac2558708ece11");
        Address toAddress = new Address("hx5bfdb090f43a808005ffc27c25b213145e80b7cd");

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(NetworkId.MAIN)
                .from(fromAddress)
                .to(toAddress)
                .value(new BigInteger("de0b6b3a7640000", 16))
                .stepLimit(new BigInteger("12345", 16))
                .timestamp(new BigInteger("563a6cf330136", 16))
                .nonce(new BigInteger("1"))
                .build();
        Wallet wallet = KeyWallet.load(new Bytes(SampleKeys.PRIVATE_KEY_STRING));
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

        IconService iconService = new IconService(provider);
        iconService.sendTransaction(signedTransaction);

        String expected = "xR6wKs+IA+7E91bT8966jFKlK5mayutXCvayuSMCrx9KB7670CsWa0B7LQzgsxU0GLXaovlAT2MLs1XuDiSaZQE=";
        verify(provider).request(
                argThat(request -> {
                    boolean isMethodMatches = request.getMethod().equals("icx_sendTransaction");
                    boolean isSignatureMatches = request.getParams().getItem("signature").asString().equals(expected);
                    return isMethodMatches && isSignatureMatches;
                }),
                argThat(Objects::nonNull));
    }

    @Test
    void testTransferTokenTransaction() {
        Provider provider = mock(Provider.class);

        Address fromAddress = new Address("hxbe258ceb872e08851f1f59694dac2558708ece11");
        Address scoreAddress = new Address("cx982aed605b065b50a2a639c1ea5710ef5a0501a9");
        Address toAddress = new Address("hx5bfdb090f43a808005ffc27c25b213145e80b7cd");
        new Address("hxbe258ceb872e08851f1f59694dac2558708ece11");

        RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_value", new RpcValue(new BigInteger("1")))
                .build();

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(NetworkId.MAIN)
                .from(fromAddress)
                .to(scoreAddress)
                .stepLimit(new BigInteger("12345", 16))
                .timestamp(new BigInteger("563a6cf330136", 16))
                .nonce(new BigInteger("1"))
                .call("transfer")
                .params(params)
                .build();

        Wallet wallet = KeyWallet.load(new Bytes(SampleKeys.PRIVATE_KEY_STRING));
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

        IconService iconService = new IconService(provider);
        iconService.sendTransaction(signedTransaction);

        String expected = "ITpAdh3bUV4Xj0WQIPlfhv+ppA+K+LtXqaYMjnt8pMwV7QJwyZNQuhH2ljdGPR+31wG+GpKEdOEuqeYOwODBVwA=";
        verify(provider).request(
                argThat(request -> {
                    boolean isMethodMatches = request.getMethod().equals("icx_sendTransaction");
                    boolean isSignatureMatches = request.getParams().getItem("signature").asString().equals(expected);
                    return isMethodMatches && isSignatureMatches;
                }),
                argThat(Objects::nonNull));
    }

    @Test
    void testConverterNotfound() {
        Provider provider = mock(Provider.class);

        IconService iconService = new IconService(provider);
        Person person = new Person();
        person.name = "gold bug";
        person.age = new BigInteger("20");
        person.hasPermission = false;

        Call<PersonResponse> call = new Builder()
                .from(new Address(AddressPrefix.EOA, getRandomBytes(20)))
                .to(new Address(AddressPrefix.CONTRACT, getRandomBytes(20)))
                .method("addUser")
                .params(person)
                .buildWith(PersonResponse.class);

        assertThrows(IllegalArgumentException.class, () -> iconService.call(call));
    }

    private boolean isRequestMatches(foundation.icon.icx.transport.jsonrpc.Request request, String method, Map<String, RpcValue> params) {

        if (!request.getMethod().equals(method)) return false;
        if (request.getParams() == null && params == null) return true;
        if (request.getParams() != null && params != null) {
            boolean isParamMatches = true;
            Set<String> keys = params.keySet();
            for (String key : keys) {
                RpcValue value = ((RpcValue) (request.getParams()).getItem(key));
                isParamMatches = value.asString().equals(params.get(key).asString());
                if (!isParamMatches) break;
            }
            return isParamMatches;
        }
        return false;
    }

    @SuppressWarnings("WeakerAccess")
    static class Person {
        public String name;
        public BigInteger age;
        public boolean hasPermission;
    }

    @SuppressWarnings("unused")
    static class PersonResponse {
        public boolean isOk;
        public String message;
    }
}
