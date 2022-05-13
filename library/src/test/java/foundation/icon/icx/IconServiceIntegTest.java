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

package foundation.icon.icx;

import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.data.NetworkId;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
public class IconServiceIntegTest {
    private IconService iconService;
    private KeyWallet owner;
    private TransactionHandler txHandler;

    @BeforeEach
    void init() throws Exception {
        iconService = new IconService(new HttpProvider(Constants.SERVER_URL, 3));
        txHandler = new TransactionHandler(iconService);
        owner = KeyWallet.load(Constants.GOD_WALLET_PASSWORD,
                new File(getClass().getClassLoader().getResource(Constants.GOD_WALLET_FILENAME).getFile()));
    }

    @Test
    void testGetTotalSupply() throws IOException {
        BigInteger beforeTs = iconService.getTotalSupply().execute();
        BigInteger burnAmount = IconAmount.of("100", IconAmount.Unit.ICX).toLoop();
        TransactionResult result = burnICX(burnAmount);
        BigInteger afterTs = iconService.getTotalSupply().execute();
        BigInteger delta = beforeTs.subtract(afterTs);
        assertEquals(burnAmount, delta);

        BigInteger height = result.getBlockHeight();
        BigInteger heightTs = iconService.getTotalSupply(height).execute();
        assertEquals(beforeTs, heightTs);
        BigInteger heightTs1 = iconService.getTotalSupply(height.add(BigInteger.ONE)).execute();
        assertEquals(afterTs, heightTs1);
    }

    @Test
    void testGetBalance() throws IOException {
        BigInteger beforeBal = iconService.getBalance(owner.getAddress()).execute();
        BigInteger burnAmount = IconAmount.of("200", IconAmount.Unit.ICX).toLoop();
        TransactionResult result = burnICX(burnAmount);
        BigInteger afterBal = iconService.getBalance(owner.getAddress()).execute();
        BigInteger delta = beforeBal.subtract(afterBal);
        BigInteger fee = result.getStepPrice().multiply(result.getStepUsed());
        assertEquals(burnAmount.add(fee), delta);

        BigInteger height = result.getBlockHeight();
        BigInteger heightBal = iconService.getBalance(owner.getAddress(), height).execute();
        assertEquals(beforeBal, heightBal);
        BigInteger heightBal1 = iconService.getBalance(owner.getAddress(), height.add(BigInteger.ONE)).execute();
        assertEquals(afterBal, heightBal1);
    }

    private TransactionResult burnICX(BigInteger burnAmount) throws IOException {
        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(NetworkId.LOCAL)
                .from(owner.getAddress())
                .to(Constants.ZERO_ADDRESS)
                .value(burnAmount)
                .stepLimit(Constants.DEFAULT_STEP.multiply(BigInteger.TEN))
                .call("burn")
                .build();
        SignedTransaction signedTransaction = new SignedTransaction(transaction, owner);
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
        TransactionResult result = txHandler.getTransactionResult(txHash);
        assertEquals(BigInteger.ONE, result.getStatus());
        return result;
    }
}
