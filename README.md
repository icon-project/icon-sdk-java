[ ![Download](https://api.bintray.com/packages/icon/icon-sdk/icon-sdk/images/download.svg) ](https://bintray.com/icon/icon-sdk/icon-sdk/_latestVersion)

# ICON SDK for Java

ICON supports SDK for 3rd party or user services development. You can integrate ICON SDK for your project and utilize ICON’s functionality.



## Quick start

A simple query of the block by height is as follows.

```java
IconService iconService = new IconService(new HttpProvider("https://url"));

// Gets a block matching the block height.
Request<Block> request = iconService.getBlock(1209);
try {
    Block block = request.execute();
    ...
} catch (Exception e) {
    ...    
}
```



## IconService

APIs are called through `IconService`.

It can be initialized as follows.

```java
// Creates an instance of IconService using the HTTP provider.
IconService iconService = new IconService(new HttpProvider("https://url"));
```

With the customized httpclient is 

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .readTimeout(200, TimeUnit.MILLISECONDS)
    .writeTimeout(600, TimeUnit.MILLISECONDS)
    .build();
     
IconService iconService = new IconService(new HttpProvider(okHttpClient, "https://url"));
```



## Queries

All queries are requested by a `Request` object.

Its requests are executed as **Synchronized** or **Asynchronized**.

```java
Request<Block> request = iconService.getBlock(1209);

// Asynchronized request execution
request.execute(new Callback<Block>(){
    void onFailure(Throwable t) {
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
Request<Block> request = iconService.getBlock(1000); // by height

Request<Block> request = iconService.getBlock(new Bytes("0x000...000"); // by hash

Request<Block> request = iconService.getLatestBlock(); // latest block
     

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


// Calls a SCORE API just for reading
IcxCall<BigInteger> call = new IcxCall.Builder()
    .from(wallet.getAddress())
    .to(scoreAddress)
    .method("balanceOf")
    .params(params)
    .build();
Request<BigInteger> request = iconService.call(call);
```



## Sending transactions

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
KeyStoreWallet.store(wallet, "password", dir); // throw exception if an error exists.
```

**Creating transactions**

```java
// sending icx
Transaction transaction = TransactionBuilder.of(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .nonce(new BigInteger("1000000"))
    .build();

// deploy
Transaction transaction = TransactionBuilder.of(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .stepLimit(new BigInteger("5000000"))
    .nonce(new BigInteger("1000000"))
    .deploy("application/zip", content)
    .params(params)
    .build();

// call
Transaction transaction = TransactionBuilder.of(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(new BigInteger("150000000"))
    .stepLimit(new BigInteger("1000000"))
    .nonce(new BigInteger("1000000"))
    .call("transfer")
    .params(params)
    .build();

// message
Transaction transaction = TransactionBuilder.of(networkId)
    .from(wallet.getAddress())
    .to(scoreAddress)
    .value(BigInteger("150000000"))
    .stepLimit(BigInteger("1000000"))
    .nonce(BigInteger("1000000"))
    .message(message)
    .build();
```

`SignedTransaction` object signs a transaction using the wallet.

And the request is executed as **Synchronized** or **Asynchronized** like a querying request.

```java
SignedTransaction signedTransaction = new SignedTransaction(transaction, wallet);

Request<Bytes> request = iconService.sendTransaction(signedTransaction);

// Asynchronized request execution
request.execute(new Callback<Bytes>(){
    void onFailure(Throwable t) {
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



## Converter

All the requests and responses values are parcelled as `RpcItem`(RpcObject, RpcArray, RcpValue). You can convert your own class using `RpcConverter`.

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
    
IcxCall<Person> call = new IcxCall.Builder()
                .from(fromAddress)
                .to(scoreAddress)
                .method("searchMember")
                .params(person)
                .build();

Person memberPerson = iconService.query(call).execute();
```


## Version

0.9.0 beta

## Download

Download [the latest JAR](https://search.maven.org/remote_content?g=foundation.icon&a=icon-sdk&v=LATEST) or grab via Maven:

```
<dependency>
  <groupId>foundation.icon</groupId>
  <artifactId>icon-sdk</artifactId>
  <version>0.9.0</version>
  <type>pom</type>
</dependency>
```

or Gradle:

```
implementation 'foundation.icon:icon-sdk:0.9.0'
```

## License

This project follows the Apache 2.0 License. Please refer to [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) for details.


