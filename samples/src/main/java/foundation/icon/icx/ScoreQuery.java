package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class ScoreQuery {

    public final String URL = "http://localhost:9000/api/v3";
    private final Address scoreAddress = new Address("cx2e6032c7598b882da4b156ed9334108a5b87f2dc");

    private IconService iconService;

    public ScoreQuery() {
        HttpLoggingInterceptor loggning = new HttpLoggingInterceptor();
        loggning.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggning)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void query() throws IOException {
        Address fromAddress = new Address("hx4873b94352c8c1f3b2f09aaeccea31ce9e90bd31");

        RpcObject params = new RpcObject.Builder()
                .put("_owner", new RpcValue(fromAddress))
                .build();

        IcxCall<RpcItem> call = new IcxCall.Builder()
                .from(fromAddress)
                .to(scoreAddress)
                .method("balanceOf")
                .params(params)
                .build();

        RpcItem result = iconService.query(call).execute();
        System.out.println("result:"+result.asInteger());
    }

    public static void main(String[] args) throws IOException {
        new ScoreQuery().query();
    }

}
