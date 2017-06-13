# Polyfill Service Perf Measurements

## Get Started
Run this command to start the measurements:
```
mvn exec:java -pl perf
```

## Runner Configurations
Modify src/main/java/org/polyfill/perf/RunnerConfig to change the configurations.  
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
|ie/11.0            |0.098 ms     |33.128 kb |15.568 kb |5.966 kb       |9             |Object.assign        |
|                   |             |          |          |               |              |Function.name        |
|                   |             |          |          |               |              |WeakMap              |
|                   |             |          |          |               |              |Event                |
|                   |             |          |          |               |              |Symbol               |
|                   |             |          |          |               |              |Object.setPrototypeOf|
|                   |             |          |          |               |              |CustomEvent          |
|                   |             |          |          |               |              |Promise              |
|                   |             |          |          |               |              |Proxy                |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|ios_saf/10.3.1     |0.020 ms     |6.598 kb  |1.752 kb  |0.773 kb       |1             |setImmediate         |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|safari/9.3.2       |0.046 ms     |14.545 kb |3.893 kb  |1.554 kb       |2             |setImmediate         |
|                   |             |          |          |               |              |Proxy                |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|chrome/59.0.3071.86|0.019 ms     |6.598 kb  |1.752 kb  |0.773 kb       |1             |setImmediate         |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|safari/10.1.1      |0.026 ms     |6.598 kb  |1.752 kb  |0.773 kb       |1             |setImmediate         |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
|firefox/54.0       |0.026 ms     |6.598 kb  |1.752 kb  |0.773 kb       |1             |setImmediate         |
+-------------------+-------------+----------+----------+---------------+--------------+---------------------+
```