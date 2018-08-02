package foundation.icon.icx.transport.jsonrpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import foundation.icon.icx.transport.jsonrpc.RpcField.RpcValueException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RpcValueTest {
    private RpcValue plainStringValue;
    private RpcValue bytesValue;
    private RpcValue oddIntegerValue;
    private RpcValue evenIntegerValue;
    private RpcValue booleanValue;

    @BeforeEach
    void initAll() {
        plainStringValue = new RpcValue("string value");
        bytesValue = new RpcValue(new byte[]{1, 2, 3, 4, 5});
        oddIntegerValue = new RpcValue(new BigInteger("1234"));
        evenIntegerValue = new RpcValue(new BigInteger("61731"));
        booleanValue = new RpcValue(true);
    }

    @Test
    void testAsString() {
        assertEquals("string value", plainStringValue.asString());
        assertEquals("0x0102030405", bytesValue.asString());
        assertEquals("0x4d2", oddIntegerValue.asString());
        assertEquals("0xf123", evenIntegerValue.asString());
        assertEquals("0x1", booleanValue.asString());
    }

    @Test
    void testAsBytes() {
        assertThrows(RpcValueException.class, plainStringValue::asBytes);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, bytesValue.asBytes());
        assertThrows(RpcValueException.class, oddIntegerValue::asBytes);
        assertArrayEquals(new byte[]{-15, 35}, evenIntegerValue.asBytes());
        assertThrows(RpcValueException.class, booleanValue::asBytes);
    }

    @Test
    void testAsInteger() {
        assertThrows(RpcValueException.class, plainStringValue::asInteger);
        assertEquals(new BigInteger("0102030405", 16), bytesValue.asInteger());
        assertEquals(new BigInteger("1234"), oddIntegerValue.asInteger());
        assertEquals(new BigInteger("61731"), evenIntegerValue.asInteger());
        assertEquals(new BigInteger("1"), booleanValue.asInteger());

        RpcValue minusHex = new RpcValue("-0x4d2");
        assertEquals(new BigInteger("-1234"), minusHex.asInteger());

        RpcValue plusHex = new RpcValue("+0x4d2");
        assertThrows(RpcValueException.class, plusHex::asInteger);
    }

    @Test
    void testAsBoolean() {
        assertThrows(RpcValueException.class, plainStringValue::asBoolean);
        assertThrows(RpcValueException.class, bytesValue::asBoolean);
        assertThrows(RpcValueException.class, oddIntegerValue::asBoolean);
        assertThrows(RpcValueException.class, evenIntegerValue::asBoolean);
        assertTrue(booleanValue.asBoolean());
    }

}
