package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class SendIcxTransaction {

    public final String URL = "http://localhost:9000/api/v3";
    public final String PRIVATE_KEY_STRING =
            "2d42994b2f7735bbc93a3e64381864d06747e574aa94655c516f9ad0a74eed79";

    private IconService iconService;
    private Wallet wallet;

    public SendIcxTransaction() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
        wallet = KeyWallet.load(PRIVATE_KEY_STRING);
    }

    public void sendTransaction() throws IOException {
        BigInteger networkId = new BigInteger("3");
        Address fromAddress = wallet.getAddress();
        Address toAddress = new Address("hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31");

        BigInteger value = IconAmount.of("1", IconAmount.Unit.ICX).toLoop();
        BigInteger stepLimit = new BigInteger("75000");
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger nonce = new BigInteger("1");

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(fromAddress)
                .to(toAddress)
                .value(value)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(nonce)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash:"+hash);
    }

    public static void main(String[] args) throws IOException {
        new SendIcxTransaction().sendTransaction();
    }
}
