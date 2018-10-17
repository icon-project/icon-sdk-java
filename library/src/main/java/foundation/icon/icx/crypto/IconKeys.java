package foundation.icon.icx.crypto;


import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;


/**
 * Implementation from
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Keys.java
 * Crypto key utilities.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class IconKeys {

    public static final int PRIVATE_KEY_SIZE = 32;
    public static final int PUBLIC_KEY_SIZE = 64;

    public static final int ADDRESS_SIZE = 160;
    public static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;
    private static final SecureRandom SECURE_RANDOM;
    private static int isAndroid = -1;

    static {
        if (isAndroidRuntime()) {
            new LinuxSecureRandom();
            Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        } else {
            Security.addProvider(new BouncyCastleProvider());
        }
        SECURE_RANDOM = new SecureRandom();
    }

    private IconKeys() { }

    public static Bytes createPrivateKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        String provider = isAndroidRuntime()?"SC":"BC";
        KeyPairGenerator keyPairGenerator= KeyPairGenerator.getInstance("ECDSA", provider);
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecGenParameterSpec, secureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        if (isAndroidRuntime()) {
            BigInteger privateKey = ((org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey) keyPair.getPrivate()).getD();
            return new Bytes(privateKey.toString(16));
        } else {
            return new Bytes(((BCECPrivateKey) keyPair.getPrivate()).getD());
        }
    }

    public static Bytes getPublicKey(Bytes privateKey) {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey.toByteArray()));
        byte[] publicKeyBytes = pointQ.getEncoded(false);
        return new Bytes(Arrays.copyOfRange(publicKeyBytes, 1, publicKeyBytes.length));
    }

    public static Address getAddress(Bytes publicKey) {
        return new Address(Address.AddressPrefix.EOA, getAddressHash(publicKey.toByteArray(PUBLIC_KEY_SIZE)));
    }

    public static byte[] getAddressHash(BigInteger publicKey) {
        return getAddressHash(new Bytes(publicKey).toByteArray(PUBLIC_KEY_SIZE));
    }

    public static byte[] getAddressHash(byte[] publicKey) {
        byte[] hash = new SHA3.Digest256().digest(publicKey);

        int length = 20;
        byte[] result = new byte[20];
        System.arraycopy(hash, hash.length - 20, result, 0, length);
        return result;
    }

    public static boolean isValidAddress(Address input) {
        return isValidAddress(input.toString());
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = cleanHexPrefix(input);
        try {
            return cleanInput.matches("^[0-9a-f]{40}$") && cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidAddressBody(byte[] body) {
        return body.length == 20 &&
                IconKeys.isValidAddress(Hex.toHexString(body));
    }

    public static boolean isContractAddress(Address address) {
        return address.getPrefix() == Address.AddressPrefix.CONTRACT;
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public static boolean containsHexPrefix(String input) {
        return getAddressHexPrefix(input) != null;
    }

    public static Address.AddressPrefix getAddressHexPrefix(String input) {
        return Address.AddressPrefix.fromString(input.substring(0, 2));
    }

    public static SecureRandom secureRandom() {
        return SECURE_RANDOM;
    }

    public static boolean isAndroidRuntime() {
        if (isAndroid == -1) {
            final String runtime = System.getProperty("java.runtime.name");
            isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
        }
        return isAndroid == 1;
    }

}
