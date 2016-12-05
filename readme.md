## How build the project

```
mvn clean install
```

## How start the app

```
mvn jetty:start
```

http://localhost:8080/hello?thing=Jose

## How run test of the app

```
mvn test
```

## Polyfill endpoint.

http://localhost:8080/polyfill.js

Parameters:

- minify : If present, the polyfied features in the request will be minified. Example: http://localhost:8080/polyfill.js?minify


[![Coverage Status](https://coveralls.io/repos/github/reiniergs/polyfill-service/badge.svg)](https://coveralls.io/github/reiniergs/polyfill-service)
