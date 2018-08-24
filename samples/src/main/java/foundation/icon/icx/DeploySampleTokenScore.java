package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class DeploySampleTokenScore {

    public final String URL = "http://localhost:9000/api/v3";
    public final String PRIVATE_KEY_STRING =
            "2d42994b2f7735bbc93a3e64381864d06747e574aa94655c516f9ad0a74eed79";

    private IconService iconService;
    private Wallet wallet;

    public DeploySampleTokenScore() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
        wallet = KeyWallet.load(PRIVATE_KEY_STRING);
    }

    public void sendTransaction() throws IOException {
        String contentType = "application/zip";
        byte[] content = readFile();
        BigInteger networkId = new BigInteger("3");
        Address fromAddress = wallet.getAddress();
        Address toAddress = new Address("cx0000000000000000000000000000000000000000");
        BigInteger stepLimit = new BigInteger("14685000");
        long timestamp = System.currentTimeMillis() * 1000L;
        BigInteger nonce = new BigInteger("1");

        BigInteger initialSupply = new BigInteger("10000");
        BigInteger decimals = new BigInteger("18");
        String tokenName = "ICON";
        String tokenSymbol = "ICX";

        RpcObject params = new RpcObject.Builder()
                .put("initialSupply", new RpcValue(initialSupply))
                .put("decimals", new RpcValue(decimals))
                .put("name", new RpcValue(tokenName))
                .put("symbol", new RpcValue(tokenSymbol))
                .build();

        Transaction transaction = TransactionBuilder.of(networkId)
                .from(fromAddress)
                .to(toAddress)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .nonce(nonce)
                .deploy(contentType, content)
                .params(params)
                .build();

        SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
        Bytes hash = iconService.sendTransaction(signedTransaction).execute();
        System.out.println("txHash:"+hash);
    }

    private byte[] readFile() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("test.zi").getFile());
        return readBytes(file);
    }

    private byte[] readBytes(File file) throws IOException {
        int length = (int) file.length();
        if (length > Integer.MAX_VALUE) throw new OutOfMemoryError("File is too big!!");
        byte[] result = new byte[length];
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
        inputStream.readFully(result);
        return result;
    }

    public static void main(String[] args) throws IOException {
        new DeploySampleTokenScore().sendTransaction();
    }
}
