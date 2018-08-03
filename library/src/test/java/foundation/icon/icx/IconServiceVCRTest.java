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

package foundation.icon.icx;

import foundation.icon.icx.data.Block;
import foundation.icon.icx.data.ConfirmedTransaction;
import foundation.icon.icx.data.ScoreApi;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IconServiceVCRTest {

    public final String URL = "http://localhost:3000/api/v3";
    public final String PRIVATE_KEY_STRING =
            "2d42994b2f7735bbc93a3e64381864d06747e574aa94655c516f9ad0a74eed79";

    private IconService iconService;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
        wallet = KeyWallet.load(PRIVATE_KEY_STRING);
    }

    @Test
    void testGetBalance() throws IOException {
        BigInteger balance = iconService.getBalance(wallet.getAddress()).execute();
        assertEquals(new BigInteger("1021623405000000000000"), balance);
    }

    @Test
    void testGetTotalSupply() throws IOException {
        BigInteger totalSupply = iconService.getTotalSupply().execute();
        assertEquals(new BigInteger("801459900000000000000000000"), totalSupply);
    }

    @Test
    void testGetBlockByHeight() throws IOException {
        Block block = iconService.getBlock(BigInteger.ONE).execute();
        assertEquals("aa9b739597043e25e669dbc20eadbc17455b898540bf88018c7f065bdedb393a", block.getBlockHash());
    }

    @Test
    void testGetBlockByHash() throws IOException {
        String hash = "aa9b739597043e25e669dbc20eadbc17455b898540bf88018c7f065bdedb393a";
        Block block = iconService.getBlock(hash).execute();
        assertEquals(hash, block.getBlockHash());
    }

    @Test
    void testGetLastBlock() throws IOException {
        Block block = iconService.getBlock("latest").execute();
        assertEquals("53bc71e1a4e737b6ebeaa6efcceb2c4ec73b850d48e88baeeef735ea2d7b3286", block.getBlockHash());
    }

    @Test
    void testGetScoreApi() throws IOException {
        String scoreAddress = "cx1ca4697e8229e29adce3cded4412a137be6d7edb";
        List<ScoreApi> apis = iconService.getScoreApi(scoreAddress).execute();
        assertEquals("balanceOf", apis.get(0).getName());
    }

    @Test
    void testGetTransaction() throws IOException {
        String txHash = "0xe8c167e2333eca73f10e1de03c9e616b655064aec2540913504cf0a4bab34db7";
        ConfirmedTransaction tx = iconService.getTransaction(txHash).execute();
        assertEquals(txHash, tx.getTxHash());
    }

    @Test
    void testGetTransactionResult() throws IOException {
        String txHash = "0xe8c167e2333eca73f10e1de03c9e616b655064aec2540913504cf0a4bab34db7";
        TransactionResult tx = iconService.getTransactionResult(txHash).execute();
        assertEquals(txHash, tx.getTxHash());
    }

}
