!function(){var t=Object.defineProperty,e=Date.now()%1e9,n=function(t){this.name="__st"+(1e9*Math.random()>>>0)+e+++"__",t&&t.forEach&&t.forEach(function(t){this.set.apply(this,t)},this)};n.prototype.set=function(e,n){if("object"!=typeof e&&"function"!=typeof e)throw new TypeError("Invalid value used as weak map key");var a=e[this.name];return a&&a[0]===e?a[1]=n:t(e,this.name,{value:[e,n],writable:!0}),this},n.prototype.get=function(t){var e;return(e=t[this.name])&&e[0]===t?e[1]:undefined},n.prototype["delete"]=function(t){var e=t[this.name];return!(!e||e[0]!==t)&&(e[0]=e[1]=undefined,!0)},n.prototype.has=function(t){var e=t[this.name];return!!e&&e[0]===t},this.WeakMap=n}();