package foundation.icon.icx.data;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class BytesTest {

    @Test
    void testCreate() {

        byte[] byteArray = new byte[]{
                (byte) 10, (byte) 16, (byte) 8
        };
        String byteArrayToHex = Hex.toHexString(byteArray);

        Assertions.assertArrayEquals(byteArray, new Bytes(byteArray).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(byteArrayToHex).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(new BigInteger(1, byteArray)).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(new BigInteger(byteArrayToHex, 16)).toByteArray());

        // first byte 0xff
        byteArray = new byte[]{
                (byte) -1, (byte) 128
        };
        byteArrayToHex = Hex.toHexString(byteArray);

        Assertions.assertArrayEquals(byteArray, new Bytes(byteArray).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(byteArrayToHex).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(new BigInteger(1, byteArray)).toByteArray());
        Assertions.assertArrayEquals(byteArray, new Bytes(new BigInteger(byteArrayToHex, 16)).toByteArray());

        String stringVaule = "string value";
        String hexValue = "ffaabb";
        String tx = "0x2600770376fbf291d3d445054d45ed15280dd33c2038931aace3f7ea2ab59dbc";

        Assertions.assertEquals(hexValue, new Bytes(hexValue).toHexString(false));
        Assertions.assertEquals(tx, new Bytes(tx).toString());

        byte[] secret = new SecureRandom().generateSeed(32);
        Assertions.assertEquals(secret, new Bytes(secret).toByteArray());

        byte[] b = stringVaule.getBytes(StandardCharsets.UTF_8);
        Assertions.assertEquals(b, new Bytes(b).toByteArray());
    }

    @Test
    void testThrow() {
        String stringVaule = "string value";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Bytes(stringVaule);
        });

        byte[] b = stringVaule.getBytes(StandardCharsets.UTF_8);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Bytes.toBytesPadded(b, b.length - 1);
        });

        String oddHex = "4d2";
        Assertions.assertThrows(DecoderException.class, () -> {
            new Bytes(oddHex);
        });

    }
}
