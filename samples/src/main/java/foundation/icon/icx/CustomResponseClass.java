package foundation.icon.icx;

import foundation.icon.icx.data.Address;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcConverter;
import foundation.icon.icx.transport.jsonrpc.RpcItem;
import foundation.icon.icx.transport.jsonrpc.RpcItemCreator;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.math.BigInteger;

public class CustomResponseClass {

    public final String URL = "http://localhost:9000/api/v3";
    private final Address scoreAddress = new Address("cx0000000000000000000000000000000000000001");

    private IconService iconService;

    public CustomResponseClass() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        iconService = new IconService(new HttpProvider(httpClient, URL));
    }

    public void getStepCosts() throws IOException {

        iconService.addConverterFactory(new RpcConverter.RpcConverterFactory() {
            @Override
            public <T> RpcConverter<T> create(Class<T> type) {
                if (StepCost.class == type) {
                    return new RpcConverter<T>() {
                        @Override
                        public T convertTo(RpcItem object) {
                            RpcObject o = (RpcObject) object;
                            StepCost cost = new StepCost();
                            cost.defaultCost = o.getItem("default").asInteger();
                            cost.contractCall = o.getItem("contractCall").asInteger();
                            cost.contractUpdate = o.getItem("contractUpdate").asInteger();
                            cost.contractDestruct = o.getItem("contractDestruct").asInteger();
                            cost.contractCreate = o.getItem("contractCreate").asInteger();
                            cost.contractSet = o.getItem("contractSet").asInteger();
                            cost.set = o.getItem("set").asInteger();
                            cost.replace = o.getItem("replace").asInteger();
                            cost.input = o.getItem("input").asInteger();
                            cost.eventLog = o.getItem("eventLog").asInteger();
                            return (T) cost;
                        }

                        @Override
                        public RpcItem convertFrom(T object) {
                            return RpcItemCreator.create(object);
                        }
                    };
                }
                return null;
            }
        });

        IcxCall<StepCost> call = new IcxCall.Builder()
                .to(scoreAddress)
                .method("getStepCosts")
                .buildWith(StepCost.class);

        StepCost costs = iconService.query(call).execute();
        System.out.println("step costs:"+costs);
    }

    class StepCost {

        BigInteger defaultCost;
        BigInteger contractCall;
        BigInteger contractCreate;
        BigInteger contractUpdate;
        BigInteger contractDestruct;
        BigInteger contractSet;
        BigInteger set;
        BigInteger replace;
        BigInteger input;
        BigInteger eventLog;

        @Override
        public String toString() {
            return "StepCost{" +
                    "defaultCost=" + defaultCost +
                    ", contractCall=" + contractCall +
                    ", contractCreate=" + contractCreate +
                    ", contractUpdate=" + contractUpdate +
                    ", contractDestruct=" + contractDestruct +
                    ", contractSet=" + contractSet +
                    ", set=" + set +
                    ", replace=" + replace +
                    ", input=" + input +
                    ", eventLog=" + eventLog +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        new CustomResponseClass().getStepCosts();
    }
}
