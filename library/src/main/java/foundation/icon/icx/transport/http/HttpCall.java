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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import foundation.icon.icx.Call;
import foundation.icon.icx.Callback;
import foundation.icon.icx.transport.jsonrpc.Deserializers.BigIntegerDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.BooleanDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.BytesDeserializer;
import foundation.icon.icx.transport.jsonrpc.Deserializers.RpcFieldDeserializer;
import foundation.icon.icx.transport.jsonrpc.Response;
import foundation.icon.icx.transport.jsonrpc.RpcError;
import foundation.icon.icx.transport.jsonrpc.RpcField;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Http call can be executed by this class
 * @param <T> the data type of the response
 */
public class HttpCall<T> implements Call<T> {

    private final okhttp3.Call httpCall;
    private final Class<T> responseType;

    @SuppressWarnings("unused")
    HttpCall(okhttp3.Call httpCall, Class<T> responseType) {
        this.httpCall = httpCall;
        this.responseType = responseType;
    }

    @Override
    public T execute() throws IOException {
        return convertResponse(httpCall.execute());
    }

    @Override
    public void execute(final Callback<T> callback) {
        httpCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(
                    okhttp3.Call call, okhttp3.Response response) {
                try {
                    T result = convertResponse(response);
                    callback.onSuccess(result);
                } catch (IOException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    // converts the response data from the okhttp response
    private T convertResponse(okhttp3.Response httpResponse)
            throws IOException {
        ResponseBody body = httpResponse.body();
        if (body != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(createDeserializerModule());
            JavaType type = mapper.getTypeFactory().constructParametricType(Response.class, responseType);
            Response<T> response = mapper.readValue(body.byteStream(), type);
            T params = response.getResult();
            if(params != null) {
                return params;
            } else {
                throw response.getError();
            }
        } else {
            throw new RpcError(httpResponse.code(), httpResponse.message());
        }
    }

    private SimpleModule createDeserializerModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RpcField.class, new RpcFieldDeserializer());
        module.addDeserializer(BigInteger.class, new BigIntegerDeserializer());
        module.addDeserializer(boolean.class, new BooleanDeserializer());
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        module.addDeserializer(byte[].class, new BytesDeserializer());
        return module;
    }
}
