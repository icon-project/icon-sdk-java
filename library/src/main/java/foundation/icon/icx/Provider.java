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

import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;

/**
 * Provider class transports the request and receives the response
 */
public interface Provider {

    /**
     * Prepares to execute the request
     *
     * @param request   A request to send
     * @param converter converter converter for the responseType
     * @param <O>       returning type
     * @return a Call object to execute
     */
    <O> Call<O> request(Request request, RpcConverter<O> converter);
}
