# Changelog

### Version 0.9.6 - September 12, 2018 ([Maven](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk))

#### Changes
  - Change logic to check token transfer in QuickStart sample
  - Fix converting publicKey of `Address` class to bytearray

#### Improvements
  - Add logic to get default step cost to QuickStart sample


### Version 0.9.5 - September 10, 2018 ([Maven](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk))

#### API changes
  - Deprecates `TransactionBuilder.of` and adds `TransactionBuilder.newBuilder` to create an instance of TransactionBuilder
  - `EventLog` class is moved into `TransactionResult` as an inner class
  - Adds failure field to TransactionResult. 

#### Improvements
  - Migrates ICON-RPC-V2 block data
  - Adds Quickstart sample
  - Validates a transaction to send when using the TransactionBuilder
  - Adds a default value for Timestamp and NetworkId when missing

