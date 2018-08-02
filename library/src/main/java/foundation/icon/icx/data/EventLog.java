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


public class EventLog {
    private RpcObject properties;

    EventLog(RpcObject properties) {
        this.properties = properties;
    }

    public String getScoreAddress() {
        RpcValue value = (RpcValue) properties.getValue("scoreAddress");
        return value != null ? value.asString() : null;
    }

    public List<RpcField> getIndexed() {
        RpcArray field = (RpcArray) properties.getValue("indexed");
        return field != null ? field.asArray().asList() : null;
    }

    public List<RpcField> getData() {
        RpcField field = properties.getValue("data");
        return field != null ? field.asArray().asList() : null;
    }

}
