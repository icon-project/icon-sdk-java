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
 *
 */

package foundation.icon.icx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcObject.Builder;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcItemSerializer;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.math.BigInteger;

/**
 * SignedTransaction serializes transaction messages and
 * makes parameters to send
 */
public class SignedTransaction {

    private Transaction transaction;
    private Wallet wallet;

    public SignedTransaction(Transaction transaction, Wallet wallet) {
        this.transaction = transaction;
        this.wallet = wallet;
    }

    /**
     * Gets the parameters including signature
     *
     * @return parameters
     */
    public RpcObject getProperties() {
        RpcObject properties = getTransactionProperties();

        Builder builder = properties.newBuilder();
        builder.put("signature", new RpcValue(getSignature(properties)));
        return builder.build();
    }

    RpcObject getTransactionProperties() {
        return new Builder(Builder.Sort.KEY)
                .put("version", getRpcValueFromTransaction(transaction.getVersion()))
                .put("from", getRpcValueFromTransaction(transaction.getFrom()))
                .put("to", getRpcValueFromTransaction(transaction.getTo()))
                .put("value", getRpcValueFromTransaction(transaction.getValue()))
                .put("stepLimit", getRpcValueFromTransaction(transaction.getStepLimit()))
                .put("timestamp", getRpcValueFromTransaction(transaction.getTimestamp()))
                .put("nid", getRpcValueFromTransaction(transaction.getNid()))
                .put("nonce", getRpcValueFromTransaction(transaction.getNonce()))
                .put("dataType", getRpcValueFromTransaction(transaction.getDataType()))
                .put("data", transaction.getData())
                .build();
    }

    RpcValue getRpcValueFromTransaction(BigInteger value) {
        return value != null ? new RpcValue(value) : null;
    }

    RpcValue getRpcValueFromTransaction(String value) {
        return value != null ? new RpcValue(value) : null;
    }

    /**
     * Gets the signature of the transaction
     *
     * @return signature
     */
    String getSignature(RpcObject properties) {
        String message = generateMessage(properties);
        return wallet.signMessage(message);
    }

    /**
     * Generates the message has of the transaction
     *
     * @return message hash
     */
    String generateMessage(RpcObject properties) {
        byte[] bHash = new SHA3.Digest256().digest(serialize(properties).getBytes());
        return RpcValue.toHexString(bHash, false);
    }

    /**
     * Serializes the properties
     *
     * @return Serialized property
     */
    // TODO make own serializer
    String serialize(RpcObject properties) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        module.addSerializer(RpcItem.class, new RpcItemSerializer(true));
        mapper.registerModule(module);

        String jsonString = null;
        try {
            SerializePrinter printer = new SerializePrinter();
            ObjectWriter writer = mapper.writer(printer);
            jsonString = writer.writeValueAsString(properties);
        } catch (JsonProcessingException ignored) {
        }
        if (jsonString == null || jsonString.length() < 2) return "";
        return "icx_sendTransaction." +
                jsonString.substring(1, jsonString.length() - 1);
    }

}
