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

import java.math.BigInteger;

/**
 * Defines network ids
 */
public enum NetworkId {

    MAIN(BigInteger.valueOf(1)),
    LISBON(BigInteger.valueOf(2)),
    BERLIN(BigInteger.valueOf(7)),
    LOCAL(BigInteger.valueOf(3));

    private final BigInteger nid;

    NetworkId(BigInteger nid) {
        this.nid = nid;
    }

    public BigInteger getValue() {
        return nid;
    }
}
