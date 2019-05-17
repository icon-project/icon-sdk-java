[ ![Download](https://api.bintray.com/packages/icon/icon-sdk/icon-sdk/images/download.svg) ](https://bintray.com/icon/icon-sdk/icon-sdk/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/foundation.icon/icon-sdk/badge.svg)](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk)
[![Javadocs](http://www.javadoc.io/badge/foundation.icon/icon-sdk.svg)](http://www.javadoc.io/doc/foundation.icon/icon-sdk)

# ICON SDK for Java

ICON supports SDK for 3rd party or user services development. You can integrate ICON SDK for your project and utilize ICON’s functionality.



## Quick Start

A simple query of the block by height is as follows.

```java
IconService iconService = new IconService(new HttpProvider("http://localhost:9000", 3));

// Gets a block matching the block height.
Request<Block> request = iconService.getBlock(height);
try {
    Block block = request.execute();
    ...
} catch (IOException e) {
    ...    
}
```



## IconService

APIs are called through `IconService`.

`IconService` can be initialized as follows.

```java
// Creates an instance of IconService using the HTTP provider.
IconService iconService = new IconService(new HttpProvider("http://localhost:9000", 3));
```

The code below shows initializing `IconService` with a custom HTTP client.

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .readTimeout(200, TimeUnit.MILLISECONDS)
    .writeTimeout(600, TimeUnit.MILLISECONDS)
    .build();

IconService iconService = new IconService(new HttpProvider(okHttpClient, "http://localhost:9000", 3));
```



## Queries

All queries are requested by a `Request` object.

Query requests can be executed as **Synchronized** or **Asynchronized**.

Once the request has been executed, the same request object cannot be executed again.

```java
Request<Block> request = iconService.getBlock(height);

// Asynchronized request execution
request.execute(new Callback<Block>(){
    void onFailure(Exception exception) {
        ...
    }

    void onResponse(Block block) {
        ...
    }
});

// Synchronized request execution
try {
    Block block = request.execute();
    ...
} catch (Exception e) {
    ...
}
```

The querying APIs are as follows.

```java
// Gets the block
Request<Block> request = iconService.getBlock(new BigInteger("1000")); // by height

Request<Block> request = iconService.getBlock(new Bytes("0x000...000"); // by hash

Request<Block> request = iconService.getLastBlock(); // the last block
     

// Gets the balance of an given account
Request<BigInteger> request = iconService.getBalance(new Address("hx000...1");


// Gets a list of the SCORE API
Request<List<ScoreApi>> request = iconService.getScoreApi(new Address("cx000...1"));


// Gets the total supply of icx
Request<BigInteger> request = iconService.getTotalSupply();


// Gets a transaction matching the given transaction hash
Request<Transaction> request = iconService.getTransaction(new Bytes("0x000...000"));


// Gets the result of the transaction matching the given transaction hash
Request<TransactionResult> request = iconService.getTransactionResult(new Bytes("0x000...000"));


// Calls a SCORE read-only API
Call<BigInteger> call = new Call.Builder()
    .from(wallet.getAddress())
    .to(scoreAddress)
    .method("balanceOf")
    .params(params)
    .buildWith(BigInteger.class);
Request<BigInteger> request = iconService.call(call);

// Calls without response type                                                     
Call<RpcItem> call = new Call.Builder()
    .from(wallet.getAddress())
    .to(scoreAddress)
    .method("balanceOf")
    .params(params)
    .build();
Request<RpcItem> request = iconService.call(call);
try {
    RpcItem rpcItem = request.execute();
    BigInteger balance = rpcItem.asInteger();
    ...
} catch (Exception e) {
    ...
}                                                     
```


## Transactions

Calling SCORE APIs to change states is requested as sending a transaction.

Before sending a transaction, the transaction should be signed. It can be done using a `Wallet` object.

**Loading wallets and storing the Keystore**

```java
// Generates a wallet.
Wallet wallet = KeyWallet.create();

// Loads a wallet from the private key.
Wallet wallet = KeyWallet.load(new Bytes("0x0000"));

// Loads a wallet from the key store file.
File file = new File("./key.keystore");
Wallet wallet = KeyWallet.load("password", file);

// Stores the keystore on the file path.
File dir = new File("./");
KeyWallet.store(wallet, "password", dir); // throw exception if an error exists.
```

**Creating transactions**

```java
// sending icx
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .nonce(new BigInteger("1000000"))
    .build();

// deploy
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .stepLimit(new BigInteger("5000000"))
    .nonce(new BigInteger("1000000"))
    .deploy("application/zip", content)
    .params(params)
    .build();

// call
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .nonce(new BigInteger("1000000"))
    .call("transfer")
    .params(params)
    .build();

// message
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .nonce(new BigInteger("1000000"))
    .message(message)
    .build();
```

`SignedTransaction` object signs a transaction using the wallet.

And the request is executed as **Synchronized** or **Asynchronized** like a querying request.

Once the request has been executed, the same request object cannot be executed again.

```java
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

Request<Bytes> request = iconService.sendTransaction(signedTransaction);

// Asynchronized request execution
request.execute(new Callback<Bytes>(){
    void onFailure(Exception e) {
        ...
    }
     
    void onResponse(Bytes txHash) {
        ...
    }
});

// Synchronized request execution
try {
    Bytes txHash = request.execute();
    ...
} catch (Exception e) {
    ...
}
```


## Step Estimation

It is important to set a proper `stepLimit` value in your transaction to make the submitted transaction executed successfully.

You can get an estimated step before sending your transaction and use it later for making a `SignedTransaction`.

```java
// make a raw transaction without the stepLimit
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(fromAddress)
    .to(toAddress)
    .nonce(BigInteger.valueOf(1))
    .call("transfer")
    .params(params)
    .build();

// get an estimated step value
BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

// set some margin value
BigInteger margin = BigInteger.valueOf(10000);

// make a signed transaction with the same raw transaction and the estimated step
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep.add(margin));
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
...
```

Note that the estimation can be smaller or larger than the actual amount of step to be used by the transaction for several reasons,
so it is recommended to add some margin value to the estimation when you set `stepLimit` parameter of `SignedTransaction`.


## Converter

All the requests and responses values are parcelled as `RpcItem` (`RpcObject`, `RpcArray`, `RcpValue`). You can convert your own class using `RpcConverter`.

```java
iconService.addConverterFactory(new RpcConverter.RpcConverterFactory() {
    @Override
    public  RpcConverter create(Class type) {
        if (type.isAssignableFrom(Person.class)) {
            return new RpcConverter<Person>() {
                @Override
                public Person convertTo(RpcItem object) {
                    // Unpacking from RpcItem to the user defined class
                    String name = object.asObject().getItem("name").asString();
                    BigInteger age = object.asObject().getItem("age").asInteger();
                    return new Person(name, age);
                }

                @Override
                public RpcItem convertFrom(Person person) {
                    // Packing from the user defined class to RpcItem
                    return new RpcObject.Builder()
                        .put("name", person.name)
                        .put("age", person.age)
                        .build();
                }
            };
        }
        return null;
    }
});

...

class Person {
    public Person(String name, BigInteger age) {}
}

...
    
Call<Person> call = new Call.Builder()
    .from(fromAddress)
    .to(scoreAddress)
    .method("searchMember")
    .params(person) // the input parameter is an instance of Person type
    .buildWith(Person.class); // build with the response type 'Person'

Person memberPerson = iconService.call(call).execute();
```


## References

- [ICON JSON-RPC API v3](https://github.com/icon-project/icon-rpc-server/blob/master/docs/icon-json-rpc-v3.md)
- [ICON Network](https://github.com/icon-project/icon-project.github.io/blob/master/docs/icon_network.md)


## Version

0.9.11 (beta)

## Android Support

- Android 3.0+ (API 11+)

## Download

Download [the latest JAR](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk) or grab via Maven:

```xml
<dependency>
  <groupId>foundation.icon</groupId>
  <artifactId>icon-sdk</artifactId>
  <version>[x.y.z]</version>
</dependency>
```

or Gradle:

```groovy
implementation 'foundation.icon:icon-sdk:[x.y.z]'
```


## License

This project follows the Apache 2.0 License. Please refer to [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) for details.


