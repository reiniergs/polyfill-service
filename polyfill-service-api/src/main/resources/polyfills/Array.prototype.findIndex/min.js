Object.defineProperty(Array.prototype,"findIndex",{configurable:!0,value:function(n){if(this===undefined||null===this)throw new TypeError(this+" is not an object");if(!(n instanceof Function))throw new TypeError(n+" is not a function");for(var t=Object(this),i=arguments[1],e=t instanceof String?t.split(""):t,r=Math.max(Math.min(e.length,9007199254740991),0)||0,o=-1;++o<r;)if(o in e&&n.call(i,e[o],o,t))return o;return-1},writable:!0});