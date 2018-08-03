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
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Block {

    private RpcObject properties;

    Block(RpcObject properties) {
        this.properties = properties;
    }

    public String getPrevBlockHash() {
        return getProperty("prev_block_hash").asString();
    }

    public String getMerkleTreeRootHash() {
        return getProperty("merkle_tree_root_hash").asString();
    }

    public BigInteger getTimestamp() {
        return getProperty("time_stamp").asInteger();
    }

    public List<ConfirmedTransaction> getTransactions() {
        RpcArray array = (RpcArray) properties.getValue("confirmed_transaction_list");

        List<ConfirmedTransaction> transactions = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            transactions.add(new ConfirmedTransaction((RpcObject) array.get(i)));
        }
        return transactions;
    }

    public String getBlockHash() {
        return getProperty("block_hash").asString();
    }

    public String getPeerId() {
        return getProperty("peer_id").asString();
    }

    public BigInteger getVersion() {
        return getProperty("version").asInteger();
    }

    public BigInteger getHeight() {
        return getProperty("height").asInteger();
    }

    public String getSignature() {
        return getProperty("signature").asString();
    }


    RpcValue getProperty(String key) {
        return (RpcValue) properties.getValue(key);
    }

}
