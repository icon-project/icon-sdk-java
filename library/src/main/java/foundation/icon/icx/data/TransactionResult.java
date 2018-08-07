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

import foundation.icon.icx.transport.jsonrpc.RpcArray;
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
        return getSafeProperty("status").asInteger();
    }

    public String getTo() {
        return getSafeProperty("to").asString();
    }

    public Hex getTxHash() {
        return getSafeProperty("txHash").asHex();
    }

    public BigInteger getBlockHeight() {
        return getSafeProperty("blockHeight").asInteger();
    }

    public Hex getBlockHash() {
        return getSafeProperty("blockHash").asHex();
    }

    public BigInteger getCumulativeStepUsed() {
        return getSafeProperty("cumulativeStepUsed").asInteger();
    }

    public BigInteger getStepUsed() {
        return getSafeProperty("stepUsed").asInteger();
    }

    public BigInteger getStepPrice() {
        return getSafeProperty("stepPrice").asInteger();
    }

    public String getScoreAddress() {
        return getSafeProperty("scoreAddress").asString();
    }

    public String getLogsBloom() {
        return getSafeProperty("logsBloom").asString();
    }

    public List<EventLog> getEventLogs() {
        RpcArray array = getSafeProperty("eventLogs").asArray();
        List<EventLog> eventLogs = new ArrayList<>();
        if (array != null) {
            for (RpcItem rpcItem : array) {
                eventLogs.add(new EventLog(rpcItem.asObject()));
            }
        }
        return eventLogs;
    }

    RpcItem getSafeProperty(String key) {
        RpcItem item = properties.getItem(key);
        if (item != null) return item.asValue();
        return new RpcItem() {

            @Override
            public String asString() {
                return null;
            }

            @Override
            public BigInteger asInteger() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public RpcArray asArray() {
                return null;
            }
        };
    }

}
