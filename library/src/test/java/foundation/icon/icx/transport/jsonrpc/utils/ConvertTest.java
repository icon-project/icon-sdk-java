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

package foundation.icon.icx.transport.jsonrpc.utils;

import foundation.icon.icx.transport.utils.Convert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ConvertTest {

    @Test
    void testFromLoop() {
        BigInteger loop = new BigInteger("1000000000000000000");
        BigDecimal icx = Convert.fromLoop(loop, Convert.Unit.ICX);
        assertEquals(BigDecimal.ONE, icx);

        loop = new BigInteger("1000000000000000000000");
        icx = Convert.fromLoop(loop, Convert.Unit.ICX);
        assertEquals(new BigDecimal("1000"), icx);

        loop = new BigInteger("100000000000000000");
        icx = Convert.fromLoop(loop, Convert.Unit.ICX);
        assertEquals(new BigDecimal("0.1"), icx);

        String stringValue = "100000000000000000";
        icx = Convert.fromLoop(stringValue, Convert.Unit.ICX);
        assertEquals(new BigDecimal("0.1"), icx);

        stringValue = "200000000000000000000";
        icx = Convert.fromLoop(stringValue, Convert.Unit.ICX);
        assertEquals(new BigDecimal("200"), icx);

        loop = new BigInteger("1000000000000000000");
        icx = Convert.fromLoop(loop, 17);
        assertEquals(new BigDecimal("10"), icx);

        loop = new BigInteger("1000000000000000000");
        icx = Convert.fromLoop(loop, 16);
        assertEquals(new BigDecimal("100"), icx);

        loop = new BigInteger("1");
        icx = Convert.fromLoop(loop, 0);
        assertEquals(new BigDecimal("1"), icx);

        icx = Convert.fromLoop("1", 1);
        assertEquals(new BigDecimal("0.1"), icx);
    }

    @Test
    void testToLoop() {
        BigDecimal icx = BigDecimal.ONE;
        BigInteger loop = Convert.toLoop(icx, Convert.Unit.ICX);
        assertEquals(new BigInteger("1000000000000000000"), loop);

        icx = new BigDecimal("200");
        loop = Convert.toLoop(icx, Convert.Unit.ICX);
        assertEquals(new BigInteger("200000000000000000000"), loop);

        icx = new BigDecimal("0.1");
        loop = Convert.toLoop(icx, Convert.Unit.ICX);
        assertEquals(new BigInteger("100000000000000000"), loop);

        String stringValue = "1";
        loop = Convert.toLoop(stringValue, Convert.Unit.ICX);
        assertEquals(new BigInteger("1000000000000000000"), loop);

        stringValue = "200";
        loop = Convert.toLoop(stringValue, Convert.Unit.ICX);
        assertEquals(new BigInteger("200000000000000000000"), loop);

        stringValue = "0.1";
        loop = Convert.toLoop(stringValue, Convert.Unit.ICX);
        assertEquals(new BigInteger("100000000000000000"), loop);

        stringValue = "0.1";
        loop = Convert.toLoop(stringValue, 1);
        assertEquals(new BigInteger("1"), loop);

        stringValue = "0.1";
        loop = Convert.toLoop(stringValue, 2);
        assertEquals(new BigInteger("10"), loop);
    }
}
