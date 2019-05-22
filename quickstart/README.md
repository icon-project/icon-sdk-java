# ICON SDK Java Quickstart

This is an example project of ICON SDK Java.
In this project, the examples are implemented as follows.

| Example                 | Description |
| ----------------------- | ----------- |
| WalletExample           | An example of creating and loading a `Keywallet`. |
| IcxTransactionExample   | An example of transferring ICX and confirming the result. |
| DeployTokenExample      | An example of deploying token. |
| TokenTransactionExample | An example of transferring IRC token and confirming the result. |
| SyncBlockExample        | An example of checking block confirmation and printing the ICX and token transfer information. |


## Using the SDK

### IconService

Create an `IconService` instance to communicate with ICON nodes.

`IconService` allows you to send transactions, check the result and block information, etc.

```java
IconService iconService = new IconService(new HttpProvider("http://localhost:9000", 3));
```


### Wallet

This example shows how to create a new `KeyWallet` or load wallet with a private key or Keystore file.

#### Create a wallet

Create new EOA by calling `create` function. After creation, the address and private key can be looked up.

```java
KeyWallet wallet = KeyWallet.create(); // Wallet Creation
System.out.println("address:" + wallet.getAddress()); // Address Check
System.out.println("privateKey:" + wallet.getPrivateKey()); // PrivateKey Check

// Output
address:hx4d37a7013c14bedeedbe131c72e97ab337aea159
privateKey:00e1d6541bfd8be7d88be0d24516556a34ab477788022fa07b4a6c1d862c4de516
```

#### Load a wallet

You can call existing EOA by calling `load` function.

After creation, address and private key can be looked up.

```java
String privateKey;
KeyWallet wallet = KeyWallet.load(new Bytes(privateKey));  // Load keyWallet with privateKey
System.out.println("address:" + wallet.getAddress()); // Address lookup
System.out.println("privateKey:" + wallet.getPrivateKey()); // PrivateKey lookup
```

#### Store the wallet

After `KeyWallet` object creation, the Keystore file can be stored by calling `store` function.

After calling `store`, the Keystore file’s name can be looked up with the returned value.

```java
String password;
KeyWallet wallet; /* create or load keywallet */
File destinationDirectory = new File(/* directory Path */);
String fileName = KeyWallet.store(wallet, password, destinationDirectory);
System.out.println("fileName:" + fileName); // Keystore file name output

// Output
fileName:UTC--2018-08-30T03-27-41.768000000Z--hx4d37a7013c14bedeedbe131c72e97ab337aea159.json
```

### ICX Transfer

This example shows how to transfer ICX and check the result.

*For the KeyWallet and IconService creation, please refer to the information above.*

#### ICX transfer transaction

In this example, you can create a `KeyWallet` with `CommonData.PRIVATE_KEY_STRING` and transfer 1 ICX to `CommonData.ADDRESS_1`.

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
Address toAddress = new Address(CommonData.ADDRESS_1);
// 1 ICX -> 1000000000000000000 loop conversion
BigInteger value = IconAmount.of("1", IconAmount.Unit.ICX).toLoop();
```

You can get a default step cost to transfer ICX as follows.

```java
// Get apis that provides Governance SCORE
public Map<String, ScoreApi> getGovernanceScoreApi() throws IOException {
    // GOVERNANCE_ADDRESS : cx0000000000000000000000000000000000000001
    List<ScoreApi> apis = iconService.getScoreApi(GOVERNANCE_ADDRESS).execute();
    return apis.stream().collect(Collectors.toMap(ScoreApi::getName, api -> api));
}

// You can use "governance score apis" to get step costs.
public BigInteger getDefaultStepCost() throws IOException {
    String methodName = "getStepCosts";

    // Check input and output parameters of api if you need
    Map<String, ScoreApi> governanceScoreApiMap = getGovernanceScoreApi();
    ScoreApi api = governanceScoreApiMap.get(methodName);
    System.out.println("[getStepCosts]\n inputs:" + api.getInputs() + "\n outputs:" + api.getOutputs());

    Call<RpcItem> call = new Call.Builder()
        .to(GOVERNANCE_ADDRESS)	// cx0000000000000000000000000000000000000001
        .method(methodName)
        .build();
    RpcItem result = iconService.call(call).execute();
    return result.asObject().getItem("default").asInteger();
}
```

Generate a transaction using the values above.

```java
// Network ID ("1" for Mainnet, "2" for Testnet, etc)
BigInteger networkId = new BigInteger("3");
// Recommended step limit to transfer icx:
// use 'default' step cost in the response of getStepCosts API
BigInteger stepLimit = getDefaultStepCost(); // Please refer to the above description.

// Timestamp is used to prevent the identical transactions. Only current time is required (Standard unit: us)
// If the timestamp is considerably different from the current time, the transaction will be rejected.
long timestamp = System.currentTimeMillis() * 1000L;

//Enter transaction information
Transaction transaction = TransactionBuilder.newBuilder()
        .nid(networkId)
        .from(fromAddress)
        .to(toAddress)
        .value(value)
        .stepLimit(stepLimit)
        .timestamp(new BigInteger(Long.toString(timestamp)))
        .build();
```

Generate a `SignedTransaction` to add the signature of the transaction.

```java
// Create signature of the transaction
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);
// Read params to transfer to nodes
System.out.println(signedTransaction.getProperties());
// Send the transaction
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
```

#### Check the transaction result

After a transaction is sent, the result can be looked up with the returned hash value.

In this example, you can check your transaction result in every 2 seconds because of the block confirmation time. Checking the result is as follows:

```java
// Check the result with the transaction hash
TransactionResult result = iconService.getTransactionResult(hash).execute();
System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());

// Output
transaction status(1:success, 0:failure):1
```

You can check the following information using the `TransactionResult`.

- status : 1 (success), 0 (failure)
- to : transaction’s receiving address
- failure : Only exists if the status is 0(failure). code(str), message(str) property included
- txHash : transaction hash
- txIndex : transaction index in a block
- blockHeight : Block height of the transaction
- blockHash : Block hash of the transaction
- cumulativeStepUsed : Accumulated amount of consumed step until the transaction is executed in block
- stepUsed : Consumed step amount to send the transaction
- stepPrice : Consumed step price to send the transaction
- scoreAddress : SCORE address if the transaction generated SCORE (optional)
- eventLogs : List of EventLogs written during the execution of the transaction.
- logsBloom : Bloom Filter of the indexed data of the Eventlogs.

#### Check the ICX balance

In this example, you can check the ICX balance by looking up the transaction before and after the transaction.

ICX balance can be confirmed by calling `getBalance` function from `IconService`

```java
KeyWallet wallet; /* create or load */
// Check the wallet balance
BigInteger balance = iconService.getBalance(wallet.getAddress()).execute();
System.out.println("balance:" + balance));

// Output
balance:5000000000000000000
```

### Token Deploy and Transfer

This example shows how to deploy a token and check the result. After that, shows how to send tokens and check the balance.

*For KeyWallet and IconService generation, please refer to the information above.*

#### Token deploy transaction

You need a SCORE project to deploy token.

In this example, you will use ‘sampleToken.zip’ from the ‘resources’ folder.

* sampleToken.zip: SampleToken SCORE project zip file.

Generate a `KeyWallet` using `CommonData.PRIVATE_KEY_STRING`, then read the binary data from ‘sampleToken.zip’

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
// Read sampleToken.zip from ‘resources’ folder.
byte[] content = /* score binary */
```

Prepare basic information of the token you want to deploy and make a parameter object.

```java
BigInteger initialSupply = new BigInteger("100000000000");
BigInteger decimals = new BigInteger("18");

RpcObject params = new RpcObject.Builder()
	.put("_initialSupply", new RpcValue(initialSupply))
	.put("_decimals", new RpcValue(decimals))
	.build();
```

Generate a raw transaction to deploy a token SCORE without the `stepLimit` value.

```java
Transaction transaction = TransactionBuilder.newBuilder()
	.nid(networkId)
	.from(wallet.getAddress())
	.to(CommonData.SCORE_INSTALL_ADDRESS)
	.timestamp(new BigInteger(Long.toString(timestamp)))
	.deploy(contentType, content)
	.params(params)
	.build();
```

Get an estimated Step value using `estimateStep` API of `IconService`.

```java
BigInteger estimatedStep = iconService.estimateStep(transaction).execute();
```

Generate a `SignedTransaction` with the same raw transaction and the estimated Step.
Note that the estimation can be smaller or larger than the actual amount of step to be used by the transaction.
So we need to add some margin to the estimation when you set the `stepLimit` of the `SignedTransaction`.

```java
// Set some margin for the operation of `on_install`
BigInteger margin = BigInteger.valueOf(10000);

SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep.add(margin));
```

Calling `sendTransaction` API of `IconService` will return the transaction hash.

```java
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

// Print Transaction Hash
System.out.println("txHash:"+txHash);

// Output
txHash:0x6b17886de346655d96373f2e0de494cb8d7f36ce9086cb15a57d3dcf24523c8f
```

#### Check the Result

After sending the transaction, you can check the result with the returned hash value.

In this example, you can check your transaction result in every 2 seconds because of the block confirmation time.

If the transaction succeeds, you can check the `scoreAddress` from the result.

You can use the SCORE after the SCORE passes the audit and is finally accepted to deploy.

```java
// Checking the results with transaction hash
TransactionResult result = iconService.getTransactionResult(hash).execute();
System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());
System.out.println("created score address:"+result.getScoreAddress());
System.out.println("waiting accept score...");

// Output
transaction status(1:success, 0:failure):1
created score address:cxd7fce67cc95b731dfbfdd8c8b34e8a5d0664a9ed
waiting accept score...
```

*For the 'TransactionResult', please refer to the `IcxTransactionExample`.*

#### Token transfer transaction

You can get the token SCORE address by checking the `scoreAddress` from the deploy transaction result above, and use this to send the token.

You can generate a `KeyWallet` using `CommonData.PRIVATE_KEY_STRING` just like in the case of `IcxTransactionExample`, then send 1 Token to `CommonData.ADDRESS_1`.

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
Address toAddress = new Address(CommonData.ADDRESS_1);

// Deploy a token SCORE and get the SCORE address
Address tokenAddress = new DeployTokenExample().deploy(wallet);

int tokenDecimals = 18;	// token decimal
// 1 ICX -> 1000000000000000000 conversion
BigInteger value = IconAmount.of("1", tokenDecimals).toLoop();
```

Generate a transaction with the given parameters above.
You have to add receiving address and value to `RpcObject` to send token.

```java
// Network ID ("1" for Mainnet, "2" for Testnet, etc)
BigInteger networkId = new BigInteger("3");
// Transaction creation time (timestamp is in the microsecond)
long timestamp = System.currentTimeMillis() * 1000L;
// 'transfer' as a methodName means to transfer token
// https://github.com/icon-project/IIPs/blob/master/IIPS/iip-2.md
String methodName = "transfer";

// Enter receiving address and the token value.
// You must enter the given key name ("_to", "_value"). Otherwise, the transaction will be rejected.
RpcObject params = new RpcObject.Builder()
	.put("_to", new RpcValue(toAddress))
	.put("_value", new RpcValue(value))
	.build();

// Create a raw transaction to transfer token (without stepLimit)
Transaction transaction = TransactionBuilder.newBuilder()
	.nid(networkId)
	.from(wallet.getAddress())
	.to(tokenAddress)
	.timestamp(new BigInteger(Long.toString(timestamp)))
	.call(methodName)
	.params(params)
	.build();

// Get an estimated step value
BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

// Create a signedTransaction with the sender's wallet and the estimatedStep
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep);
```

Call `sendTransaction` from `IconService` to check the transaction hash.

```java
// Send transaction
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

// Print transaction hash
System.out.println("txHash:"+txHash);

// Output
txHash:0x6b17886de346655d96373f2e0de494cb8d7f36ce9086cb15a57d3dcf24523c8f
```

#### Check the Result

You can check the result with the returned hash value of your transaction.

In this example, you check your transaction result in every 2 seconds because the block confirmation time is around 2 seconds.
Checking the result is as follows:

```java
// Check the result with the transaction hash
TransactionResult result = iconService.getTransactionResult(hash).execute();
System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());

// Output
transaction status(1:success, 0:failure):1
```

*For the TransactionResult, please refer to the `IcxTransactionExample`.*

#### Check the token balance

In this example, you can check the token balance before and after the transaction.

You can check the token balance by calling `balanceOf` from the token SCORE.

```java
KeyWallet wallet; /* create or load */
Address tokenAddress; /* returned from `new DeployTokenExample().deploy(wallet)` */
String methodName = "balanceOf"; /* Method name to check the balance */

// Enter the address to check balance.
// You must enter the given key name ("_owner"). Otherwise, your transaction will be rejected.
RpcObject params = new RpcObject.Builder()
    .put("_owner", new RpcValue(wallet.getAddress()))
    .build();

Call<RpcItem> call = new Call.Builder()
    .to(tokenAddress)
    .method(methodName)
    .params(params)
    .build();

RpcItem result = iconService.call(call).execute();
BigInteger balance = result.asInteger();
System.out.println("balance:"+balance));

// Output
balance:6000000000000000000
```

### Sync Block

This example shows how to read block information and print the transaction result for every block creation.

*Please refer to above for KeyWallet and IconService creation.*

#### Read block information

In this example, `getLastBlock` is called periodically in order to check the new blocks,

by updating the transaction information for every block creation.

```java
// Check the recent blocks
Block block = iconService.getLastBlock().execute();
System.out.println("block height:"+block.getHeight());

// Output
block height:237845
```

If a new block has been created, get the transaction list.

```java
List<ConfirmedTransaction> txList = block.getTransactions();
System.out.println("transaction hash:" + transaction.getTxHash());
System.out.println("transaction:" + transaction);

// Output
transaction hash:0x0d2b71ec3045bfd39f90da844cb03c58490fe364c7715cc299db346c1153fe0f
transaction:ConfirmedTransaction...stepLimit=0xfa0...value=0x21e19e...version=0x3...
```

You can check the following information using the `ConfirmedTransaction`:

- version : json rpc server version
- to : Receiving address of the transaction
- value: The amount of ICX coins to transfer to the address. If omitted, the value is assumed to be 0
- timestamp: timestamp of the transmitting transaction (unit: microseconds)
- nid : network ID
- signature: digital signature data of the transaction
- txHash : transaction hash
- dataType: A value indicating the type of the data item (call, deploy, message)
- data: Various types of data are included according to dataType.

#### Transaction output

After reading the `TransactionResult`, merge with `ConfirmedTransaction` to send ICX or tokens. Transaction output is as follows:

```java
TransactionResult txResult = iconService.getTransactionResult(transaction.getTxHash()).execute();

// Send ICX
if ((transaction.getValue() != null) &&
    (transaction.getValue().compareTo(BigInteger.ZERO) > 0)) {

    System.out.println("[Icx] status:" + txResult.getStatus() +
                       ",from:" + transaction.getFrom() +
                       ",to:" + transaction.getTo() +
                       ",amount:" + transaction.getValue());
}

// Send token
if (transaction.getDataType() != null &&
    transaction.getDataType().equals("call")) {

    RpcObject data = transaction.getData().asObject();
    String methodName = data.getItem("method").asString();

    if (methodName != null && methodName.equals("transfer")) {
        RpcObject params = data.getItem("params").asObject(); // SCORE params
        BigInteger value = params.getItem("_value").asInteger(); // value
        Address toAddr = params.getItem("_to").asAddress(); // to address value

        String tokenName = getTokenName(transaction.getTo());
        String symbol = getTokenSymbol(transaction.getTo());
        String token = String.format("[%s Token(%s)]", tokenName, symbol);
        System.out.println(token+",tokenAddress:" + transaction.getTo() +
                           ",status:" + txResult.getStatus() +
                           ",from:" + transaction.getFrom() +
                           ",to:" + toAddr +
                           ",amount:" + value);
    }
}
```

#### Check the token name & symbol

You can check the token SCORE by calling the `name` and `symbol` functions.

```java
Address tokenAddress; /* returned from `new DeployTokenExample().deploy(wallet)` */

Call<RpcItem> call = new Call.Builder()
    .to(tokenAddress)
    .method("name")
    .build();

RpcItem result = iconService.call(call).execute();
String tokenName = result.asString();

Call<RpcItem> call = new Call.Builder()
    .to(tokenAddress)
    .method("symbol")
    .build();

result = iconService.call(call).execute();
String tokenSymbol = result.asString();

System.out.println("tokenName:"+tokenName);
System.out.println("tokenSymbol:"+tokenSymbol);

// Output
tokenName:StandardToken
tokenSymbol:ST
```

## References

- [Overview](https://github.com/icon-project/icon-sdk-java/blob/master/README.md)
- [API Reference](http://www.javadoc.io/doc/foundation.icon/icon-sdk)
- [ICON JSON-RPC API v3](https://github.com/icon-project/icon-rpc-server/blob/master/docs/icon-json-rpc-v3.md) 


## Licenses

This project follows the Apache 2.0 License. Please refer to [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) for details.
