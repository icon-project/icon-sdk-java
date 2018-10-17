package foundation.icon.icx;

import foundation.icon.icx.crypto.KeystoreException;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.CommonData;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class WalletExample {

	static void print(KeyWallet wallet) {
		System.out.println("address:"+wallet.getAddress());
		System.out.println("privateKey:"+wallet.getPrivateKey().toHexString(false));
		System.out.println();
	}

	public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,  IOException, KeystoreException {

		String dirPath = "./";
		// File of directory for keystorfile.
		File destinationDirectory = new File(dirPath);

		// Create keyWallet and store it as a keyStorefile
		System.out.println("Create KeyWallet");
		KeyWallet wallet1 = KeyWallet.create();
		print(wallet1);

		System.out.println("Store KeyWallet");
		String fileName = KeyWallet.store(wallet1, CommonData.PASSWORD, destinationDirectory);
		System.out.println("keystore fileName:" + fileName);
		System.out.println();

		// Loads a wallet from bytes of the private key
		System.out.println("Load KeyWallet using privatekey");
		KeyWallet wallet2 = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
		print(wallet2);


		//  Loads a wallet from a key store file
		System.out.println("Load KeyWallet using keystore file");
		File file = new File(destinationDirectory, fileName);
		KeyWallet wallet3 = KeyWallet.load(CommonData.PASSWORD, file);
		print(wallet3);
	}

}
