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
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.math.BigInteger;


public class ConfirmedTransaction implements Transaction {

    private RpcObject properties;

    ConfirmedTransaction(RpcObject properties) {
        this.properties = properties;
    }

    @Override
    public BigInteger getVersion() {
        return getSafeProperty("version").asInteger();
    }

    @Override
    public Address getFrom() {
        return getSafeProperty("from").asAddress();
    }

    @Override
    public Address getTo() {
        return getSafeProperty("to").asAddress();
    }

    @Override
    public BigInteger getValue() {
        return getSafeProperty("value").asInteger();
    }

    @Override
    public BigInteger getStepLimit() {
        return getSafeProperty("stepLimit").asInteger();
    }

    @Override
    public BigInteger getTimestamp() {
        return getSafeProperty("timestamp").asInteger();
    }

    @Override
    public BigInteger getNid() {
        return getSafeProperty("nid").asInteger();
    }

    @Override
    public BigInteger getNonce() {
        return getSafeProperty("nonce").asInteger();
    }

    @Override
    public String getDataType() {
        return getSafeProperty("dataType").asString();
    }

    @Override
    public RpcItem getData() {
        return properties.getItem("data");
    }

    public String getTxHash() {
        return getSafeProperty("txHash").asString();
    }

    public BigInteger getTxIndex() {
        return getSafeProperty("txIndex").asInteger();
    }

    public BigInteger getBlockHeight() {
        return getSafeProperty("blockHeight").asInteger();
    }

    public String getBlockHash() {
        return getSafeProperty("blockHash").asString();
    }

    public String getSignature() {
        return getSafeProperty("signature").asString();
    }

    RpcItem getSafeProperty(String key) {
        RpcItem item = properties.getItem(key);
        if (item != null) return item.asValue();
        return new RpcItem() {

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public String asString() {
                return null;
            }

            @Override
            public BigInteger asInteger() {
                return null;
            }
        };
    }

}
