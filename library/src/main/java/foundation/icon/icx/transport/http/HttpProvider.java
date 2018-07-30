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

package foundation.icon.icx.transport.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigInteger;

import foundation.icon.icx.Call;
import foundation.icon.icx.Provider;
import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.Serializers.BigIntegerSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.BooleanSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.BytesSerializer;
import foundation.icon.icx.transport.jsonrpc.Serializers.RpcFieldSerializer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;

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

    @Override
    public <I, O> Call<O> request(
            final Request<I> request, Class<O> responseType) {

        // Makes the request body
        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addSerializer(RpcField.class, new RpcFieldSerializer());
                module.addSerializer(BigInteger.class, new BigIntegerSerializer());
                module.addSerializer(boolean.class, new BooleanSerializer());
                module.addSerializer(Boolean.class, new BooleanSerializer());
                module.addSerializer(byte[].class, new BytesSerializer());
                mapper.registerModule(module);
                mapper.writeValue(sink.outputStream(), request);
            }
        };

        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        return new HttpCall<>(httpClient.newCall(httpRequest), responseType);
    }
}
