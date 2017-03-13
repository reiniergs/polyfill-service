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
    * Omitting or setting to an empty string is equivalent to the value "default", which is an alias for [a curated list of the most popular polyfills](/v{{apiversion}}/docs/features/#default-sets). Setting the value "all" will select every feature in the library (this is an extremely bad idea and will be removed in a future version) |
* `excludes`
    * List of browser features to exclude from output. Accepts a comma-separated list of feature names. Available feature names are shown on the [Browsers and Features](/v{{apiversion}}/docs/features/) page. Aliases and flags are not accepted. Omitting or setting to an empty string will exclude no polyfills.
    * The main use case for this is where you want a polyfill but you don't want one or more of its dependencies.
    * Note that it doesn't make any sense to list the same feature in both the `features` and the `excludes` list.
* `flags`
    * Comma separated list of flags to apply to **all** features. For available options see the list in the `features` parameter documented above. Equivalent to adding _`|flagname`_ to every feature in the `features` argument.
    * For example, `&flags=always,gated` will apply both the `always` and the `gated` flag to all features requested in the `features` parameter. |
* `ua`
    * User agent string to override the `User-Agent` header on the request. Useful if the polyfill service is being used from the server-side, and in that scenario, this is preferable to setting an inaccurate `User-Agent` header (the User-Agent header should properly be set to a string identifying the client you are using to make the request - for server side requests that might be cURL, for example).
    * Normally, responses from the service will Vary on User-Agent. Setting the `ua` parameter means responses no longer differ between user agents, so the `Vary` header will not list User-Agent as a vary value.
* `callback`
    * Name of JavaScript function to call after polyfills are loaded. Must match the PCRE expression `^[\w\.]+$` otherwise will have no effect. Note that this feature differs from normal JSONp in that nothing is passed to the function - it is simply a way of triggering your own code when the polyfills have loaded, intended to allow the polyfill service to be more easily loaded asynchronously with `async` and `defer` attributes.
* `unknown`
    * What to do when the user agent is not recognised. Set to `polyfill` to return default polyfill variants of all qualifying features, `ignore` to return nothing. Use caution when setting this argument to 'polyfill' on large feature sets, since huge polyfill bundles may cause crashes or lockups in extremely old or underpowered user agents. Defaults to `ignore`.
* `rum`
    * Set to `1` to enable [real user monitoring](https://en.wikipedia.org/wiki/Real_user_monitoring), allowing the polyfill service to gather performance data about itself using the [resource timing API](https://developer.mozilla.org/en-US/docs/Web/API/Resource_Timing_API/Using_the_Resource_Timing_API), where available, and to perform feature detection to improve our browser targeting. Turning this on will help us improve the service, and tune our browser targeting to better serve your particular users. Defaults to 0\. In a future release this will be on by default, so if you want to opt out permanently, set it explicitly to `0` now.

## GET /v2/getRumPerfData

Reports network performance data from real user monitoring. Data includes separate statistics for each phase of the connection.

This endpoint is only available because real user monitoring is enabled. See README for details.

### Query Parameters
* `header`
    * Whether to include a header row in the CSV output (`1` for yes, `0` for no). Default no.
* `metrics`
    * Which connection metrics to report on. Comma-delimited list. Choose from 'perf_dns', 'perf_connect', 'perf_req', 'perf_resp', 'perf_total'
* `dimensions`
    * How to group results. Comma-delimited list. Choose from 'data_center', 'country', 'refer_domain'
* `stats`
    * Aggregation functions to run on grouped metrics. Comma-delimited list. Choose from 'mean', 'median', '95P', '99P', 'std', 'min', 'max', 'count'
* `period`
    * Number of days to draw data from, ending now. Must be in range 1-60.
* `minSample`
    * Minimum number of records to require for a result to be created. Must be at least 500.

## GET /v2/getRumCompatData

Reports compatibility data from Real user monitoring, grouped by browser family and version, covering the last 30 days.

This endpoint is only available if Real user monitoring is enabled.

### Query Parameters
* `header`
    * Whether to include a header row in the CSV output (`1` for yes, `0` for no). Default no.