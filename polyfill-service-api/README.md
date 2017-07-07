<a name="top"></a>
# Polyfill Service Java API

## Table of Content
- [Usage](#usage)
    - [Setup](#setup)
    - [Configurations](#configurations)
    - [Fetching polyfills](#fetching-polyfills)
    - [Custom Polyfill Location](#custom-polyfill-location)

<a name="usage"></a>
## Usage

<a name="setup"></a>
### Setup
Include dependency in pom.xml
```xml
<dependency>
    <groupId>org.polyfillservice</groupId>
    <artifactId>polyfill-service-api</artifactId>
    <version>${polyfillServiceVersion}</version>
</dependency>
```

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
<configurations>
    <!-- gate all polyfills. default: true -->
    <gated>true</gated>
    <!-- send minified source. default: true -->
    <minify>true</minify>
    <!-- load polyfills when user agent is unknown. default: true -->
    <load-on-unknown-ua>true</load-on-unknown-ua>

    <polyfills>
        <polyfill>es6</polyfill>
        <polyfill>Element.prototype.classList</polyfill>
        <polyfill>Element.prototype.cloneNode</polyfill>
    </polyfills>
</configurations>
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

<a name="custom-polyfill-location"></a>
### Custom Polyfill Location

Polyfill Structure
```
resources
- custom_polyfills
    - MyPolyfill
        - meta.json
        - min.js
        - raw.js
```

meta.json Example
```json
{
  "browsers": {
    "android": "* - 4.4",
    "bb": "* - 10",
    "chrome": "* - 31",
    "firefox": "6 - 28",
    "ie": "8 - 12",
    "ie_mob": "*",
    "ios_saf": "* - 7.1",
    "op_mini": "*",
    "opera": "* - 19",
    "safari": "* - 7",
    "firefox_mob": "6 - 28"
  },
  "dependencies": ["setImmediate", "Array.isArray", "Event"],
  "license": "MIT",
  "detectSource": "'Promise' in this",
  "baseDir": "Promise"
}
```

Polyfill Service Hook
```java
@Bean
public PolyfillLocation location() {
    return PolyfillLocationString("custom_polyfills");
}
```

If custom polyfills already exist in Polyfill Service, it will be replaced by your polyfills. The existing aliases will also apply to your custom polyfills. However, note that we don't support custom alias as of now.