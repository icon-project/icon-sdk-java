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

package foundation.icon.icx.transport.jsonrpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A read-only data class of RpcArray
 */
public class RpcArray implements RpcField {
    private final List<RpcField> fields;

    private RpcArray(List<RpcField> fields) {
        this.fields = fields;
    }

    public Iterator<RpcField> iterator() {
        return fields.iterator();
    }

    public RpcField get(int index) {
        return fields.get(index);
    }

    /**
     * Builder for RpcArray
     */
    public static class Builder {

        private final List<RpcField> fields;

        public Builder() {
            fields = new ArrayList<>();
        }

        public Builder add(RpcField value) {
            fields.add(value);
            return this;
        }

        public RpcArray build() {
            return new RpcArray(fields);
        }
    }
}
