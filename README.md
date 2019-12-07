# Keycloak ApiKey Auth

### This module extends [https://github.com/zak905/keycloak-api-key-demo](https://github.com/zak905/keycloak-api-key-demo)

The correlated article is [http://www.zakariaamine.com/2019-06-14/extending-keycloak
(http://www.zakariaamine.com/2019-06-14/extending-keycloak)

It enables ApiKey validation in Keycloak, when validation fails it emits a `LOGIN_ERROR` event and `LOGIN` otherwise.

This is cool when using with [Keycloak Metrics SPI](https://github.com/aerogear/keycloak-metrics-spi)

## Improvments

- Discovery realm by session context
- Dipatch Events

## Running example

Running: `mvn package && docker-compose up --build`