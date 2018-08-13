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

package foundation.icon.icx.transport.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import foundation.icon.icx.Call;
import foundation.icon.icx.Provider;
import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcItemSerializer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.IOException;

/**
 * HttpProvider class transports as http jsonrpc
 */
public class HttpProvider implements Provider {

    private final OkHttpClient httpClient;
    private final String url;

    public HttpProvider(OkHttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    public HttpProvider(String url) {
        this(new OkHttpClient.Builder().build(), url);
    }

    /**
     * @see Provider#request(Request, RpcConverter)
     */
    @Override
    public <O> Call<O> request(final Request request, RpcConverter<O> converter) {

        // Makes the request body
        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                SimpleModule module = new SimpleModule();
                module.addSerializer(RpcItem.class, new RpcItemSerializer());
                mapper.registerModule(module);
                mapper.writeValue(sink.outputStream(), request);
            }
        };

        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        return new HttpCall<>(httpClient.newCall(httpRequest), converter);
    }
}
