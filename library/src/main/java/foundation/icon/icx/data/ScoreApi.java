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
import foundation.icon.icx.transport.jsonrpc.RpcField;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.util.ArrayList;
import java.util.List;


public class ScoreApi {

    private RpcObject properties;

    ScoreApi(RpcObject properties) {
        this.properties = properties;
    }

    public String getType() {
        return getProperty("type").asString();
    }

    public String getName() {
        return getProperty("name").asString();
    }

    public List<Param> getInputs() {
        return getParams((RpcArray) properties.getValue("inputs"));
    }

    public List<Param> getOutputs() {
        return getParams((RpcArray) properties.getValue("outputs"));
    }

    List<Param> getParams(RpcArray array) {
        List<Param> params = new ArrayList<>(array.size());
        for (RpcField rpcField : array) {
            RpcObject object = (RpcObject) rpcField;

            String name = ((RpcValue) object.getValue("type")).asString();
            String type = ((RpcValue) object.getValue("type")).asString();
            params.add(new Param(name, type));
        }
        return params;
    }

    public String getReadonly() {
        return getProperty("readonly").asString();
    }

    RpcValue getProperty(String key) {
        return (RpcValue) properties.getValue(key);
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
