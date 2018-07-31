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

/**
 * IcxCall contains parameters for querying request.
 *
 * @param <I> Input parameter type
 * @param <O> Response type
 */
@SuppressWarnings("FieldCanBeLocal")
public class IcxCall<I, O> {

    private String from;
    private String to;
    private String dataType = "call";
    private CallData<I> data;
    private Class<O> responseType;

    private IcxCall(
            String from, String to, CallData<I> data, Class<O> responseType) {
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

    public CallData<I> getData() {
        return data;
    }

    public Class<O> responseType() {
        return responseType;
    }

    /**
     * Builder for creating immutable object of  IcxCall
     *
     * @param <I> Input parameter type
     * @param <O> Response type
     */
    public static class Builder<I, O> {
        private String from;
        private String to;
        private String method;
        private I params;
        private Class<O> responseType;

        /**
         * Create builder with the response type
         *
         * @param responseType response type of icx call
         */
        public Builder(Class<O> responseType) {
            this.responseType = responseType;
        }

        public Builder<I, O> from(String from) {
            this.from = from;
            return this;
        }

        public Builder<I, O> to(String to) {
            this.to = to;
            return this;
        }

        public Builder<I, O> method(String method) {
            this.method = method;
            return this;
        }

        public Builder<I, O> params(I params) {
            this.params = params;
            return this;
        }

        public IcxCall<I, O> build() {
            CallData<I> callData = new CallData.Builder<I>()
                    .method(method)
                    .params(params)
                    .build();
            return new IcxCall<>(from, to, callData, responseType);
        }
    }

}
