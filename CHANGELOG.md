# Changelog

## 1.0.0 - Sep 11, 2020
- There are no special features or incompatibilities related to the version number change. This is just a numbering change to drop the beta tag.
- Remove unused field and method of `jsonrpc.Response`
- Add `Address.getBody()` method

## 0.9.15 - Dec 9, 2019
- Fix the wrong handling of null and empty bytes when `RpcObject` is serialized/deserialized
- Define `RpcValue.NULL` for null value
- Add a new method `getDefault` in `ScoreApi.Param`

## 0.9.14 - Jun 21, 2019
- Fix the bug that generates an invalid signature when a `stepLimit` is given

## 0.9.13 - Jun 14, 2019
- Add a new `DepositBuilder` to support add/withdraw deposit transactions
- Add a new `getStepUsedDetails` method to `TransactionResult`
- Resurrect the older `HttpProvider` constructors to keep the source-code compatibility

## 0.9.12 - May 23, 2019
- Add new constructors to `HttpProvider` to accept a server-based authority URI and a JSON-RPC version. The older constructors have been deprecated.
- Add a new `estimateStep` method to `IconService` to get an estimated Step of how much Step is necessary to allow the transaction to complete.
- Add a new constructor to `SignedTransaction` to override the `stepLimit` in the given raw `Transaction`.  The `stepLimit` could be obtained from the `estimateStep` method.
- All the sample codes (including quickstart) have been improved and work against the T-Bears Docker instance.

## 0.9.11 - January 8, 2019
- Fix the generation of the wrong address caused by the wrong conversion of `BigInteger` to byte array

## 0.9.10 - December 21, 2018
- Override `hashCode` to keep identities corresponding to equality rule in data classes of `Address` and `Bytes`
- Throw an exception rather than print stack trace when conversion failure in `AnnotatedConverterFactory`
- Fix some bugs in samples and quickstart example

## 0.9.9 - November 27, 2018
- Fix signature mismatch error in case of the non UTF-8 environment

## 0.9.8 - October 31, 2018
- Removes the crypto package of web3j and add the corresponding abilities into `foundation.icon.icx.crypto`
- Supports Android 3.0 or newer

## 0.9.7 - September 19, 2018
- Adds a method `getIndexed` in ScoreApi.Param
- Sets the step limit as max step limit of the governance in the quickstart sample.
- Adds a method that queries SCORE API in the quickstart sample.

## 0.9.6 - September 12, 2018
- Change logic to check token transfer in quickstart sample
- Fix converting publicKey of `Address` class to bytearray
- Add logic to get default step cost to quickstart sample

## 0.9.5 - September 10, 2018
- Deprecates `TransactionBuilder.of` and adds `TransactionBuilder.newBuilder` to create an instance of TransactionBuilder
- `EventLog` class is moved into `TransactionResult` as an inner class
- Adds failure field to TransactionResult.
- Migrates ICON-RPC-V2 block data
- Adds quickstart sample
- Validates a transaction to send when using the TransactionBuilder
- Adds a default value for Timestamp and NetworkId when missing
