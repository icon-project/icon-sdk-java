package foundation.icon.icx;

import foundation.icon.icx.data.Block;
import foundation.icon.icx.data.ConfirmedTransaction;
import foundation.icon.icx.data.ScoreApi;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.jsonrpc.Request;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;

import java.math.BigInteger;
import java.util.List;

/**
 * IconService which provides APIs of ICON network
 */
public class IconService {

    private Provider provider;

    public IconService(Provider provider) {
        this.provider = provider;
    }

    /**
     * Get the total number of issued coins.
     *
     * @return A BigNumber instance of the total number of coins.
     */
    public Call<BigInteger> getTotalSupply() {
        Request<RpcObject> request = new Request<>("icx_getTotalSupply", null);
        return provider.request(request, BigInteger.class);
    }

    /**
     * Get the balance of an address.
     *
     * @param address The address to get the balance of.
     * @return A BigNumber instance of the current balance for the given address in loop.
     */
    public Call<BigInteger> getBalance(String address) {
        RpcObject params = new RpcObject.Builder()
                .put("address", new RpcValue(address))
                .build();
        Request<RpcObject> request = new Request<>("icx_getBalance", params);
        return provider.request(request, BigInteger.class);
    }

    /**
     * Get a block matching the block number.
     *
     * @param height The block number
     * @return The Block object
     */
    public Call<Block> getBlock(BigInteger height) {
        RpcObject params = new RpcObject.Builder()
                .put("height", new RpcValue(height))
                .build();
        Request<RpcObject> request = new Request<>("icx_getBlockByHeight", params);
        return provider.request(request, Block.class);
    }

    /**
     * Get a block matching the block hash.
     *
     * @param hash The block hash (without hex prefix) or the string 'latest'
     * @return The Block object
     */
    public Call<Block> getBlock(String hash) {
        if (hash.equals("latest")) return getLastBlock();

        RpcObject params = new RpcObject.Builder()
                .put("hash", new RpcValue(hash))
                .build();
        Request<RpcObject> request = new Request<>("icx_getBlockByHash", params);
        return provider.request(request, Block.class);
    }

    /**
     * Get the latest block.
     *
     * @return The Block object
     */
    public Call<Block> getLastBlock() {
        Request<RpcObject> request = new Request<>("icx_getLastBlock", null);
        return provider.request(request, Block.class);
    }

    /**
     * Get information about api function in score
     *
     * @param scoreAddress The address to get APIs
     * @return The ScoreApi object
     */
    public Call<List<ScoreApi>> getScoreApi(String scoreAddress) {
        RpcObject params = new RpcObject.Builder()
                .put("address", new RpcValue(scoreAddress))
                .build();
        Request<RpcObject> request = new Request<>("icx_getScoreApi", params);
        return provider.request(request, (Class<List<ScoreApi>>) ((Class) List.class));
    }


    /**
     * Get a transaction matching the given transaction hash.
     *
     * @param hash The transaction hash
     * @return The Transaction object
     */
    public Call<ConfirmedTransaction> getTransaction(String hash) {
        RpcObject params = new RpcObject.Builder()
                .put("txHash", new RpcValue(hash))
                .build();
        Request<RpcObject> request = new Request<>("icx_getTransactionByHash", params);
        return provider.request(request, ConfirmedTransaction.class);
    }

    /**
     * Get the result of a transaction by transaction hash.
     *
     * @param hash The transaction hash
     * @return The TransactionResult object
     */
    public Call<TransactionResult> getTransactionResult(String hash) {
        RpcObject params = new RpcObject.Builder()
                .put("txHash", new RpcValue(hash))
                .build();
        Request<RpcObject> request = new Request<>("icx_getTransactionResult", params);
        return provider.request(request, TransactionResult.class);
    }

    /**
     * Calls a SCORE API just for reading
     *
     * @param icxCall instance of IcxCall
     * @param <I>     input type of the parameter
     * @return the Call object can execute a request
     */
    public <I, O> Call<O> query(IcxCall<I, O> icxCall) {
        Request<IcxCall<I, O>> request = new Request<>("icx_call", icxCall);
        return provider.request(request, icxCall.responseType());
    }

    /**
     * Sends a transaction that changes the states of account
     *
     * @param signedTransaction parameters including signatures
     * @return the Call object can execute a request (result type is txHash)
     */
    public Call<String> sendTransaction(SignedTransaction signedTransaction) {
        Request<RpcObject> request = new Request<>(
                "icx_sendTransaction", signedTransaction.getParams());
        return provider.request(request, String.class);
    }

}
