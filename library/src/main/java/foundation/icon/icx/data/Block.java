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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;


public class Block {

    @JsonProperty("prev_block_hash")
    private String prevBlockHash;

    @JsonProperty("merkle_tree_root_hash")
    private String merkleTreeRootHash;

    @JsonProperty("time_stamp")
    private BigInteger timestamp;

    @JsonProperty("confirmed_transaction_list")
    private List<ConfirmedTransaction> transactions;

    @JsonProperty("block_hash")
    private String blockHash;

    @JsonProperty("peer_id")
    private String peerId;

    private String version;
    private BigInteger height;
    private String signature;

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public String getMerkleTreeRootHash() {
        return merkleTreeRootHash;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public List<ConfirmedTransaction> getTransactions() {
        return transactions;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getVersion() {
        return version;
    }

    public BigInteger getHeight() {
        return height;
    }

    public String getSignature() {
        return signature;
    }

}
