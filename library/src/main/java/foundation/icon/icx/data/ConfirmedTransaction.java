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
 */

package foundation.icon.icx.data;

import foundation.icon.icx.Transaction;
import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;


public class ConfirmedTransaction implements Transaction {

    private RpcObject properties;

    ConfirmedTransaction(RpcObject properties) {
        this.properties = properties;
    }

    @Override
    public BigInteger getVersion() {
        return getProperty("version").asInteger();
    }

    @Override
    public String getFrom() {
        return getProperty("from").asString();
    }

    @Override
    public String getTo() {
        return getProperty("to").asString();
    }

    @Override
    public BigInteger getValue() {
        return getProperty("value").asInteger();
    }

    @Override
    public BigInteger getStepLimit() {
        return getProperty("stepLimit").asInteger();
    }

    @Override
    public BigInteger getTimestamp() {
        return getProperty("timestamp").asInteger();
    }

    @Override
    public BigInteger getNid() {
        return getProperty("nid").asInteger();
    }

    @Override
    public BigInteger getNonce() {
        return getProperty("nonce").asInteger();
    }

    @Override
    public String getDataType() {
        return getProperty("dataType").asString();
    }

    @Override
    public RpcField getData() {
        return getProperty("data");
    }

    public String getTxHash() {
        return getProperty("txHash").asString();
    }

    public BigInteger getTxIndex() {
        return getProperty("txIndex").asInteger();
    }

    public BigInteger getBlockHeight() {
        return getProperty("blockHeight").asInteger();
    }

    public String getBlockHash() {
        return getProperty("blockHash").asString();
    }

    public String getSignature() {
        return getProperty("signature").asString();
    }

    RpcValue getProperty(String key) {
        return (RpcValue) properties.getValue(key);
    }

}
