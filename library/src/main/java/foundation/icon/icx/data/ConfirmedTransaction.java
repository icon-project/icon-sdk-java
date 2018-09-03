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
        RpcItem item = properties.getItem("version");
        return item != null ? item.asInteger() : null;
    }

    @Override
    public Address getFrom() {
        RpcItem item = properties.getItem("from");
        return item != null ? item.asAddress() : null;
    }

    @Override
    public Address getTo() {
        RpcItem item = properties.getItem("to");
        return item != null ? item.asAddress() : null;
    }

    @Override
    public BigInteger getValue() {
        RpcItem item = properties.getItem("value");
        return item != null ? item.asInteger() : null;
    }

    @Override
    public BigInteger getStepLimit() {
        RpcItem item = properties.getItem("stepLimit");
        return item != null ? item.asInteger() : null;
    }

    @Override
    public BigInteger getTimestamp() {
        RpcItem item = properties.getItem("timestamp");

        BigInteger timestamp = null;
        if (item != null) {
            try {
                timestamp = item.asInteger();
            } catch (RpcItem.RpcValueException e) {
                timestamp = new BigInteger(item.asString());
            }
        }

        return timestamp;
    }

    @Override
    public BigInteger getNid() {
        RpcItem item = properties.getItem("nid");
        return item != null ? item.asInteger() : null;
    }

    @Override
    public BigInteger getNonce() {
        RpcItem item = properties.getItem("nonce");

        BigInteger nonce = null;
        if (item != null) {
            try {
                nonce = item.asInteger();
            } catch (RpcItem.RpcValueException e) {
                nonce = new BigInteger(item.asString());
            }
        }

        return nonce;
    }

    @Override
    public String getDataType() {
        RpcItem item = properties.getItem("dataType");
        return item != null ? item.asString() : null;
    }

    @Override
    public RpcItem getData() {
        return properties.getItem("data");
    }

    public Bytes getTxHash() {
        RpcItem item = properties.getItem("txHash");
        return item != null ? item.asBytes() : null;
    }

    public BigInteger getTxIndex() {
        RpcItem item = properties.getItem("txIndex");
        return item != null ? item.asInteger() : null;
    }

    public BigInteger getBlockHeight() {
        RpcItem item = properties.getItem("blockHeight");
        return item != null ? item.asInteger() : null;
    }

    public Bytes getBlockHash() {
        RpcItem item = properties.getItem("blockHash");
        return item != null ? item.asBytes() : null;
    }

    public String getSignature() {
        RpcItem item = properties.getItem("signature");
        return item != null ? item.asString() : null;
    }

    @Override
    public String toString() {
        return "ConfirmedTransaction{" +
                "properties=" + properties +
                '}';
    }
}
