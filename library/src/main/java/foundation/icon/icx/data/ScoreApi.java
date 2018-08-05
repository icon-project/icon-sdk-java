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
 */

package foundation.icon.icx.data;

import foundation.icon.icx.transport.jsonrpc.RpcArray;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class ScoreApi {

    private RpcObject properties;

    ScoreApi(RpcObject properties) {
        this.properties = properties;
    }

    public String getType() {
        return getSafeProperty("type").asString();
    }

    public String getName() {
        return getSafeProperty("name").asString();
    }

    public List<Param> getInputs() {
        return getParams(getSafeProperty("inputs").asArray());
    }

    public List<Param> getOutputs() {
        return getParams(getSafeProperty("outputs").asArray());
    }

    List<Param> getParams(RpcArray array) {
        List<Param> params = new ArrayList<>();
        if (array != null) {
            for (RpcItem rpcItem : array) {
                RpcObject object = (RpcObject) rpcItem;

                String name = object.getItem("name").asString();
                String type = object.getItem("type").asString();
                params.add(new Param(name, type));
            }
        }
        return params;
    }

    public String getReadonly() {
        return getSafeProperty("readonly").asString();
    }

    RpcItem getSafeProperty(String key) {
        RpcItem item = properties.getItem(key);
        if (item != null) item.asValue();
        return new RpcItem() {

            @Override
            public String asString() {
                return null;
            }

            @Override
            public BigInteger asInteger() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public RpcArray asArray() {
                return null;
            }
        };
    }

    public class Param {
        private String type;
        private String name;

        Param(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

    }
}
