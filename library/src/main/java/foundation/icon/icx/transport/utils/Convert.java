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

package foundation.icon.icx.transport.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Convert {

    public static BigDecimal fromLoop(BigInteger number, int digit) {
        BigDecimal n = new BigDecimal(number);
        BigDecimal d = BigDecimal.TEN.pow(digit);
        return n.divide(d);
    }

    public static BigDecimal fromLoop(BigInteger number, Unit unit) {
        return fromLoop(number, unit.getValue());
    }

    public static BigDecimal fromLoop(String number, Unit unit) {
        return fromLoop(new BigInteger(number), unit.getValue());
    }

    public static BigDecimal fromLoop(String number, int digit) {
        return fromLoop(new BigInteger(number), digit);
    }

    public static BigInteger toLoop(BigDecimal number, int digit) {
        BigDecimal d = BigDecimal.TEN.pow(digit);
        return number.multiply(d).toBigInteger();
    }

    public static BigInteger toLoop(BigDecimal number, Unit unit) {
        return toLoop(number, unit.getValue());
    }

    public static BigInteger toLoop(String number, Unit unit) {
        return toLoop(new BigDecimal(number), unit.getValue());
    }

    public static BigInteger toLoop(String number, int digit) {
        return toLoop(new BigDecimal(number), digit);
    }

    public enum Unit {
        LOOP(0),
        ICX(18);

        int digit;

        Unit(int digit) {
            this.digit = digit;
        }

        public int getValue() {
            return digit;
        }
    }
}
