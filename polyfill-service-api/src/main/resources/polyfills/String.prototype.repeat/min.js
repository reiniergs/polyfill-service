String.prototype.repeat=function(t){"use strict";if(this===undefined||null===this)throw new TypeError(this+" is not an object");if(t<0||t===Infinity)throw new RangeError(t+" is less than zero or equal to infinity");return new Array((parseInt(t,10)||0)+1).join(this)};