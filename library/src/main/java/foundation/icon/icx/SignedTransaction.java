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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcObject.Builder;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcFieldSerializer;

/**
 * SignedTransaction serializes transaction messages and
 * makes parameters to send
 */
public class SignedTransaction implements Transaction {

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
    public RpcObject getParams() {
        RpcObject params = transaction.getParams();

        Builder builder = params.newBuilder();
        builder.put("signature", new RpcValue(getSignature()));
        return builder.build();
    }

    /**
     * Gets the signature of the transaction
     *
     * @return signature
     */
    public String getSignature() {
        String message = generateMessage();
        return wallet.signMessage(message);
    }

    /**
     * Generates the message has of the transaction
     *
     * @return message hash
     */
    public String generateMessage() {
        byte[] bHash = new SHA3.Digest256().digest(serialize().getBytes());
        return RpcValue.toHexString(bHash, false);
    }

    /**
     * Serializes the properties
     *
     * @return Serialized property
     */
    // TODO make own serializer
    public String serialize() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RpcField.class, new RpcFieldSerializer());
        mapper.registerModule(module);

        RpcObject params = transaction.getParams();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(params)
                    .replaceAll("[\"]", "")
                    .replaceAll("[:,]", ".");
        } catch (JsonProcessingException ignored) {
        }
        if (jsonString == null || jsonString.length() < 2) return "";
        return "icx_sendTransaction." +
                jsonString.substring(1, jsonString.length() - 1);
    }

}
