/*
 * Copyright 2018 theloop Inc.
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
 *
 */

package foundation.icon.icx;

import foundation.icon.icx.data.NetworkId;
import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcFieldCreator;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public final class TransactionBuilder {

    private BigInteger version = new BigInteger("3");
    private String from;
    private String to;
    private BigInteger value;
    private BigInteger stepLimit;
    private BigInteger timestamp;
    private BigInteger nid;
    private BigInteger nonce;
    private String dataType;
    private RpcField data;

    private TransactionBuilder() {
    }

    public Transaction build() {
        return new SendingTransaction(this);
    }

    public static IcxBuilder of(NetworkId nid) {
        IcxBuilder icxBuilder = new IcxBuilder(new TransactionBuilder());
        return icxBuilder.nid(nid.getValue());
    }

    public static IcxBuilder of(BigInteger nid) {
        IcxBuilder icxBuilder = new IcxBuilder(new TransactionBuilder());
        return icxBuilder.nid(nid);
    }

    public static final class IcxBuilder {

        private TransactionBuilder txBuilder;

        private IcxBuilder(TransactionBuilder txBuilder) {
            this.txBuilder = txBuilder;
        }

        public IcxBuilder nid(BigInteger nid) {
            txBuilder.nid = nid;
            return this;
        }

        public IcxBuilder from(String from) {
            txBuilder.from = from;
            return this;
        }

        public IcxBuilder to(String to) {
            txBuilder.to = to;
            return this;
        }

        public IcxBuilder value(BigInteger value) {
            txBuilder.value = value;
            return this;
        }

        public IcxBuilder stepLimit(BigInteger stepLimit) {
            txBuilder.stepLimit = stepLimit;
            return this;
        }

        public IcxBuilder timestamp(BigInteger timestamp) {
            txBuilder.timestamp = timestamp;
            return this;
        }

        public IcxBuilder nonce(BigInteger nonce) {
            txBuilder.nonce = nonce;
            return this;
        }

        public CallBuilder call(String method) {
            return new CallBuilder(txBuilder, method);
        }

        public DeployBuilder deploy(String contentType, byte[] content) {
            return new DeployBuilder(txBuilder, contentType, content);
        }

        public MessageBuilder message(String message) {
            return new MessageBuilder(txBuilder, message);
        }

        public Transaction build() {
            return txBuilder.build();
        }

    }

    public static final class CallBuilder {

        private TransactionBuilder txBuilder;
        private RpcObject.Builder dataBuilder;

        private CallBuilder(TransactionBuilder txBuilder, String method) {
            this.txBuilder = txBuilder;
            this.txBuilder.dataType = "call";

            dataBuilder = new RpcObject.Builder()
                    .put("method", new RpcValue(method));
        }

        public CallBuilder params(RpcObject params) {
            dataBuilder.put("params", params);
            return this;
        }

        public <T> CallBuilder params(T params) {
            dataBuilder.put("params", RpcFieldCreator.create(params));
            return this;
        }

        public Transaction build() {
            txBuilder.data = dataBuilder.build();
            return txBuilder.build();
        }
    }

    public static final class DeployBuilder {

        private TransactionBuilder txBuilder;
        private RpcObject.Builder dataBuilder;

        private DeployBuilder(TransactionBuilder txBuilder, String contentType, byte[] content) {
            this.txBuilder = txBuilder;
            this.txBuilder.dataType = "deploy";

            dataBuilder = new RpcObject.Builder()
                    .put("contentType", new RpcValue(contentType))
                    .put("content", new RpcValue(content));
        }

        public DeployBuilder params(RpcObject params) {
            dataBuilder.put("params", params);
            return this;
        }

        public Transaction build() {
            txBuilder.data = dataBuilder.build();
            return txBuilder.build();
        }
    }

    public static final class MessageBuilder {
        private TransactionBuilder txBuilder;

        private MessageBuilder(TransactionBuilder txBuilder, String message) {
            this.txBuilder = txBuilder;
            this.txBuilder.dataType = "message";
            this.txBuilder.data = new RpcValue(message.getBytes(StandardCharsets.UTF_8));
        }

        public Transaction build() {
            return txBuilder.build();
        }

    }

    private static class SendingTransaction implements Transaction {
        private BigInteger version;
        private String from;
        private String to;
        private BigInteger value;
        private BigInteger stepLimit;
        private BigInteger timestamp;
        private BigInteger nid;
        private BigInteger nonce;
        private String dataType;
        private RpcField data;

        private SendingTransaction(TransactionBuilder txBuilder) {
            version = txBuilder.version;
            from = txBuilder.from;
            to = txBuilder.to;
            value = txBuilder.value;
            stepLimit = txBuilder.stepLimit;
            timestamp = txBuilder.timestamp;
            nid = txBuilder.nid;
            nonce = txBuilder.nonce;
            dataType = txBuilder.dataType;
            data = txBuilder.data;
        }

        @Override
        public BigInteger getVersion() {
            return version;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public BigInteger getValue() {
            return value;
        }

        @Override
        public BigInteger getStepLimit() {
            return stepLimit;
        }

        @Override
        public BigInteger getTimestamp() {
            return timestamp;
        }

        @Override
        public BigInteger getNid() {
            return nid;
        }

        @Override
        public BigInteger getNonce() {
            return nonce;
        }

        @Override
        public String getDataType() {
            return dataType;
        }

        @Override
        public RpcField getData() {
            return data;
        }
    }

}
