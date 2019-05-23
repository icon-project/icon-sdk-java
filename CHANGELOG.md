# Changelog

### Version 0.9.12 - May 23, 2019

#### API Changes
  - Add new constructors to `HttpProvider` to accept a server-based authority URI and a JSON-RPC version. The older constructors have been deprecated.
  - Add a new `estimateStep` method to `IconService` to get an estimated Step of how much Step is necessary to allow the transaction to complete.
  - Add a new constructor to `SignedTransaction` to override the `stepLimit` in the given raw `Transaction`.  The `stepLimit` could be obtained from the `estimateStep` method.

#### Improvements
  - All the sample codes (including quickstart) have been improved and work against the T-Bears Docker instance.

### Version 0.9.11 - January 8, 2019

#### Bugfixes
  - Fix the generation of the wrong address caused by the wrong conversion of `BigInteger` to byte array

### Version 0.9.10 - December 21, 2018

#### Bugfixes
  - Override `hashCode` to keep identities corresponding to equality rule in data classes of `Address` and `Bytes`
  - Throw an exception rather than print stack trace when conversion failure in `AnnotatedConverterFactory`
  - Fix some bugs in sample and quickstart example

### Version 0.9.9 - November 27, 2018

#### Bugfixes
  - Fix signature mismatch error in case of the non UTF-8 environment

### Version 0.9.8 - October 31, 2018

#### API Changes
  - Removes the crypto package of web3j and add the corresponding abilities into `foundation.icon.icx.crypto`

#### Improvements
  - Supports Android 3.0 or newer

### Version 0.9.7 - September 19, 2018

#### API Changes
  - Adds a method `getIndexed` in ScoreApi.Param

#### Improvements
  - Sets the step limit as max step limit of the governance in the quickstart sample.
  - Adds a method that queries SCORE API in the quickstart sample.

### Version 0.9.6 - September 12, 2018

#### Changes
  - Change logic to check token transfer in QuickStart sample
  - Fix converting publicKey of `Address` class to bytearray

#### Improvements
  - Add logic to get default step cost to QuickStart sample

### Version 0.9.5 - September 10, 2018

#### API changes
  - Deprecates `TransactionBuilder.of` and adds `TransactionBuilder.newBuilder` to create an instance of TransactionBuilder
  - `EventLog` class is moved into `TransactionResult` as an inner class
  - Adds failure field to TransactionResult. 

#### Improvements
  - Migrates ICON-RPC-V2 block data
  - Adds Quickstart sample
  - Validates a transaction to send when using the TransactionBuilder
  - Adds a default value for Timestamp and NetworkId when missing
