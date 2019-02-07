package foundation.icon.icx.data;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;


class BytesTest {

    @Test
    void testCreate() {

        byte[] byteArray = new byte[]{
                (byte) 10, (byte) 16, (byte) 8
        };
        String hexValue = Hex.toHexString(byteArray);
        BigInteger bigIntegerValue = new BigInteger(byteArray);

        Assertions.assertArrayEquals(byteArray, new Bytes(byteArray).toByteArray());
        Assertions.assertArrayEquals(Hex.decode(hexValue), new Bytes(hexValue).toByteArray());
        Assertions.assertArrayEquals(bigIntegerValue.toByteArray(), new Bytes(bigIntegerValue).toByteArray());

        String tx = "2600770376fbf291d3d445054d45ed15280dd33c2038931aace3f7ea2ab59dbc";
        Assertions.assertArrayEquals(Hex.decode(tx), new Bytes(tx).toByteArray());

        byte[] secret = new SecureRandom().generateSeed(32);
        Assertions.assertArrayEquals(secret, new Bytes(secret).toByteArray());

        String stringValue = "string value";
        byte[] b = stringValue.getBytes(StandardCharsets.UTF_8);
        Assertions.assertArrayEquals(b, new Bytes(b).toByteArray());

    }

    @Test
    void testBigInteger() {
        String hexValue = "ff80";

        // The first byte is sign bytes
        // positive value : { 0x00, 0xff, 0x80 }
        BigInteger positive = new BigInteger(hexValue, 16);
        // negative value : { 0x80 }
        BigInteger negative = new BigInteger(Hex.decode("ff0101"));

        Assertions.assertArrayEquals(positive.toByteArray(), new Bytes(positive).toByteArray());
        Assertions.assertArrayEquals(negative.toByteArray(), new Bytes(negative).toByteArray());

    }

    @Test
    void testThrow() {
        String stringValue = "string value";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Bytes(stringValue));

        byte[] b = stringValue.getBytes(StandardCharsets.UTF_8);
        Assertions.assertThrows(IllegalArgumentException.class, () -> Bytes.toBytesPadded(b, b.length - 1));

        String oddHex = "4d2";
        Assertions.assertThrows(DecoderException.class, () -> new Bytes(oddHex));
    }

    @Test
    void testEquals() {
        // same byte array
        String hex = "7979";
        byte[] byteArray = new byte[]{0x79, 0x79};
        BigInteger big = new BigInteger("31097");

        Bytes b1 = new Bytes(hex);
        Bytes b2 = new Bytes(byteArray);
        Bytes b3 = new Bytes(big);

        // reflexive
        compareFuncs(b1, b1, true);
        compareFuncs(b2, b2, true);
        compareFuncs(b3, b3, true);

        // symmetric
        compareFuncs(b1, b2, true);
        compareFuncs(b2, b1, true);

        // transitive
        compareFuncs(b1, b2, true);
        compareFuncs(b2, b3, true);
        compareFuncs(b3, b1, true);

        // nonullity
        compareFuncs(b1, null, false);
        compareFuncs(b2, null, false);
        compareFuncs(b3, null, false);
        compareFuncs(b3, null, false);

        // different
        String diff = "ffff";
        Bytes b4 = new Bytes(diff);
        compareFuncs(b3, b4, false);
    }

    private void compareFuncs(Bytes b1, Bytes b2, boolean expectEquals) {
        Assertions.assertEquals(expectEquals, b1.equals(b2));
    }

}
