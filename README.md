[![Download](https://api.bintray.com/packages/icon/icon-sdk/icon-sdk/images/download.svg)](https://bintray.com/icon/icon-sdk/icon-sdk/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/foundation.icon/icon-sdk/badge.svg)](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk)
[![Javadocs](http://www.javadoc.io/badge/foundation.icon/icon-sdk.svg)](http://www.javadoc.io/doc/foundation.icon/icon-sdk)

# ICON SDK for Java

ICON supports SDK for 3rd party or user services development. You can integrate ICON SDK for your project and utilize ICON’s functionality.

This document is focused on how to use the SDK properly. For the detailed API specification, see the API reference documentation.

## Version

0.9.15 (beta)

## Prerequisite

This Java SDK works on the following platforms:

- Java 8+ (for Java 7, you can explore source code [here](https://github.com/icon-project/icon-sdk-java/blob/sdk-for-java7/README.md))
- Android 3.0+ (API 11+)

## Installation

Download [the latest JAR](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk) or grab via Maven:

```xml
<dependency>
    <groupId>foundation.icon</groupId>
    <artifactId>icon-sdk</artifactId>
    <version>0.9.15</version>
</dependency>
```

or Gradle:

```groovy
dependencies {
    implementation 'foundation.icon:icon-sdk:0.9.15'
}
```

## Quick Start

We provide different types of code examples to help you to start quickly from scratch.
Please refer to the separate [Quick Start] project for the code examples.


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

// Synchronized request execution
try {
    Block block = request.execute();
    ...
} catch (Exception e) {
    ...
}

// Asynchronized request execution
request.execute(new Callback<Block>(){
    @Override
    public void onSuccess(Block block) {
        ...
    }

    @Override
    public void onFailure(Exception exception) {
        ...
    }
});
```

The querying APIs are as follows.

```java
// Gets the block
Request<Block> request = iconService.getBlock(new BigInteger("1000")); // by height
Request<Block> request = iconService.getBlock(new Bytes("0x5e23...af83"); // by hash
Request<Block> request = iconService.getLastBlock(); // the last block

// Gets the balance of an given account
Request<BigInteger> request = iconService.getBalance(new Address("hxe7af...dfcb");

// Gets a list of the SCORE API
Request<List<ScoreApi>> request = iconService.getScoreApi(new Address("cx0000...0001"));

// Gets the total supply of icx
Request<BigInteger> request = iconService.getTotalSupply();

// Gets a transaction matching the given transaction hash
Request<Transaction> request = iconService.getTransaction(new Bytes("0x5e23...af83"));

// Gets the result of the transaction matching the given transaction hash
Request<TransactionResult> request = iconService.getTransactionResult(new Bytes("0x5e23...af83"));

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
Wallet wallet = KeyWallet.load(new Bytes("592eb2...27ff0c"));

// Loads a wallet from the key store file.
File file = new File("./keystore_file");
Wallet wallet = KeyWallet.load("password", file);

// Stores the keystore on the file path.
File dir = new File("./");
KeyWallet.store(wallet, "password", dir); // throw exception if an error exists.
```

**Creating transactions**

```java
// send ICX
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .build();

// deploy a SCORE
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .stepLimit(new BigInteger("2000000000"))
    .deploy("application/zip", content)
    .params(params)
    .build();

// call a method in SCORE
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .stepLimit(new BigInteger("1000000"))
    .call("transfer")
    .params(params)
    .build();

// send a message
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .stepLimit(new BigInteger("1000000"))
    .message(message)
    .build();

// deposit ICX to SCORE
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(depositAmount)
    .stepLimit(new BigInteger("1000000"))
    .deposit()
    .add()
    .build();
```

`SignedTransaction` object signs a transaction using the wallet.
And the request can be executed as **Synchronized** or **Asynchronized** like a query request.
Once the request has been executed, the same request object cannot be executed again.

```java
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

Request<Bytes> request = iconService.sendTransaction(signedTransaction);

// Synchronized request execution
try {
    Bytes txHash = request.execute();
    ...
} catch (Exception e) {
    ...
}

// Asynchronized request execution
request.execute(new Callback<Bytes>() {
    @Override
    public void onSuccess(Bytes result) {
        ...
    }

    @Override
    public void onFailure(Exception exception) {
        ...
    }
});
```


## Step Estimation

It is important to set a proper `stepLimit` value in your transaction to make the submitted transaction executed successfully.

`estimateStep` API provides a way to **estimate** the Step usage of a given transaction. Using the method, you can get an estimated Step usage before sending your transaction then make a `SignedTransaction` with the `stepLimit` based on the estimation.

```java
// make a raw transaction without the stepLimit
Transaction transaction = TransactionBuilder.newBuilder()
    .nid(networkId)
    .from(fromAddress)
    .to(toAddress)
    .call("transfer")
    .params(params)
    .build();

// get an estimated step value
BigInteger estimatedStep = iconService.estimateStep(transaction).execute();

// set some margin
BigInteger margin = BigInteger.valueOf(10000);

// make a signed transaction with the same raw transaction and the estimated step
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet, estimatedStep.add(margin));
Bytes txHash = iconService.sendTransaction(signedTransaction).execute();
...
```

Note that the estimation can be smaller or larger than the actual amount of step to be used by the transaction,
so it is recommended to add some margin to the estimation when you set the `stepLimit` of the `SignedTransaction`.

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

- [Quick Start]
- [ICON JSON-RPC API v3]
- [ICON Network]

[Quick Start]: https://github.com/icon-project/icon-sdk-java/tree/master/quickstart
[ICON JSON-RPC API v3]: https://github.com/icon-project/icon-rpc-server/blob/master/docs/icon-json-rpc-v3.md
[ICON Network]: https://github.com/icon-project/icon-project.github.io/blob/master/docs/icon_network.md


## Licenses

This project follows the Apache 2.0 License. Please refer to [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) for details.
