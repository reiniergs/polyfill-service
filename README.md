<a name="top"></a>
#  Polyfill Service (v1.1.2)
Polyfill Service sends down only the polyfills that your browsers need, because browsers deserve the bare minimum.  
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://img.shields.io/github/license/mashape/apistatus.svg)
[![Coverage Status](https://coveralls.io/repos/github/reiniergs/polyfill-service/badge.svg?branch=codeCoverage)](https://coveralls.io/github/reiniergs/polyfill-service?branch=codeCoverage)
[![Build Status](https://travis-ci.org/reiniergs/polyfill-service.svg?branch=master)](https://travis-ci.org/reiniergs/polyfill-service)
## Getting Started
To get started, [clone and build](#running-locally) the service. Polyfill Service is available in two flavors, as a [Java API](/polyfill-service-api/README.md) or [RESTful API](/polyfill-service-rest/README.md). With the Java API, you can customize how you want to fetch polyfills and even narrow down which polyfills to fetch. The REST API provides endpoints for you to work with different URL variables and query parameters. Both APIs fulfill the following conditions:
* Does not require browsers to download unneeded polyfills
* Easy to maintain
* Lightweight and small footprint

We recommend exploring both services to see what works best for your project. Although both services are considered lightweight and performant, the Java API is ideal if you already know which polyfills you want to include or eliminate, and you don't want to make an extra request. 


## Using Polyfill Service
- [What are we solving?](#why)
- [A better way with Polyfill Service](#solution)
- [Java API](/polyfill-service-api/README.md)
- [RESTful API](/polyfill-service-rest/README.md)
- [Running locally](#running-locally)
- [Run tests](#tests)
- [Java docs](#java-docs)
- [Performance](/polyfill-service-perf/README.md)
- [Contributing to Polyfill Service](#contribute)


<a name="why"></a>
### What are we solving?

New JavaScript features are being introduced all the time. In many cases, these new features are so helpful we choose to use 
them on production before they’re fully implemented in all browsers. To avoid breaking the experience for users on older browsers, 
web projects typically include polyfills for any not-fully-supported features. Therefore, we end up unnecessarily penalizing users on modern browsers by forcing them to download a lot of code they don’t need.

This picture is far from ideal. We need a better way to only load polyfills when they are needed. And this is where Polyfill Service shines.

We also want to write Javascript with ES6 syntax, but supporting older browsers
that don’t understand ES6 is an issue. We need to deal with that. The standard process for that is:

1. Write ES6 
2. Compile everything down to ES5 using (e.g. Babel)
3. Send that to the browser.

Is that the most efficient way? The problem is that we are forcing modern browsers to run old code when they don’t have to. They understand ES6. Why not just give them ES6?

Note: One thing to be clear, Polyfill Service does not provide support for syntactic sugar. For example, Classes, enhanced Object Literals, and things like Arrow Functions. You'd still need a compiler for those.

**[⬆ back to top](#top)**



<a name="solution"></a>
### A better way with Polyfill Service

Essentially, what we want from a polyfill delivery system is a way to send a single copy of each of the best polyfills, in the right order (respecting dependencies), to just the browsers that lack a native implementation. The polyfill service does this by parsing the User-Agent from HTTP header and then using it to find the list of polyfills that are suitable for that browser and its specific version.

**Note:** Typically once a browser version is released, its feature set does not change until its next release.

**[⬆ back to top](#top)**



<a name="running-locally"></a>
### Running Locally

Clone and build the project
```bash
$ git clone https://github.com/reiniergs/polyfill-service.git
$ cd polyfill-service
$ mvn clean install
```

Start the app server
```
$ mvn jetty:run -pl web
```

After the server starts up, navigate to `http://localhost:8080` to test that it is up

**[⬆ back to top](#top)**



<a name="tests"></a>
### Run Tests
```
$ mvn test # run unit tests
$ mvn verify -Pintegration-test # run integration tests
```

**[⬆ back to top](#top)**



<a name="java-docs"></a>
### Java docs
Start the app server and navigate to `http://localhost:8080/docs/index.html`

**[⬆ back to top](#top)**


<a name="contribute"></a>
## Contributing to Polyfill Service
This service uses the [polyfill.io](http://polyfill.io) `npm` package for a set of polyfills and their metadata. We welcome original polyfills or third party polyfills as well.

Polyfills are located in the `/polyfill-service-api/polyfills` directory and each folder represents a feature. The subdirectories follow the convention for polyfill.io. For example, `/polyfill-service-api/polyfills/Array/prototype/forEach` should be used to hold the polyfill for `Array.prototype.forEach`.

A feature that has no JavaScript API is given a name prefixed with a tilde (~), for example, `~html5-elements`.
See [Authoring Polyfills](https://polyfill.io/v2/docs/contributing/authoring-polyfills) for more information on:

* Polyfill directory structure
* Configuration using the config.json file
* Browser support
* Original vs third party polyfills

**[⬆ back to top](#top)**

