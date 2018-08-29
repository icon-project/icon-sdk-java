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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventLog {
    private RpcObject properties;
    private Map<String, Class<?>> types;

    EventLog(RpcObject properties) {
        this.properties = properties;

        types = new HashMap<>();
        types.put("scoreAddress", String.class);
        types.put("indexed", RpcItem.class);
        types.put("data", RpcItem.class);
    }

    public String getScoreAddress() {
        RpcItem item = properties.getItem("scoreAddress");
        return item != null ? item.asString() : null;
    }

    public List<RpcItem> getIndexed() {
        RpcItem item = properties.getItem("indexed");
        return item != null ? item.asArray().asList() : null;
    }

    public List<RpcItem> getData() {
        RpcItem field = properties.getItem("data");
        return field != null ? field.asArray().asList() : null;
    }

    @Override
    public String toString() {
        String text = (types == null) ? properties.toString() : properties.toString(types);
        return "EventLog{" +
                "properties=" + text +
                '}';
    }
}
