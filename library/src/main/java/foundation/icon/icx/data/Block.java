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
