# GnosisWatch

A comprehensive Scala service for monitoring and interacting with the Gnosis Chain ecosystem.
This project provides a clean API interface for accessing Gnosis Chain data through various services including:

- Beaconcha
- Blockscout
- GnosisScan
- Discord
- Binance
- Aura
- Chainlink

## Features

- REST API endpoints for blockchain data (balances, contracts, validators)
- Real-time price monitoring via Binance
- Discord bot integration for notifications
- Swagger API documentation
- Integration with our custom Ethereum client (Rust-based)
- Support for Aura and Chainlink services

## Technologies

- Scala 3
- ZIO ecosystem (ZIO, ZIO HTTP, ZIO Config)
- Tapir for API documentation
- Circe for JSON processing
- STTP client for HTTP requests
- WebSocket support for real-time data
