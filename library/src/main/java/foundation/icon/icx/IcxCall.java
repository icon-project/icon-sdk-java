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

import foundation.icon.icx.data.CallData;
import foundation.icon.icx.transport.jsonrpc.RpcField;

/**
 * IcxCall contains parameters for querying request.
 * @param <T> input type of the parameter
 */
@SuppressWarnings("FieldCanBeLocal")
public class IcxCall<T> {

    private String from;
    private String to;
    private String dataType = "call";
    private CallData<T> data;
    private Class<?> responseType;

    private IcxCall(
            String from, String to, CallData<T> data, Class<?> responseType) {
        this.from = from;
        this.to = to;
        this.data = data;
        this.responseType = responseType;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDataType() {
        return dataType;
    }

    public CallData<T> getData() {
        return data;
    }

    public Class<?> responseType() {
        return responseType;
    }

    /**
     * Builder for creating immutable object of  IcxCall
     * @param <T> input type of the parameter
     */
    public static class Builder<T> {
        private String from;
        private String to;
        private String method;
        private T params;
        private Class<?> responseType = RpcField.class;

        public Builder<T> from(String from) {
            this.from = from;
            return this;
        }

        public Builder<T> to(String to) {
            this.to = to;
            return this;
        }

        public Builder<T> method(String method) {
            this.method = method;
            return this;
        }

        public Builder<T> params(T params) {
            this.params = params;
            return this;
        }

        public Builder<T> responseType(Class<?> responseType) {
            this.responseType = responseType;
            return this;
        }

        public IcxCall<T> build() {
            CallData<T> callData = new CallData.Builder<T>()
                    .method(method)
                    .params(params)
                    .build();
            return new IcxCall<>(from, to, callData, responseType);
        }
    }

}
