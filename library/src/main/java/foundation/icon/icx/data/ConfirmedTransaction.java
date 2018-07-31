package foundation.icon.icx.data;

import foundation.icon.icx.transport.jsonrpc.RpcField;

import java.math.BigInteger;


public class ConfirmedTransaction {

    private BigInteger version;
    private String from;
    private String to;
    private BigInteger value;
    private BigInteger stepLimit;
    private BigInteger timestamp;
    private BigInteger nonce;
    private BigInteger txIndex;
    private BigInteger blockHeight;
    private String blockHash;
    private String signature;
    private String dataType;
    private RpcField data;
    private String txHash;
    private String nid;

    public BigInteger getVersion() {
        return version;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public BigInteger getStepLimit() {
        return stepLimit;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getTxIndex() {
        return txIndex;
    }

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getSignature() {
        return signature;
    }

    public String getDataType() {
        return dataType;
    }

    public RpcField getData() {
        return data;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getNid() {
        return nid;
    }

}
