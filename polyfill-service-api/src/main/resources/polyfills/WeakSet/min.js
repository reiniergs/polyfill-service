!function(t){var e=Date.now()%1e9,n=function(t){this.name="__st"+(1e9*Math.random()>>>0)+e+++"__",t&&t.forEach&&t.forEach(this.add,this)};n.prototype.add=function(t){var e=this.name;return t[e]||Object.defineProperty(t,e,{value:!0,writable:!0}),this},n.prototype["delete"]=function(t){return!!t[this.name]&&(t[this.name]=undefined,!0)},n.prototype.has=function(t){return!!t[this.name]},t.WeakSet=n}(this);