/*
 * Copyright 2019 ICON Foundation
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

package foundation.icon.icx;

import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.jsonrpc.RpcError;

import java.io.IOException;

public class Utils {

    public static TransactionResult getTransactionResult(IconService iconService, Bytes txHash) throws IOException {
        TransactionResult result = null;
        while (result == null) {
            try {
                result = iconService.getTransactionResult(txHash).execute();
            } catch (RpcError e) {
                System.out.println("RpcError: code: " + e.getCode() + ", message: " + e.getMessage());
                try {
                    // wait until block confirmation
                    System.out.println("Sleep 1.2 second.");
                    Thread.sleep(1200);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        return result;
    }
}
