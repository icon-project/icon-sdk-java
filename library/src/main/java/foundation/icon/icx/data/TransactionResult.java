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

import foundation.icon.icx.transport.jsonrpc.RpcArray;
import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class TransactionResult {

    private RpcObject properties;

    TransactionResult(RpcObject properties) {
        this.properties = properties;
    }

    public BigInteger getStatus() {
        return getPropertyAsInteger("status");
    }

    public String getTo() {
        return getPropertyAsString("to");
    }

    public String getTxHash() {
        return getPropertyAsString("txHash");
    }

    public BigInteger getBlockHeight() {
        return getPropertyAsInteger("blockHeight");
    }

    public String getBlockHash() {
        return getPropertyAsString("blockHash");
    }

    public BigInteger getCumulativeStepUsed() {
        return getPropertyAsInteger("cumulativeStepUsed");
    }

    public BigInteger getStepUsed() {
        return getPropertyAsInteger("stepUsed");
    }

    public BigInteger getStepPrice() {
        return getPropertyAsInteger("stepPrice");
    }

    public String getScoreAddress() {
        return getPropertyAsString("scoreAddress");
    }

    public String getLogsBloom() {
        return getPropertyAsString("logsBloom");
    }

    public List<EventLog> getEventLogs() {
        RpcField field = properties.getValue("eventLogs");
        return field != null ? getArray(field.asArray()) : null;
    }

    private List<EventLog> getArray(RpcArray array) {
        List<EventLog> eventLogs = new ArrayList<>(array.size());
        for (RpcField rpcField : array) {
            eventLogs.add(new EventLog((RpcObject) rpcField));
        }
        return eventLogs;
    }

    String getPropertyAsString(String key) {
        RpcField value = properties.getValue(key);
        return value != null ? value.asString() : null;
    }

    BigInteger getPropertyAsInteger(String key) {
        RpcField value = properties.getValue(key);
        return value != null ? value.asInteger() : null;
    }

}
