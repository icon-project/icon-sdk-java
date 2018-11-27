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
 *
 */

package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.jsonrpc.RpcArray;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcObject.Builder;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

/**
 * SignedTransaction serializes transaction messages and
 * makes parameters to send
 */
public class SignedTransaction {

    private Transaction transaction;
    private Wallet wallet;
    private RpcObject properties;

    public SignedTransaction(Transaction transaction, Wallet wallet) {
        this.transaction = transaction;
        this.wallet = wallet;
        createProperties();
    }

    /**
     * Create the parameters including signature
     */
    private void createProperties() {
        RpcObject object = getTransactionProperties();

        RpcObject.Builder builder = new RpcObject.Builder();
        for (String key : object.keySet()) {
            builder.put(key, object.getItem(key));
        }

        String signature = Base64.toBase64String(getSignature(object));
        builder.put("signature", new RpcValue(signature));
        this.properties = builder.build();
    }

    /**
     * Gets the parameters including signature
     *
     * @return parameters
     */
    public RpcObject getProperties() {
        return properties;
    }

    RpcObject getTransactionProperties() {
        BigInteger timestamp = transaction.getTimestamp();
        if (timestamp == null) {
            timestamp = new BigInteger(Long.toString(System.currentTimeMillis() * 1000L));
        }

        Builder builder = new Builder();
        putTransactionPropertyToBuilder(builder, "version", transaction.getVersion());
        putTransactionPropertyToBuilder(builder, "from", transaction.getFrom());
        putTransactionPropertyToBuilder(builder, "to", transaction.getTo());
        putTransactionPropertyToBuilder(builder, "value", transaction.getValue());
        putTransactionPropertyToBuilder(builder, "stepLimit", transaction.getStepLimit());
        putTransactionPropertyToBuilder(builder, "timestamp", timestamp);
        putTransactionPropertyToBuilder(builder, "nid", transaction.getNid());
        putTransactionPropertyToBuilder(builder, "nonce", transaction.getNonce());
        putTransactionPropertyToBuilder(builder, "dataType", transaction.getDataType());
        putTransactionPropertyToBuilder(builder, "data", transaction.getData());
        return builder.build();
    }

    private void putTransactionPropertyToBuilder(Builder builder, String key, BigInteger value) {
        if (value != null) builder.put(key, new RpcValue(value));
    }

    private void putTransactionPropertyToBuilder(Builder builder, String key, String value) {
        if (value != null) builder.put(key, new RpcValue(value));
    }

    private void putTransactionPropertyToBuilder(Builder builder, String key, Address value) {
        if (value != null) builder.put(key, new RpcValue(value));
    }

    private void putTransactionPropertyToBuilder(Builder builder, String key, RpcItem item) {
        if (item != null) builder.put(key, item);
    }

    /**
     * Gets the signature of the transaction
     *
     * @return signature
     */
    byte[] getSignature(RpcObject properties) {
        return wallet.sign(generateMessage(serialize(properties)));
    }

    /**
     * Generates the hash of data
     *
     * @return hash
     */
    byte[] generateMessage(String data) {
        return new SHA3.Digest256().digest(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Serializes the properties
     *
     * @return Serialized property
     */
    String serialize(RpcObject properties) {
        return TransactionSerializer.serialize(properties);
    }

    /**
     * Transaction Serializer for generating a signature with transaction properties.
     */
    public static class TransactionSerializer {

        /**
         * Serializes properties as string
         *
         * @param properties transaction properties
         * @return serialized string of properties
         */
        public static String serialize(RpcObject properties) {
            StringBuilder builder = new StringBuilder();
            builder.append("icx_sendTransaction.");
            serializeObjectItems(builder, properties);
            return builder.toString();
        }

        static void serialize(StringBuilder builder, RpcItem item) {
            if (item instanceof RpcObject) {
                builder.append("{");
                serializeObjectItems(builder, item.asObject());
                builder.append("}");
            } else if (item instanceof RpcArray) {
                builder.append("[");
                serializeArrayItems(builder, item.asArray());
                builder.append("]");
            } else {
                if (item == null) {
                    builder.append("\\0");
                } else {
                    builder.append(escape(item.asString()));
                }
            }
        }

        private static void serializeObjectItems(StringBuilder builder, RpcObject object) {
            boolean firstItem = true;
            // Sorts keys before serializing object
            TreeSet<String> keys = new TreeSet<>(object.keySet());
            for (String key : keys) {
                if (firstItem) {
                    firstItem = false;
                } else {
                    builder.append(".");
                }
                serialize(builder.append(key).append("."), object.getItem(key));
            }
        }

        private static void serializeArrayItems(StringBuilder builder, RpcArray array) {
            boolean firstItem = true;
            for (RpcItem child : array) {
                if (firstItem) {
                    firstItem = false;
                } else {
                    builder.append(".");
                }
                serialize(builder, child);
            }
        }

        static String escape(String string) {
            return string.replaceAll("([\\\\.{}\\[\\]])", "\\\\$1");
        }
    }

}
