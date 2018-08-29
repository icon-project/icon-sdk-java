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

import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class TransactionResult {

    private RpcObject properties;
    TransactionResult(RpcObject properties) {
        this.properties = properties;
    }

    public BigInteger getStatus() {
        RpcItem item = properties.getItem("status");
        return item != null ? item.asInteger() : null;
    }

    public String getTo() {
        RpcItem item = properties.getItem("to");
        return item != null ? item.asString() : null;
    }

    public Bytes getTxHash() {
        RpcItem item = properties.getItem("txHash");
        return item != null ? item.asBytes() : null;
    }

    public BigInteger getBlockHeight() {
        RpcItem item = properties.getItem("blockHeight");
        return item != null ? item.asInteger() : null;
    }

    public Bytes getBlockHash() {
        RpcItem item = properties.getItem("blockHash");
        return item != null ? item.asBytes() : null;
    }

    public BigInteger getCumulativeStepUsed() {
        RpcItem item = properties.getItem("cumulativeStepUsed");
        return item != null ? item.asInteger() : null;
    }

    public BigInteger getStepUsed() {
        RpcItem item = properties.getItem("stepUsed");
        return item != null ? item.asInteger() : null;
    }

    public BigInteger getStepPrice() {
        RpcItem item = properties.getItem("stepPrice");
        return item != null ? item.asInteger() : null;
    }

    public String getScoreAddress() {
        RpcItem item = properties.getItem("scoreAddress");
        return item != null ? item.asString() : null;
    }

    public String getLogsBloom() {
        RpcItem item = properties.getItem("logsBloom");
        return item != null ? item.asString() : null;
    }

    public List<EventLog> getEventLogs() {
        RpcItem item = properties.getItem("eventLogs");
        List<EventLog> eventLogs = new ArrayList<>();
        if (item != null) {
            for (RpcItem rpcItem : item.asArray()) {
                eventLogs.add(new EventLog(rpcItem.asObject()));
            }
        }
        return eventLogs;
    }

    public RpcObject getFailure() {
        RpcItem item = properties.getItem("getFailure");
        return item != null ? item.asObject() : null;
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "properties=" + properties +
                '}';
    }
}
