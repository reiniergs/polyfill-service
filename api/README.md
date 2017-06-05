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
```

<a name="configurations"></a>
### Configurations
Polyfill Service provides some options to customize the behavior of fetching polyfills.
- minified
    - whether to minify polyfills
- gated
    - whether to gate polyfill with if (polyfill exists)
    - can be global or specific to a polyfill
- always
    - whether to load polyfill regardless of user agent
    - can be global or specific to a polyfill
- loadOnUnknownUA
    - whether to load polyfills when user agent is unknown
- polyfills
    - polyfills to fetch
    - can be alias group like es6 that contains multiple polyfills
- excludes
    - polyfills to exclude
    - use this to exclude certain polyfills when fetching alias group

A simple way to set up these options is to specify them inside a config file and load it using `QueryLoaderService`. For now `QueryLoaderService` only supports `xml` format.

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

    <excludes>
        <!-- exclude some of the es6 stuff -->
        <polyfill>Math.cbrt</polyfill>
        <polyfill>Math.tanh</polyfill>
    </excludes>
</query>
```

Once we have this configuration file, we can
```java
@Autowired
QueryLoaderService queryLoaderService;
...
// config can be a file path string or an inputStream
Query query = queryLoaderService.loadQuery(config);
```

<a name="fetching-polyfills"></a>
### Fetching polyfills
```java
@Autowired
PolyfillService polyfillService;
...
// fetch with a user agent string and an optional query config object and 
String output = polyfillService.getPolyfillsSource(userAgentString, query);
```