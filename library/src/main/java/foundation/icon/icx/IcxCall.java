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

import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcItemCreator;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

/**
 * IcxCall contains parameters for querying request.
 *
 * @param <O> Response type
 */
public class IcxCall<O> {

    private RpcObject properties;
    private Class<O> responseType;

    private IcxCall(RpcObject properties, Class<O> responseType) {
        this.properties = properties;
        this.responseType = responseType;
    }

    public RpcObject getProperties() {
        return properties;
    }

    public Class<O> responseType() {
        return responseType;
    }

    /**
     * Builder for creating immutable object of  IcxCall
     */
    public static class Builder {
        private String from;
        private String to;
        private String method;
        private RpcItem params;

        /**
         * Create builder with the response type
         */
        public Builder() {
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public <I> Builder params(I params) {
            this.params = RpcItemCreator.create(params);
            return this;
        }

        public Builder params(RpcItem params) {
            this.params = params;
            return this;
        }

        public IcxCall<RpcItem> build() {
            return buildWith(RpcItem.class);
        }

        public <O> IcxCall<O> buildWith(Class<O> responseType) {
            RpcObject data = new RpcObject.Builder()
                    .put("method", new RpcValue(method))
                    .put("params", params)
                    .build();

            RpcObject properties = new RpcObject.Builder()
                    .put("from", new RpcValue(from))
                    .put("to", new RpcValue(to))
                    .put("data", data)
                    .build();
            return new IcxCall<>(properties, responseType);
        }
    }

}
