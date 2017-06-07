<a name="top"></a>
# Polyfill Service Java API

## Table of Content
- [Usage](#usage)
    - [Setup](#setup)
    - [Configurations](#configurations)
    - [Fetching polyfills](#fetching-polyfills)

<a name="usage"></a>
## Usage

<a name="setup"></a>
### Setup
First, import Spring configuration to let Spring bootstraps the API. This can be done inside any spring bean, preferably in a @Configuration class.
```java
@Import(PolyfillApiConfig.class)

// bean to specify path to custom service configurations
// can specify which polyfills to serve, etc.
// see below for how to write this file
@Bean
public PolyfillServiceConfigLocation serviceConfigLocation() {
    return new PolyfillServiceConfigLocation(new File("./src/main/resources/settings/service-config.xml"));
}
```

<a name="configurations"></a>
### Configurations
Polyfill Service provides some options to customize the behavior of fetching polyfills.
- minified
    - whether to minify polyfills
- gated
    - whether to gate polyfill with if (polyfill exists)
    - can be global or specific to a polyfill
- loadOnUnknownUA
    - whether to load polyfills when user agent is unknown
- polyfills
    - polyfills to fetch
    - can be alias group like es6 that contains multiple polyfills

e.g.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<query>
    <!-- gate all polyfills -->
    <gated>true</gated>
    <!-- send minified source -->
    <minified>true</minified>
    <!-- load polyfills when user agent is unknown -->
    <loadOnUnknownUA>true</loadOnUnknownUA>

    <polyfills>
        <polyfill>es6</polyfill>
        <polyfill>Element.prototype.classList</polyfill>
        <polyfill>Element.prototype.cloneNode</polyfill>
    </polyfills>
</query>
```

<a name="fetching-polyfills"></a>
### Fetching polyfills
```java
@Autowired
PolyfillService polyfillService;
...
// fetch with a user agent string and an optional query config object
// without query object, it will load all the polyfills, filtered by the user agent string
String output = polyfillService.getPolyfillsSource(userAgentString);
```
