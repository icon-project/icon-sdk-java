package foundation.icon.icx.data;

import foundation.icon.icx.transport.jsonrpc.RpcField;

import java.math.BigInteger;
import java.util.List;


public class TransactionResult {

    private String status;
    private String to;
    private String txHash;
    private BigInteger blockHeight;
    private String blockHash;
    private BigInteger cumulativeStepUsed;
    private BigInteger stepUsed;
    private BigInteger stepPrice;
    private String scoreAddress;
    private String logsBloom;
    private List<RpcField> eventLogs;

    public String getStatus() {
        return status;
    }

    public String getTo() {
        return to;
    }

    public String getTxHash() {
        return txHash;
    }

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public BigInteger getCumulativeStepUsed() {
        return cumulativeStepUsed;
    }

    public BigInteger getStepUsed() {
        return stepUsed;
    }

    public BigInteger getStepPrice() {
        return stepPrice;
    }

    public String getScoreAddress() {
        return scoreAddress;
    }

    public String getLogsBloom() {
        return logsBloom;
    }

    public List<RpcField> getEventLogs() {
        return eventLogs;
    }

}
