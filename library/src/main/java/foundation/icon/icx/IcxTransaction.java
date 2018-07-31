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

import java.math.BigInteger;
import java.util.TreeMap;

import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

/**
 * IcxTransaction class includes properties for sending ICX transaction
 */
public class IcxTransaction implements Transaction {

    private final RpcObject params;

    private IcxTransaction(RpcObject params) {
        this.params = params;
    }

    @Override
    public BigInteger getVersion() {
        RpcField rpcField = params.getValue("version");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public String getFrom() {
        RpcField rpcField = params.getValue("from");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asString();
    }

    @Override
    public String getTo() {
        RpcField rpcField = params.getValue("to");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asString();
    }

    @Override
    public BigInteger getValue() {
        RpcField rpcField = params.getValue("value");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public BigInteger getStepLimit() {
        RpcField rpcField = params.getValue("stepLimit");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public BigInteger getTimestamp() {
        RpcField rpcField = params.getValue("timestamp");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public BigInteger getNID() {
        RpcField rpcField = params.getValue("nid");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public BigInteger getNonce() {
        RpcField rpcField = params.getValue("nonce");
        if (rpcField == null) return null;
        return ((RpcValue) rpcField).asInteger();
    }

    @Override
    public RpcObject getParams() {
        return params;
    }

    /**
     * Builder for IcxTransaction
     */
    public static class Builder {
        private RpcObject.Builder objectBuilder;

        public Builder() {
            objectBuilder = new RpcObject.Builder(new TreeMap<>());
            objectBuilder.put("version", new RpcValue(new BigInteger("3")));
        }

        public Builder version(BigInteger version) {
            objectBuilder.put("version", new RpcValue(version));
            return this;
        }

        public Builder from(String from) {
            objectBuilder.put("from", new RpcValue(from));
            return this;
        }

        public Builder to(String to) {
            objectBuilder.put("to", new RpcValue(to));
            return this;
        }

        public Builder value(BigInteger value) {
            objectBuilder.put("value", new RpcValue(value));
            return this;
        }

        public Builder stepLimit(BigInteger stepLimit) {
            objectBuilder.put("stepLimit", new RpcValue(stepLimit));
            return this;
        }

        public Builder timestamp(BigInteger timestamp) {
            objectBuilder.put("timestamp", new RpcValue(timestamp));
            return this;
        }

        public Builder nid(BigInteger nid) {
            objectBuilder.put("nid", new RpcValue(nid));
            return this;
        }

        public Builder nonce(BigInteger nonce) {
            objectBuilder.put("nonce", new RpcValue(nonce));
            return this;
        }

        public IcxTransaction build() {
            return new IcxTransaction(objectBuilder.build());
        }
    }

}
