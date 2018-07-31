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
