!function(t){function e(){}function n(t){var e,n=[];for(e in t)Object.prototype.hasOwnProperty.call(t,e)&&"length"!==e&&n.push(e);return n}function i(){var t=r;r=n(o),t.concat(r).forEach(function(t){t in o?a.setAttribute(c+t,o[t]):a.removeAttribute(c+t)}),a.setAttribute(c,r.join(",")),a.save(c)}if(e.prototype={clear:function(){n(this).forEach(this.removeItem,this)},constructor:e,getItem:function(){var t=String(arguments[0]);return t in this?this[t]:null},key:function(){var t=parseInt(arguments[0],10)||0;return n(this)[t]||null},removeItem:function(){var t=String(arguments[0]);for(t in this)delete this[t],--this.length;i()},setItem:function(){var t=String(arguments[0]),e=String(arguments[1]);t in this||++this.length,this[t]=e,i()}},!t.localStorage){var r,o=t.localStorage=new e,a=t.document.lastChild.lastChild.appendChild(t.document.createElement("x-local-storage")),c="userdata";try{a.addBehavior("#default#"+c),a.load(c)}catch(h){}r=a.getAttribute(c)?a.getAttribute(c).split(","):[],o.length=r.length,r.forEach(function(t){o[t]=a.getAttribute(c+t)}),t.attachEvent("onunload",i)}}(this);