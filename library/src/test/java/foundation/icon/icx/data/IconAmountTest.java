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

package foundation.icon.icx.data;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IconAmountTest {


    @Test
    void testCreate() {
        BigInteger loop = new BigInteger("1000000000000000000");

        IconAmount amount = IconAmount.of("1", IconAmount.Unit.ICX);
        assertEquals(new BigInteger("1"), amount.asInteger());
        assertEquals(IconAmount.Unit.ICX.getValue(), amount.getDigit());

        amount = IconAmount.of("1000000000000000000", IconAmount.Unit.LOOP);
        assertEquals("1000000000000000000", amount.toString());
        assertEquals(IconAmount.Unit.LOOP.getValue(), amount.getDigit());

        amount = IconAmount.of(new BigInteger("1000000000000000000"), 16);
        assertEquals(new BigInteger("1000000000000000000"), amount.asInteger());
        assertEquals(16, amount.getDigit());

        amount = IconAmount.of(new BigDecimal("0.1"), IconAmount.Unit.ICX);
        assertEquals(new BigDecimal("0.1"), amount.asDecimal());
        assertEquals(IconAmount.Unit.ICX.getValue(), amount.getDigit());

        amount = IconAmount.of(new BigDecimal("0.1"), 16);
        assertEquals(new BigDecimal("0.1"), amount.asDecimal());
        assertEquals(16, amount.getDigit());
    }

    @Test
    void testToLoop() {
        BigInteger loop = new BigInteger("1000000000000000000");

        IconAmount amount = IconAmount.of("1", IconAmount.Unit.ICX);
        assertEquals(loop, amount.toLoop());

        amount = IconAmount.of("1000000000000000000", IconAmount.Unit.LOOP);
        assertEquals(loop, amount.toLoop());

        amount = IconAmount.of(new BigInteger("1"), IconAmount.Unit.ICX);
        assertEquals(loop, amount.toLoop());

        amount = IconAmount.of(new BigInteger("1000"), IconAmount.Unit.ICX);
        assertEquals(new BigInteger("1000000000000000000000"), amount.toLoop());

        amount = IconAmount.of("0.1", IconAmount.Unit.ICX);
        assertEquals(new BigInteger("100000000000000000"), amount.toLoop());

        amount = IconAmount.of(new BigDecimal("0.1"), IconAmount.Unit.ICX);
        assertEquals(new BigInteger("100000000000000000"), amount.toLoop());
    }

    @Test
    void testConvertUnit() {

        BigDecimal loop = new BigDecimal("1000000000000000000");

        IconAmount amount = IconAmount.of("1", IconAmount.Unit.ICX);
        assertEquals(new BigInteger("10"), amount.convertUnit(17).asInteger());

        amount = IconAmount.of("1", IconAmount.Unit.ICX);
        assertEquals(new BigInteger("100"), amount.convertUnit(16).asInteger());

        amount = IconAmount.of(new BigDecimal("1"), IconAmount.Unit.ICX);
        assertEquals(new BigDecimal("0.1"), amount.convertUnit(19).asDecimal());

        amount = IconAmount.of(new BigDecimal("1"), IconAmount.Unit.ICX);
        assertEquals(new BigInteger("1000000000000000000"), amount.convertUnit(IconAmount.Unit.LOOP).asInteger());
    }

}
