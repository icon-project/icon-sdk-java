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

package foundation.icon.icx;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SignedTransactionTest {
    @Test
    void testSerialize() throws IOException {

//        For the transaction below:
//        {
//            "jsonrpc": "2.0",
//                "method": "icx_sendTransaction",
//                "id": 1234,
//                "params": {
//                    "version": "0x3",
//                    "from": "hxbe258ceb872e08851f1f59694dac2558708ece11",
//                    "to": "hx5bfdb090f43a808005ffc27c25b213145e80b7cd",
//                    "value": "0xde0b6b3a7640000",
//                    "stepLimit": "0x12345",
//                    "timestamp": "0x563a6cf330136",
//                    "nonce": "0x1",
//                    "signature": "VAia7YZ2Ji6igKWzjR2YsGa2m53nKPrfK7uXYW78QLE+ATehAVZPC40szvAiA6NEU5gCYB4c4qaQzqDh2ugcHgA="
//              }
//        }
//
//        Expected output is:
//        icx_sendTransaction.from.hxbe258ceb872e08851f1f59694dac2558708ece11.nonce.0x1.stepLimit.0x12345.timestamp.0x563a6cf330136.to.hx5bfdb090f43a808005ffc27c25b213145e80b7cd.value.0xde0b6b3a7640000.version.0x3

        IcxTransaction icxTransaction = new IcxTransaction.Builder()
                .version(new BigInteger("3"))
                .from("hxbe258ceb872e08851f1f59694dac2558708ece11")
                .to("hx5bfdb090f43a808005ffc27c25b213145e80b7cd")
                .value(new BigInteger("de0b6b3a7640000", 16))
                .stepLimit(new BigInteger("12345", 16))
                .timestamp(new BigInteger("563a6cf330136", 16))
                .nonce(new BigInteger("1"))
                .build();
        Wallet wallet = mock(Wallet.class);
        SignedTransaction signedTransaction = new SignedTransaction(icxTransaction, wallet);
        String serialize = signedTransaction.serialize();
        assertEquals("icx_sendTransaction.from.hxbe258ceb872e08851f1f59694dac2558708ece11.nonce.0x1.stepLimit.0x12345.timestamp.0x563a6cf330136.to.hx5bfdb090f43a808005ffc27c25b213145e80b7cd.value.0xde0b6b3a7640000.version.0x3",
                serialize);
    }


}
