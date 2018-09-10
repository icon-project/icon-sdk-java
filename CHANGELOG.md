# Changelog

### Version 0.9.5 - Septamber 10, 2018 ([Maven](https://search.maven.org/search?q=g:foundation.icon%20a:icon-sdk))

#### API changes
  - Deprecates `TransactionBuilder.of` and adds `TransactionBuilder.newBuilder` to create an instance of TransactionBuilder
  - `EventLog` class is moved into `TransactionResult` as an inner class
  - Adds failure field to TransactionResult. 

#### Improvements
  - Migrates ICON-RPC-V2 block data
  - Adds Quickstart sample
  - Validates a transaction to send when using the TransactionBuilder
  - Adds a default value for Timestamp and NetworkId when missing

