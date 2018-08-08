package foundation.icon.icx.crypto;


import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static org.web3j.crypto.Keys.PUBLIC_KEY_SIZE;

/**
 * Implementation from
 * https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/Keys.java
 * Crypto key utilities.
 */
public class IconKeys {

    public static ECKeyPair createEcKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = Keys.createSecp256k1KeyPair();
        return ECKeyPair.create(keyPair);
    }

    public static Address getAddress(ECKeyPair ecKeyPair) {
        return new Address(Address.AddressPrefix.EOA, getAddressHash(ecKeyPair.getPublicKey()));
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
            cleanInput.matches("^[0-9a-fA-F]{40}$");
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
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

    public static byte[] getHexAddress(String input) {
        String cleanInput = cleanHexPrefix(input);
        return Numeric.hexStringToByteArray(cleanInput);
    }

    public static String getHexAddress(byte[] input) {
        return Numeric.toHexStringNoPrefix(input);
    }

}
