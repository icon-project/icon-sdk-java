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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Iterator;

public class RpcDeserializer extends JsonDeserializer<RpcField> {

    @Override
    public RpcField deserialize(
            JsonParser parser, DeserializationContext context)
            throws IOException {
        TreeNode node = parser.readValueAsTree();
        return deserialize(node);
    }

    private RpcField deserialize(TreeNode node) {
        if (node.isObject()) {
            RpcObject.Builder builder = new RpcObject.Builder();
            for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
                String fieldName = it.next();
                TreeNode childNode = node.get(fieldName);
                builder.put(fieldName, deserialize(childNode));
            }
            return builder.build();
        } else if (node.isArray()) {
            RpcArray.Builder builder = new RpcArray.Builder();
            for (int i = 0; i < node.size(); i++) {
                TreeNode childNode = node.get(i);
                builder.add(deserialize(childNode));
            }
            return builder.build();
        } else {
            return new RpcValue(node.asToken().asString());
        }
    }

}