# ICON SDK Java Quickstart

This is an example project of Icon SDK Java.

In this project, the examples are implemented as below.

| Example       | Description |
| ------------- | ----------- |
| WalletExample | An example of creating and loading a keywallet. |
| IcxTransactionExample | An example of transferring ICX and confirming the result. |
| TokenTransactionExample | An example of transferring IRC token and confirming the result. |
| DeployTokenExample | An example of deploying token. |
| SyncBlockExample | An example of checking block confirmation and printing the ICX and token transfer information. |



### Add Dependency

Please add dependency to use `icon-sdk-java`.

Maven

```xml
<dependencies>
	<dependency>
		<groupId>foundation.icon</groupId>
		<artifactId>icon-sdk</artifactId>
		<version>0.9.4</version>
	</dependency>
</dependencies>
```

Gradle

```gradle
dependencies {
	implementation 'foundation.icon:icon-sdk:0.9.4'
}
```




#### Node URL

ICON node fundamentally complies with the following composition.

```
http://ip_address:9000/api/v3
```

Default port is 9000 and the value can be changed according to the setting.



### IconService

Generate `IconService` to communicate with the nodes.

`IconService` allows you to send transaction, check the result and block information, etc.

`OkHttpClient` is set as default to communicate with http.

```java
String url ="http://127.0.0.1:9000/api/v3"; /* node url */
// OkhttpClient library is used to communicate with http.
OkHttpClient httpClient = new OkHttpClient.Builder()
    			.build();
IconService iconService = new IconService(new HttpProvider(httpClient, url));
```



---



### WalletExample

This example shows how to create a new `KeyWallet` and load wallet with privateKey or Keystore file.

#### Create

Create new EOA by calling `create` function. After creation, the address and private Key can be looked up.

```java
KeyWallet wallet = KeyWallet.create(); //Wallet Creation
System.out.println("address:"+wallet.getAddress()); // Address Check
System.out.println("privateKey:"+wallet.getPrivateKey()); // PrivateKey Check

// Output
address:hx4d37a7013c14bedeedbe131c72e97ab337aea159
privateKey:00e1d6541bfd8be7d88be0d24516556a34ab477788022fa07b4a6c1d862c4de516
```

#### Load

You can call existing EOA by calling `load` function.

After creation, address and private Key can be looked up.

```java
String privateKey; /* privateKey */
KeyWallet wallet = KeyWallet.load(new Bytes(privateKey));  // Load keywallet with privateKey
System.out.println("address:"+wallet.getAddress()); // Address lookup
System.out.println("privateKey:"+wallet.getPrivateKey()); // PrivateKey lookup
```

#### Store

After `KeyWallet` object creation, Keystore file can be stored by calling `store` function.

After calling `store`, Keystore file’s name can be looked up with the returned value.

```java
String password; /* password */
KeyWallet wallet; /* create or load keywallet */
File destinationDirectory = new File(/* directory Path */);
String fileName = KeyWallet.store(wallet, password, destinationDirectory);
System.out.println("fileName:"+fileName);	// keystore file name output

// Output
fileName:UTC--2018-08-30T03-27-41.768000000Z--hx4d37a7013c14bedeedbe131c72e97ab337aea159.json
```



---



### IcxTransactionExample

This example shows how to transfer ICX and check the result.

*For the KeyWallet and IconService creation, please refer to the information above.*

#### ICX Transfer

In this example, you can create KeyWallet with `CommonData.PRIVATE_KEY_STRING` and transfer 1 ICX to `CommonData.ADDRESS_1`.

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
Address toAddress = new Address(CommonData.ADDRESS_1);
// 1 ICX -> 1000000000000000000 conversion
BigInteger value = IconAmount.of("1", IconAmount.Unit.ICX).toLoop();

```

Generate transaction using the values above.

```java
// networkId 1:mainnet, 2:testnet, 3~:private id
BigInteger networkId = new BigInteger("3"); // input node’s networkld
// Recommended icx transfer step limit : 1000000
BigInteger stepLimit = new BigInteger("1000000");

// Timestamp is used to prevent the identical transactions. Only current time is required (Standard unit : us)
// If the timestamp is considerably different from the current time, the transaction will be rejected.
long timestamp = System.currentTimeMillis() * 1000L;

//Enter transaction information
Transaction transaction = TransactionBuilder.of(networkId)
			.from(fromAddress)
			.to(toAddress)
			.value(value)
			.stepLimit(stepLimit)
			.timestamp(new BigInteger(Long.toString(timestamp)))
			.build();
```

Generate SignedTransaction to add signature of the transaction.

```java
// Create signature of the transaction
SignedTransaction signedTransaction =
							new SignedTransaction(transaction, keyStoreLoad);
// Read params to transfer to nodes
System.out.println(signedTransaction.getProperties());
```

After calling sendTransaction from `IconService`, you can send transaction and check the transaction’s hash value. ICX transfer is now sent.

```java
// Send transaction
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

// Print transaction hash
System.out.println("txHash:"+txHash);

// Output
txHash:0x6b17886de346655d96373f2e0de494cb8d7f36ce9086cb15a57d3dcf24523c8f
```

#### Check the Transaction Result

After transaction is sent, the result can be looked up with the returned hash value.

In this example, you can check your transaction result in every 2 seconds because of the block confirmation time.
Checking the result is as follows:

```java
// Check the result with the transaction hash
TransactionResult result = iconService.getTransactionResult(hash).execute();
System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());

// Output
transaction status(1:success, 0:failure):1
```

You can check the following information using the TransactionResult.

- status : 1 (success), 0 (failure)
- to : transaction’s receiving address
- failure : Only exists if status is 0(failure). code(str), message(str) property included
- txHash : transaction hash
- txIndex : transaction index in a block
- blockHeight : Block height of the transaction
- blockHash : Block hash of the transaction
- cumulativeStepUsed : Accumulated amount of consumed step’s until the transaction is executed in block
- stepUsed : Consumed step amount to send the transaction
- stepPrice : Consumed step price to send the transaction
- scoreAddress : SCORE address if the transaction generated SCORE (optional)
- eventLogs :  Occurred EventLog’s list during execution of the transaction.
- logsBloom : Indexed Data’s Bloom Filter value from the occurred Eventlog’s Data

#### Check the Balance

In this example, you can check the ICX balance by looking up the transaction before and after the transaction.

ICX balance can be confirmed by calling getBalance function from `IconService`

```java
KeyWallet wallet; /* create or load */
// Check the wallet balance
BigInteger balance = iconService.getBalance(wallet.getAddress()).execute();
System.out.println("balance:"+balance));

// Output
balance:5000000000000000000
```



---



### TokenTransactionExample

This example shows how to send token and check the balance.

*For KeyWallet and IconService generation, please refer to the information above.*

#### Token Transfer

You can send the token(CommonData.TOKEN_ADDRESS) that is already generated as an example.

You can generate KeyWallet using `CommonData.PRIVATE_KEY_STRING` just like in the case of `IcxTransactionExample`, then send 1 Token to `CommonData.ADDRESS_1`

You need token address to send your token.

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
Address toAddress = new Address(CommonData.ADDRESS_1);
Address tokenAddress = new Address(CommonData.TOKEN_ADDRESS); //Token Address
int tokenDecimals = 18;	// token decimal
// 1 ICX -> 1000000000000000000 conversion
BigInteger value = IconAmount.of("1", tokenDecimals).toLoop();

```

Generate Transaction with the given parameters above. You have to add receiving address and value to ‘RpcObject’ to send token.

```java
// networkId 1:mainnet, 2:testnet, 3~:private id
BigInteger networkId = new BigInteger("3"); // Enter networkId of the node.
// Recommended Step limit to send transaction for token transfer : 1200000
BigInteger stepLimit = new BigInteger("1200000");
// Timestamp is used to prevent the identical transactions. Only current time is required (Default:US)
// If the timestamp is considerably different from the current time, the transaction will be rejected.
long timestamp = System.currentTimeMillis() * 1000L;
// SCORE name that send transaction is “transfer”.
String methodName = "transfer";

// Enter receiving address and the token value.
// You must enter the given key name("_to", "_value"). Otherwise, the transaction will be rejected.

RpcObject params = new RpcObject.Builder()
                .put("_to", new RpcValue(toAddress))
                .put("_value", new RpcValue(value))
                .build();

// Enter transaction information
Transaction transaction = TransactionBuilder.of(networkId)
                .from(wallet.getAddress())
                .to(tokenAddress)
                .stepLimit(stepLimit)
                .timestamp(new BigInteger(Long.toString(timestamp)))
                .call(methodName)
                .params(params)
                .build();
```

Generate SignedTransaction to add signature to your transaction.

```java
// Generate transaction signature.
SignedTransaction signedTransaction =
							new SignedTransaction(transaction, keyStoreLoad);
// Read params to send to nodes.
System.out.println(signedTransaction.getProperties());
```

 Call sendTransaction from ‘IconService’ to check the transaction hash. Token transaction is now sent.

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

In this example, you can check your transaction result in every 2 seconds because of the block confirmation time.
Checking the result is as follows:

```java
// Check the result with the transaction hash
TransactionResult result = iconService.getTransactionResult(hash).execute();
System.out.println("transaction status(1:success, 0:failure):"+result.getStatus());

// Output
transaction status(1:success, 0:failure):1
```

*For the TransactionResult, please refer to the `IcxTransactionExample`.*

#### Check the Token Balance

In this example, you can check the token balance before and after the transaction.

You can check the token balance by calling ‘balanceOf’ from the token SCORE.

```java
Address tokenAddress = new Address(CommonData.TOKEN_ADDRESS); //Token Address
KeyWallet wallet; /* create or load */

String methodName = "balanceOf"; // Method name to check the balance

// How to enter balance address
// You must enter the given key name (“_owner”). Otherwise, your transaction will be rejected.
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



---



### DeployTokenExample

This example shows how to deploy token and check the result.

*For the KeyWallet and IconService generation, please refer to the information above.*

#### Token Deploy

You need the SCORE Project to deploy token.

In this example, you will use ‘test.zi’ from the ‘resources’ folder.

*test.zi : SampleToken SCORE Project Zip file.

 Generate Keywallet using `CommonData.PRIVATE_KEY_STRING`, then read the binary data from ‘test.zi’

```java
Wallet wallet = KeyWallet.load(new Bytes(CommonData.PRIVATE_KEY_STRING));
// Read test.zi from ‘resources’ folder.
byte[] content = /* score binary */
```

Enter the basic information of the token you want to deploy.

```java
BigInteger initialSupply = new BigInteger("100000000000");
BigInteger decimals = new BigInteger("18");
String tokenName = "StandardToken";
String tokenSymbol = "ST";
```

Generate transaction with the given values above.

```java
BigInteger networkId = new BigInteger("3"); //3: networkId of loopchain is using 3.
BigInteger stepLimit = new BigInteger("2013265920"); //Max step limit for sending transaction
long timestamp = System.currentTimeMillis() * 1000L; //timestamp declaration
// Use cx0 to deploy SCORE.
Address scoreInstall = new Address(CommonData.SCORE_INSTALL_ADDRESS);
String contentType = "application/zip";

// Enter token information
// key name ("initialSupply", "decimals", "name", "symbol")
// You must enter the given values. Otherwise, your transaction will be rejected.
RpcObject params = new RpcObject.Builder()
    .put("initialSupply", new RpcValue(initialSupply))
    .put("decimals", new RpcValue(decimals))
    .put("name", new RpcValue(tokenName))
    .put("symbol", new RpcValue(tokenSymbol))
    .build();

Transaction transaction = TransactionBuilder.of(networkId) //Enter transaction information.
    .from(wallet.getAddress())
    .to(scoreInstall)
    .stepLimit(stepLimit)
    .timestamp(new BigInteger(Long.toString(timestamp)))
    .deploy(contentType, content)
    .params(params)
    .build();

```

Generate SignedTransaction to add signature to the transaction.

```java
// Generate signature of the transaction.
SignedTransaction signedTransaction =
							new SignedTransaction(transaction, keyStoreLoad );
// Read params to send to nodes.
System.out.println(signedTransaction.getProperties());
```

You can check the transaction hash value by calling sendTransaction from ‘IconService` Token transfer is now completed.

```java
// Token Transfer
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

// Print Transaction Hash
System.out.println("txHash:"+txHash);

// Output
txHash:0x6b17886de346655d96373f2e0de494cb8d7f36ce9086cb15a57d3dcf24523c8f
```

#### Check the Result

After sending the transaction, you can check the result with the returned hash value.

In this example, you can check your transaction result in every 2 seconds because of the block confirmation time.

If the transaction succeeds, you can check scoreAddress from the result.

You can use SCORE after SCORE audit is successfully accepted.

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



---



### SyncBlockExample

This example shows how to read block information and print the transaction result for every block creation.

*Please refer to above for KeyWallet and IconService creation.*

#### Read Block Information

In this example, 'getLastBlock' is called periodically in order to check the new blocks,

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

You can check the following information using the ConfirmedTransaction:

- version : json rpc server version
- to : Receiving address of transaction
- value: The amount of ICX coins to transfer to the address. If omitted, the value is assumed to be 0
- timestamp: timestamp of the transmitting transaction (unit: microseconds)
- nid : network ID
- signature: digital signature data of the transaction
- txHash : transaction hash
- dataType: A value indicating the type of the data item (call, deploy, message)
- data: Various types of data are included according to dataType.

#### Transaction Output

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

#### Check the Token Name & Symbol

You can check the token SCORE by calling the `name` and` symbol` functions.

```java
Address tokenAddress = new Address(CommonData.TOKEN_ADDRESS); //Token Address

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



----------



### Q&A

 1. What is KeyWallet?
	- A class that manages EOA's private / public key pair.
	- Transaction message signing function.
	- Keystore creation and import / export function.
 2. What is keyStore file?
	 - Text / file that is encrypted with a private key.
 3. Why use HttpLoggingInterceptor?
	 - To log detailed information of http request / response (option)
 4. What is stepLimit for?
	 -  Since transaction fee is required to send transaction, you can set the maximum fee limit. If your actual transaction fee exceeds the stepLimit that you have set, the transaction will fail but still your transaction fee(stepLimit) will be consumed.

 5. What is networId?
	- 1 for mainNet, 2 for testNet, 3 for private network
 6. Why sendTransaction method is named as ‘transfer’, when transferring token?
 - Transfer as a methodName means to transfer token
 - [Refer to IRC2 Specification](https://github.com/icon-project/IIPs/blob/master/IIPS/iip-2.md)

 7. What is httpProvider?
 - Class that supports node and jsonRpc communication.

 8. Is the HttpLoggingInterceptor in SampleCode deleted at all?

HttpLoggingInterceptor is not an SDK library, but one of the 'okhttpclient' related libraries. This is not a required library, but an additional one that is used to record the Log in detail. Please add dependency to use it as below.
```xml
<dependency>
	<groupId>com.squareup.okhttp3</groupId>
	<artifactId>logging-interceptor</artifactId>
	<version>3.11.0</version>
</dependency>
```
