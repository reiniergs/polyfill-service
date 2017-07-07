# Polyfill Service Perf Measurements

## Get Started
Run this command to start the measurements:
```
mvn exec:java -pl polyfill-service-perf
```

## Runner Configurations
Modify src/main/java/org/polyfillservice/perf/RunnerConfig to change the configurations.  
Things you can change:
- number of iterations to run to get the measurements
- number of iterations to run for warming up jvm before getting the perf numbers
- user agent strings to use
- location of custom polyfill service configurations

## Example Output
```
# Configurations
ServiceConfig: {
	polyfills: [WeakMap, Symbol, Function.name, Proxy, Object.assign, Promise, CustomEvent, Event],
	gated: true,
	minify: true,
	load-on-unknown-ua: true
}
  
Number of polyfills loaded:26
Polyfills loaded:[CustomEvent, WeakMap, Symbol, Object.defineProperty, Array.prototype.map, Element, Object.setPrototypeOf, Document, Object.getOwnPropertyDescriptor, Array.prototype.filter, Proxy, Date.now, Object.getOwnPropertyNames, Function.name, Object.assign, Array.isArray, Function.prototype.bind, Window, setImmediate, Array.prototype.forEach, Promise, Object.keys, Object.create, Object.defineProperties, Event, Object.getPrototypeOf]
  
# Measurements
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|User Agent         |Average Query|Raw Source|Min Source|Gzip Min Source|# of Polyfills|Polyfills            |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|chrome/59.0.3071.86|0.011 ms     |0.000 kb  |0.000 kb  |0.020 kb       |0             |                     |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|firefox/54.0       |0.014 ms     |0.000 kb  |0.000 kb  |0.020 kb       |0             |                     |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|safari/9.3.2       |0.026 ms     |8.099 kb  |2.291 kb  |0.969 kb       |1             |Proxy                |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|safari/10.1.1      |0.009 ms     |0.000 kb  |0.000 kb  |0.020 kb       |0             |                     |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|ios_saf/10.3.1     |0.007 ms     |0.000 kb  |0.000 kb  |0.020 kb       |0             |                     |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|ie/11.0            |0.070 ms     |33.128 kb |15.568 kb |5.966 kb       |9             |Object.assign        |
|                   |             |          |          |               |              |Function.name        |
|                   |             |          |          |               |              |WeakMap              |
|                   |             |          |          |               |              |Event                |
|                   |             |          |          |               |              |Symbol               |
|                   |             |          |          |               |              |Object.setPrototypeOf|
|                   |             |          |          |               |              |CustomEvent          |
|                   |             |          |          |               |              |Promise              |
|                   |             |          |          |               |              |Proxy                |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
```
