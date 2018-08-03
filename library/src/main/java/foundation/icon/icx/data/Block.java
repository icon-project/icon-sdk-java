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
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Block {

    private RpcObject properties;

    Block(RpcObject properties) {
        this.properties = properties;
    }

    public String getPrevBlockHash() {
        return getSafeProperty("prev_block_hash").asString();
    }

    public String getMerkleTreeRootHash() {
        return getSafeProperty("merkle_tree_root_hash").asString();
    }

    public BigInteger getTimestamp() {
        return getSafeProperty("time_stamp").asInteger();
    }

    public List<ConfirmedTransaction> getTransactions() {
        RpcArray array = getSafeProperty("time_stamp").asArray();
        List<ConfirmedTransaction> transactions = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                transactions.add(new ConfirmedTransaction((RpcObject) array.get(i)));
            }
        }
        return transactions;
    }

    public String getBlockHash() {
        return getSafeProperty("block_hash").asString();
    }

    public String getPeerId() {
        return getSafeProperty("peer_id").asString();
    }

    public BigInteger getVersion() {
        return getSafeProperty("version").asInteger();
    }

    public BigInteger getHeight() {
        return getSafeProperty("height").asInteger();
    }

    public String getSignature() {
        return getSafeProperty("signature").asString();
    }


    RpcItem getSafeProperty(String key) {
        RpcItem item = properties.getItem(key);
        if (item != null) item.asValue();
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
            public RpcArray asArray() {
                return null;
            }
        };
    }

}
