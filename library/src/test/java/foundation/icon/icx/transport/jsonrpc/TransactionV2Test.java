/*
 * Copyright 2018 ICON Foundation.
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

package foundation.icon.icx.transport.jsonrpc;

import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.ConfirmedTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static foundation.icon.icx.data.Converters.CONFIRMED_TRANSACTION;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionV2Test {

    @Test
    void testVersion3() {
        RpcObject object = new RpcObject.Builder()
                .put("timestamp", new RpcValue("1535964734110836"))
                .put("nonce", new RpcValue("8367273"))
                .put("value", new RpcValue("4563918244f40000"))
                .put("version", new RpcValue("0x3"))
                .build();

        ConfirmedTransaction tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertThrows(RpcItem.RpcValueException.class, () -> {
            tx.getTimestamp();
        });

        assertThrows(RpcItem.RpcValueException.class, () -> {
            tx.getNonce();
        });

        assertThrows(RpcItem.RpcValueException.class, () -> {
            tx.getValue();
        });

        assertNull(tx.getFee());
    }

    @Test
    void testChangeSpec() {
        RpcObject object = new RpcObject.Builder()
                .put("timestamp", new RpcValue(new BigInteger(String.valueOf(1535964734110836L))))
                .put("nonce", new RpcValue("8367273"))
                .put("value", new RpcValue("0x4563918244f40000"))
                .put("fee", new RpcValue("0x2386f26fc10000"))
                .put("tx_hash", new RpcValue("30c19ce2b5139ead7fb51c567cf8a01a3ca8d7f881c04f1312b3550330c690bb"))
                .build();

        ConfirmedTransaction tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertEquals(new BigInteger("2", 16), tx.getVersion());
        assertEquals(new BigInteger("4563918244f40000", 16), tx.getValue());
        assertEquals(new BigInteger("1535964734110836"), tx.getTimestamp());
        assertEquals(new BigInteger("8367273"), tx.getNonce());
        assertEquals(new BigInteger("2386f26fc10000", 16), tx.getFee());
        assertEquals(new Bytes("30c19ce2b5139ead7fb51c567cf8a01a3ca8d7f881c04f1312b3550330c690bb"), tx.getTxHash());
    }

    @Test
    void testNoPrefixValue() {
        RpcObject object = new RpcObject.Builder()
                .put("value", new RpcValue("4563918244f40000"))
                .put("fee", new RpcValue("2386f26fc10000"))
                .build();

        ConfirmedTransaction tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertEquals(new BigInteger("2", 16), tx.getVersion());
        assertEquals(new BigInteger("4563918244f40000", 16), tx.getValue());
        assertEquals(new BigInteger("2386f26fc10000", 16), tx.getFee());
    }

    @Test
    void testInvalidValue() {
        RpcObject object = new RpcObject.Builder()
                .put("to", new RpcValue("123124124124"))
                .put("timestamp", new RpcValue(""))
                .build();

        ConfirmedTransaction tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertEquals("123124124124", tx.getTo().toString());
        assertNull(tx.getTimestamp());
        assertNull(tx.getFrom());

        object = new RpcObject.Builder()
                .put("to", new RpcValue("bf85fac2d0b507a2db9ce9526e6d91476f16a2d269f51636f9c4b2d512017faf"))
                .put("timestamp", null)
                .build();
        tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertEquals("bf85fac2d0b507a2db9ce9526e6d91476f16a2d269f51636f9c4b2d512017faf", tx.getTo().toString());
        assertNull(tx.getTimestamp());

        object = new RpcObject.Builder()
                .put("to", new RpcValue(""))
                .put("value", new RpcValue("45400a8fd5330000"))
                .build();
        tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertNull(tx.getTo());
        assertEquals(new BigInteger("45400a8fd5330000", 16), tx.getValue());


        object = new RpcObject.Builder()
                .put("to", new RpcValue("hxa23651905d221dd36b"))
                .put("timestamp", new RpcValue(Long.toString(1535964734110836L)))
                .build();
        tx = CONFIRMED_TRANSACTION.convertTo(object);
        assertEquals("hxa23651905d221dd36b", tx.getTo().toString());
        assertEquals("1535964734110836", tx.getTimestamp().toString());
    }
}
