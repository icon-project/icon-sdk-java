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
 */

package foundation.icon.icx.data;

import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScoreApi {

    private RpcObject properties;
    private Map<String, Class<?>> types;

    ScoreApi(RpcObject properties) {
        this.properties = properties;

        types = new HashMap<>();
        types.put("type", String.class);
        types.put("name", String.class);
        types.put("inputs", RpcItem.class);
        types.put("outputs", RpcItem.class);
        types.put("readonly", String.class);

    }

    public String getType() {
        RpcItem item = properties.getItem("type");
        return item != null ? item.asString() : null;
    }

    public String getName() {
        RpcItem item = properties.getItem("name");
        return item != null ? item.asString() : null;
    }

    public List<Param> getInputs() {
        return getParams(properties.getItem("inputs"));
    }

    public List<Param> getOutputs() {
        return getParams(properties.getItem("outputs"));
    }

    List<Param> getParams(RpcItem item) {
        List<Param> params = new ArrayList<>();
        if (item != null) {
            for (RpcItem rpcItem : item.asArray()) {
                RpcObject object = (RpcObject) rpcItem;

                String name = object.getItem("name").asString();
                String type = object.getItem("type").asString();
                params.add(new Param(name, type));
            }
        }
        return params;
    }

    public String getReadonly() {
        RpcItem item = properties.getItem("readonly");
        return item != null ? item.asString() : null;
    }

    @Override
    public String toString() {
        String text = (types == null) ? properties.toString() : properties.toString(types);
        return "ScoreApi{" +
                "properties=" + text +
                '}';
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
