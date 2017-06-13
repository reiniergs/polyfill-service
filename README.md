<a name="top"></a>
#  Polyfill Service
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://img.shields.io/github/license/mashape/apistatus.svg)
[![Coverage Status](https://coveralls.io/repos/github/reiniergs/polyfill-service/badge.svg?branch=codeCoverage)](https://coveralls.io/github/reiniergs/polyfill-service?branch=codeCoverage)



## Table of content
- [Why? The problem](#why)
- [A better way](#solution)
- [Java API](/api/README.md)
- [RESTful API](/rest/README.md)
- [Running locally](#running-locally)
- [Run tests](#tests)
- [Java docs](#java-docs)
- [Performance](/perf/README.md)


<a name="why"></a>
### Why? The problem

New JavaScript features are being introduced all the time. In many cases, these new features are so helpful we choose to use 
them on production before they’re fully implemented in all browsers. To not break the experience for users on older browsers, 
we of course include polyfills for any not-fully-supported features. Then we might end up unnecessarily penalizing users on 
modern browsers by forcing them to download a lot of code they don’t need.

This is far from ideal. We need a better way to only load polyfills when they are needed.

We also want to write Javascript with ES6 syntax, but supporting older browsers
that don’t understand ES6 is an issue. We need to deal with that. The standard process for that is:

1. Write ES6 
2. Compile everything down to ES5 using (e.g. Babel)
3. Send that to the browser.

Is that the most efficient way? The problem is that we are forcing modern browsers to run old code when they don’t have to. They understand ES6. Why not just give them ES6?

Note: One thing to be clear, Polyfill Service does not provide support for syntactic sugar. For example, Classes, enhanced Object Literals, and things like Arrow Functions. You'd still need a compiler for those.

**[⬆ back to top](#top)**



<a name="solution"></a>
### A better way

Essentially, what we want from a polyfill delivery system is a way to send a single copy of each of the best polyfills, in the right order (respecting dependencies), to just the browsers that lack a native implementation. The polyfill service does this by parsing the User-Agent from HTTP header and then using it to find the list of polyfills that are suitable for that browser and its specific version.

**Note:** Typically once a browser version is released, its feature set does not change until its next release.

**[⬆ back to top](#top)**



<a name="running locally"></a>
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
