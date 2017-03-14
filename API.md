# API reference

## Migrating from v1

> Here we're talking about the API version shown in the path (eg. `/v1/polyfill.min.js`), not the project version you find in the package.json file.

* `libVersion` is no longer supported. We no longer support using an older version of the polyfill collection with the current version of the service. To use an older polyfill library, use the version of the project that contains the library in the state you want it (not possible if using cdn.polyfill.io)
* `gated` is no longer supported. Use `flags` instead.
* There are changes to the Node API. See the README for details.

## GET /v2/polyfill<var>:minify</var>.<var>:type</var>

Fetch a polyfill bundle.

### URL Variables
* `minify`
    * Whether to minify the result. If omitted, output will include the full polyfill source, and a header comment with debug information about the user agent and which polyfills have been included in the bundle. If set to `.min`, the output will be minified.
* `type`
    * Set to `js`. Currently we only support JavaScript polyfills, but the `css` type is reserved for future use.

### Query Parameters
* `features`
    * List of browser features to polyfill. Accepts a comma-separated list of feature names. Available feature names are shown on the [Browsers and Features](/v{{apiversion}}/docs/features/) page, though group aliases such as 'es5' and 'es6' are also accepted (these are defined in the polyfills' config.json files - [here's an example](https://github.com/Financial-Times/polyfill-service/blob/master/polyfills/Array/from/config.json)).
    * Each feature name may optionally be appended with zero or more of the following flags:
        * `|always` - Polyfill should be included regardless of whether it is required by the user-agent making the request. If there are multiple browser-specific variants of the polyfill, the default one will be used for browser that doesn't actually require the polyfill. In some cases where the only way of implementing a polyfill is to use browser-specific proprietary technology, the default variant may be empty.
        * `|gated` - If the polyfill is included in the bundle, it will be accompanied by a feature detect, which will only execute the polyfill if the native API is not present.
    * Omitting or setting to an empty string is equivalent to the value "default", which is an alias for [a curated list of the most popular polyfills](/v{{apiversion}}/docs/features/#default-sets). Setting the value "all" will select every feature in the library (this is an extremely bad idea and will be removed in a future version)
* `excludes`
    * List of browser features to exclude from output. Accepts a comma-separated list of feature names. Available feature names are shown on the [Browsers and Features](/v{{apiversion}}/docs/features/) page. Aliases and flags are not accepted. Omitting or setting to an empty string will exclude no polyfills.
    * The main use case for this is where you want a polyfill but you don't want one or more of its dependencies.
    * Note that it doesn't make any sense to list the same feature in both the `features` and the `excludes` list.
* `flags`
    * Comma separated list of flags to apply to **all** features. For available options see the list in the `features` parameter documented above. Equivalent to adding _`|flagname`_ to every feature in the `features` argument.
    * For example, `&flags=always,gated` will apply both the `always` and the `gated` flag to all features requested in the `features` parameter.
* `ua`
    * User agent string to override the `User-Agent` header on the request. Useful if the polyfill service is being used from the server-side, and in that scenario, this is preferable to setting an inaccurate `User-Agent` header (the User-Agent header should properly be set to a string identifying the client you are using to make the request - for server side requests that might be cURL, for example).
    * Normally, responses from the service will Vary on User-Agent. Setting the `ua` parameter means responses no longer differ between user agents, so the `Vary` header will not list User-Agent as a vary value.
* `unknown`
    * What to do when the user agent is not recognised. Set to `polyfill` to return default polyfill variants of all qualifying features, `ignore` to return nothing. Use caution when setting this argument to 'polyfill' on large feature sets, since huge polyfill bundles may cause crashes or lockups in extremely old or underpowered user agents. Defaults to `ignore`.
