/*
 * Copyright 2018 ICON Foundation.
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
 */

package foundation.icon.icx.call;

import foundation.icon.icx.Call;
import foundation.icon.icx.Constants;
import foundation.icon.icx.IconService;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcItemCreator;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class CustomResponseClass {

    private final Address scoreAddress = new Address("cx0000000000000000000000000000000000000001");
    private IconService iconService;

    private CustomResponseClass() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void getStepCosts() throws IOException {

        iconService.addConverterFactory(new RpcConverter.RpcConverterFactory() {
            @Override
            public <T> RpcConverter<T> create(Class<T> type) {
                if (StepCost.class == type) {
                    return new RpcConverter<T>() {
                        @Override
                        public T convertTo(RpcItem object) {
                            RpcObject o = (RpcObject) object;
                            StepCost cost = new StepCost();
                            cost.defaultCost = o.getItem("default").asInteger();
                            cost.contractCall = o.getItem("contractCall").asInteger();
                            cost.contractUpdate = o.getItem("contractUpdate").asInteger();
                            cost.contractDestruct = o.getItem("contractDestruct").asInteger();
                            cost.contractCreate = o.getItem("contractCreate").asInteger();
                            cost.contractSet = o.getItem("contractSet").asInteger();
                            cost.get = o.getItem("get").asInteger();
                            cost.set = o.getItem("set").asInteger();
                            cost.replace = o.getItem("replace").asInteger();
                            cost.delete = o.getItem("delete").asInteger();
                            cost.input = o.getItem("input").asInteger();
                            cost.eventLog = o.getItem("eventLog").asInteger();
                            cost.apiCall = o.getItem("apiCall").asInteger();
                            return (T) cost;
                        }

                        @Override
                        public RpcItem convertFrom(T object) {
                            return RpcItemCreator.create(object);
                        }
                    };
                }
                return null;
            }
        });

        Call<StepCost> call = new Call.Builder()
                .to(scoreAddress)
                .method("getStepCosts")
                .buildWith(StepCost.class);

        StepCost costs = iconService.call(call).execute();
        System.out.println("step costs: " + costs);
    }

    class StepCost {

        BigInteger defaultCost;
        BigInteger contractCall;
        BigInteger contractCreate;
        BigInteger contractUpdate;
        BigInteger contractDestruct;
        BigInteger contractSet;
        BigInteger get;
        BigInteger set;
        BigInteger replace;
        BigInteger delete;
        BigInteger input;
        BigInteger eventLog;
        BigInteger apiCall;

        @Override
        public String toString() {
            return "StepCost{" +
                    "defaultCost=" + defaultCost +
                    ", contractCall=" + contractCall +
                    ", contractCreate=" + contractCreate +
                    ", contractUpdate=" + contractUpdate +
                    ", contractDestruct=" + contractDestruct +
                    ", contractSet=" + contractSet +
                    ", get=" + get +
                    ", set=" + set +
                    ", replace=" + replace +
                    ", delete=" + delete +
                    ", input=" + input +
                    ", eventLog=" + eventLog +
                    ", apiCall=" + apiCall +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        new CustomResponseClass().getStepCosts();
    }
}
