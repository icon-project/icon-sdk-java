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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Block {

    private RpcObject properties;
    private Map<String, Class<?>> types;

    Block(RpcObject properties) {
        this.properties = properties;

        types = new HashMap<>();
        types.put("prev_block_hash", Bytes.class);
        types.put("merkle_tree_root_hash", Bytes.class);
        types.put("time_stamp", BigInteger.class);
        types.put("confirmed_transaction_list", RpcItem.class);
        types.put("block_hash", Bytes.class);
        types.put("peer_id", String.class);
        types.put("version", String.class);
        types.put("height", BigInteger.class);
        types.put("signature", String.class);
    }

    public Bytes getPrevBlockHash() {
        RpcItem item = properties.getItem("prev_block_hash");
        return item != null ? item.asBytes() : null;
    }

    public Bytes getMerkleTreeRootHash() {
        RpcItem item = properties.getItem("merkle_tree_root_hash");
        return item != null ? item.asBytes() : null;
    }

    public BigInteger getTimestamp() {
        RpcItem item = properties.getItem("time_stamp");
        return item != null ? item.asInteger() : null;
    }

    public List<ConfirmedTransaction> getTransactions() {
        RpcItem item = properties.getItem("confirmed_transaction_list");
        List<ConfirmedTransaction> transactions = new ArrayList<>();
        if (item != null) {
            for (RpcItem tx : item.asArray()) {
                transactions.add(new ConfirmedTransaction(tx.asObject()));
            }
        }
        return transactions;
    }

    public Bytes getBlockHash() {
        RpcItem item = properties.getItem("block_hash");
        return item != null ? item.asBytes() : null;
    }

    public String getPeerId() {
        RpcItem item = properties.getItem("peer_id");
        return item != null ? item.asString() : null;
    }

    public BigInteger getVersion() {
        RpcItem item = properties.getItem("version");
        return item != null ? item.asInteger() : null;
    }

    public BigInteger getHeight() {
        RpcItem item = properties.getItem("height");
        return item != null ? item.asInteger() : null;
    }

    public String getSignature() {
        RpcItem item = properties.getItem("signature");
        return item != null ? item.asString() : null;
    }

    @Override
    public String toString() {
        String text = (types == null) ? properties.toString() : properties.toString(types);
        return "Block{" +
                "properties=" + text +
                '}';
    }
}
