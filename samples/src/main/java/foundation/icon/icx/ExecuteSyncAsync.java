package foundation.icon.icx;

import foundation.icon.icx.data.Block;
import foundation.icon.icx.transport.http.HttpProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class ExecuteSyncAsync {

    private IconService iconService;

    private ExecuteSyncAsync() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, Constants.SERVER_URL, 3));
    }

    private void sync() throws IOException {
        Block block = iconService.getLastBlock().execute();
        System.out.println("sync call block hash:" + block.getBlockHash());
    }

    private void async() {
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
        ExecuteSyncAsync call = new ExecuteSyncAsync();
        call.sync();
        call.async();
    }
}
