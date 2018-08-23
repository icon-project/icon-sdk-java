package foundation.icon.icx;

import foundation.icon.icx.data.Block;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class CallSyncAsync {

    public static final String URL = "http://localhost:9000/api/v3";
    private IconService iconService;

    public CallSyncAsync() {
        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggning)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void sync() throws IOException {
        Block block = iconService.getLastBlock().execute();
        System.out.println("sync call block hash:" + block.getBlockHash());
    }

    public void async() {
        iconService.getLastBlock().execute(new Callback<Block>() {
            @Override
            public void onSuccess(Block block) {
                System.out.println("async call block hash:" + block.getBlockHash());
            }

            @Override
            public void onFailure(Exception exception) {
                // exception
                System.out.println("exception:" + exception.getMessage());
            }
        });
    }

    public static void main(String[] args) throws IOException {
        CallSyncAsync call = new CallSyncAsync();
        call.sync();
        call.async();
    }
}
