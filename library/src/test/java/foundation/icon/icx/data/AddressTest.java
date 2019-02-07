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
 *
 */

package foundation.icon.icx.data;

import foundation.icon.icx.crypto.IconKeys;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static foundation.icon.icx.data.Address.AddressPrefix.CONTRACT;
import static foundation.icon.icx.data.Address.AddressPrefix.EOA;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
public class AddressTest {
    private String eoa = "hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";
    private String contract = "cx1ca4697e8229e29adce3cded4412a137be6d7edb";

    @Test
    void testEoaAddress() {
        Address address = new Address(eoa);
        assertEquals(eoa, address.toString());
        assertEquals(EOA, address.getPrefix());
        assertTrue(IconKeys.isValidAddress(address));
        assertFalse(IconKeys.isContractAddress(address));
    }

    @Test
    void testContractCreate() {
        Address address = new Address(contract);
        assertEquals(contract, address.toString());
        assertEquals(CONTRACT, address.getPrefix());
        assertTrue(IconKeys.isValidAddress(address));
        assertTrue(IconKeys.isContractAddress(address));
    }

    @Test
    void testInvalidCreate() {
        String noPrefix = "4873b94352c8c1f3b2f09aaeccea31ce9e90bd31";
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(noPrefix);
        });

        String missCharacter = "4873b94352c8c1f3b2f09aaeccea31ce9e90bd3";
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(missCharacter);
        });

        String notHex = "4873b94352c8c1f3b2f09aaeccea31ce9e90bd3g";
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(notHex);
        });

        String words = "helloworldhelloworldhelloworldhelloworld";
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(words);
        });

        String upperAddress = "hx" + noPrefix.toUpperCase();
        assertThrows(IllegalArgumentException.class, () -> {
            new Address(upperAddress);
        });
    }

    @Test
    void testLoadPublicKey() {
        String pub = "04ffbb75929194621714be8998ea5c4f81d875188670b43a5ec0cef3a579fdf2280b882fddfcd6d2df8006978e35b37af5ca923e244672b07f98b95c21b4b9f03f";
        Address expected = new Address("hx18cc6371aeb01eecff91e68c01584e982755ca5d");

        Bytes publicKey = new Bytes(pub);
        Address address = IconKeys.getAddress(publicKey);
        Assertions.assertEquals(expected, address);

        publicKey = new Bytes(Hex.decode(pub));
        address = IconKeys.getAddress(publicKey);
        Assertions.assertEquals(expected, address);

        BigInteger bigPub = new BigInteger(pub, 16);
        byte[] hash = IconKeys.getAddressHash(bigPub);
        Assertions.assertEquals(expected, new Address(EOA, hash));

        BigInteger b = new BigInteger(1, Hex.decode(pub));
        Assertions.assertEquals(expected, new Address(EOA, IconKeys.getAddressHash(b)));

    }
}
