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

package foundation.icon.icx.data;

/**
 * CallData contains data parameters of ICX jsonrpc spec
 */
public class CallData<T> {
    private String method;
    private T params;

    private CallData(String method, T params) {
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public T getParams() {
        return params;
    }

    /**
     * Builder for Calldata
     */
    public static class Builder<T> {
        private String method;
        private T params;

        public Builder<T> method(String method) {
            this.method = method;
            return this;
        }

        public Builder<T> params(T params) {
            this.params = params;
            return this;
        }

        public CallData<T> build() {
            return new CallData<>(method, params);
        }
    }
}
