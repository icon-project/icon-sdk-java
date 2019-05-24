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

import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.jsonrpc.RpcError;
import foundation.icon.icx.transport.jsonrpc.RpcObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

class TransactionHandler {
    private static final Address ZERO_ADDRESS = new Address("cx0000000000000000000000000000000000000000");
    private static final long DEFAULT_STEP = 100000;
    private final IconService iconService;

    TransactionHandler(IconService iconService) {
        this.iconService = iconService;
    }

    Bytes install(KeyWallet wallet, String filename, RpcObject params) throws IOException {
        byte[] content = readFile(filename);
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(BigInteger.valueOf(3))
                .from(wallet.getAddress())
                .to(ZERO_ADDRESS)
                .deploy("application/zip", content)
                .params(params)
                .build();

        // get an estimated step value and add some margin
        BigInteger estimatedStep = iconService.estimateStep(transaction).execute();
        BigInteger margin = BigInteger.valueOf(DEFAULT_STEP);

        // make a signed transaction with the same raw transaction and the estimated step
        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep.add(margin));
        return iconService.sendTransaction(signedTransaction).execute();
    }

    TransactionResult getTransactionResult(Bytes txHash) throws IOException {
        TransactionResult result = null;
        while (result == null) {
            try {
                result = iconService.getTransactionResult(txHash).execute();
            } catch (RpcError e) {
                System.out.println("RpcError: code: " + e.getCode() + ", message: " + e.getMessage());
                try {
                    // wait until block confirmation
                    System.out.println("Sleep 1.5 seconds.");
                    Thread.sleep(1500);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        return result;
    }

    private byte[] readFile(String filename) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(filename).getFile());
        long length = file.length();
        if (length > Integer.MAX_VALUE) throw new OutOfMemoryError("File is too big!!");
        byte[] result = new byte[(int) length];
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {
            inputStream.readFully(result);
        }
        return result;
    }
}
