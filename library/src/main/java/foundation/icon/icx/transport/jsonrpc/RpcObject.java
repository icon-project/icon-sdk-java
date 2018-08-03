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

package foundation.icon.icx.transport.jsonrpc;

import java.util.*;

/**
 * A read-only data class of RpcObject
 */
public class RpcObject implements RpcField {
    private final Map<String, RpcField> fields;

    private RpcObject(Map<String, RpcField> fields) {
        this.fields = fields;
    }

    public Set<String> keySet() {
        return fields.keySet();
    }

    public RpcField getValue(String key) {
        return fields.get(key);
    }

    @Override
    public String toString() {
        return "RpcObject(" +
                "fields=" + fields +
                ')';
    }

    /**
     * Returns new builder for using current RpcObject
     *
     * @return new builder
     */
    public Builder newBuilder() {
        Builder builder = new Builder();
        for (String key : keySet()) {
            builder.put(key, getValue(key));
        }
        return builder;
    }

    /**
     * Builder for RpcObject
     */
    public static class Builder {

        /**
         * Sort policy of the properties
         */
        public enum Sort {
            NONE,
            KEY,
            INSERT
        }

        private final Map<String, RpcField> fields;

        public Builder() {
            this(Sort.KEY);
        }

        public Builder(Sort sort) {
            switch (sort) {
                case KEY:
                    fields = new TreeMap<>();
                    break;
                case INSERT:
                    fields = new LinkedHashMap<>();
                    break;
                default:
                    fields = new HashMap<>();
                    break;
            }
        }

        public Builder put(String key, RpcField value) {
            if (!fields.containsKey(key)) fields.put(key, value);
            return this;
        }

        public RpcObject build() {
            return new RpcObject(fields);
        }
    }
}
