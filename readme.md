#  Polyfill Service
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()  [![Coverage Status](https://coveralls.io/repos/github/reiniergs/polyfill-service/badge.svg?branch=codeCoverage)](https://coveralls.io/github/reiniergs/polyfill-service?branch=codeCoverage)

 
## Running Locally
Clone the project onto your local machine:
```
$ git clone https://github.com/reiniergs/polyfill-service.git
```
Change to your cloned project directory
```
$ cd polyfill-service
```
Build the project
```
$ mvn clean install
```
 Start the app server
```
$ mvn jetty:run
```
After the server starts up, navigate to `http://localhost:8080` to test that it is up

## Run Tests
```
$ mvn test
```
## Documentation
Start the app server and navigate to `http://localhost:8080/docs/index.html`