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

public enum AddressPrefix {

    EOA("hx"),
    CONTRACT("cx");

    private String prefix;

    AddressPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getValue() {
        return prefix;
    }

    public static AddressPrefix fromString(String prefix) {
        if (prefix != null) {
            for (AddressPrefix p : AddressPrefix.values()) {
                if (prefix.equalsIgnoreCase(p.getValue())) {
                    return p;
                }
            }
        }
        return null;
    }
}
