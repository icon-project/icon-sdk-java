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

import foundation.icon.icx.crypto.IconKeys;

import java.security.InvalidParameterException;

public class Address {

    private AddressPrefix prefix;
    private byte[] body;

    Address(AddressPrefix prefix, byte[] body) {
        this.prefix = prefix;
        this.body = body;
    }

    public AddressPrefix getPrefix() {
        return prefix;
    }

    public String asString() {
        return getPrefix().getValue() + IconKeys.getHexAddress(body);
    }

    public static Address.Builder of(String address) {
        return new Builder().address(address);
    }

    public static final class Builder {
        private AddressPrefix prefix;
        private byte[] body;

        public Builder() {
        }

        public Builder address(String address) {
            AddressPrefix prefix = IconKeys.getAddressHexPrefix(address);
            if (prefix == null || !IconKeys.isValidAddress(address))
                throw new InvalidParameterException("Invalid address");
            this.prefix = prefix;
            this.body = IconKeys.getHexAddress(address);
            return this;
        }

        public Builder prefix(AddressPrefix prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Address build() {
            return new Address(prefix, body);
        }
    }
}
