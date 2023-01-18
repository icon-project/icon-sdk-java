/*
 * Copyright 2022 ICON Foundation
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

package foundation.icon.icx.transport.monitor;

import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;

public class BTPMonitorSpec extends MonitorSpec {
    private final BigInteger height;
    private final BigInteger networkId;
    private final boolean proofFlag;

    public BTPMonitorSpec(BigInteger height, BigInteger networkId, boolean proofFlag) {
        this.path = "btp";
        this.height = height;
        this.networkId = networkId;
        this.proofFlag = proofFlag;
    }

    @Override
    public RpcObject getParams() {
        RpcObject.Builder builder = new RpcObject.Builder()
                .put("height", new RpcValue(this.height))
                .put("networkID", new RpcValue(this.networkId))
                .put("proofFlag", new RpcValue(this.proofFlag));
        return builder.build();
    }
}
