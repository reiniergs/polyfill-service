(function(){function t(e,n){function c(t){if(c[t]!==b)return c[t];var e;if("bug-string-char-index"==t)e="a"!="a"[0];else if("json"==t)e=c("json-stringify")&&c("json-parse");else{var r,o='{"a":[1,true,false,null,"\\u0000\\b\\n\\f\\r\\t"]}';if("json-stringify"==t){var l=n.stringify,u="function"==typeof l&&v;if(u){(r=function(){return 1}).toJSON=r;try{u="0"===l(0)&&"0"===l(new i)&&'""'==l(new a)&&l(d)===b&&l(b)===b&&l()===b&&"1"===l(r)&&"[1]"==l([r])&&"[null]"==l([b])&&"null"==l(null)&&"[null,null,null]"==l([b,d,null])&&l({a:[r,!0,!1,null,"\0\b\n\f\r\t"]})==o&&"1"===l(null,r)&&"[\n 1,\n 2\n]"==l([1,2],null,1)&&'"-271821-04-20T00:00:00.000Z"'==l(new f(-864e13))&&'"+275760-09-13T00:00:00.000Z"'==l(new f(864e13))&&'"-000001-01-01T00:00:00.000Z"'==l(new f(-621987552e5))&&'"1969-12-31T23:59:59.999Z"'==l(new f(-1))}catch(p){u=!1}}e=u}if("json-parse"==t){var s=n.parse;if("function"==typeof s)try{if(0===s("0")&&!s(!1)){r=s(o);var h=5==r.a.length&&1===r.a[0];if(h){try{h=!s('"\t"')}catch(p){}if(h)try{h=1!==s("01")}catch(p){}if(h)try{h=1!==s("1.")}catch(p){}}}}catch(p){h=!1}e=h}}return c[t]=!!e}e||(e=o.Object()),n||(n=o.Object());var i=e.Number||o.Number,a=e.String||o.String,l=e.Object||o.Object,f=e.Date||o.Date,u=e.SyntaxError||o.SyntaxError,s=e.TypeError||o.TypeError,h=e.Math||o.Math,p=e.JSON||o.JSON;"object"==typeof p&&p&&(n.stringify=p.stringify,n.parse=p.parse);var g,y,b,j=l.prototype,d=j.toString,v=new f(-0xc782b5b800cec);try{v=-109252==v.getUTCFullYear()&&0===v.getUTCMonth()&&1===v.getUTCDate()&&10==v.getUTCHours()&&37==v.getUTCMinutes()&&6==v.getUTCSeconds()&&708==v.getUTCMilliseconds()}catch(P){}if(!c("json")){var C=c("bug-string-char-index");if(!v)var S=h.floor,O=[0,31,59,90,120,151,181,212,243,273,304,334],A=function(t,e){return O[e]+365*(t-1970)+S((t-1969+(e=+(e>1)))/4)-S((t-1901+e)/100)+S((t-1601+e)/400)};if((g=j.hasOwnProperty)||(g=function(t){var e,r={};return(r.__proto__=null,r.__proto__={toString:1},r).toString!=d?g=function(t){var e=this.__proto__,r=t in(this.__proto__=null,this);return this.__proto__=e,r}:(e=r.constructor,g=function(t){var r=(this.constructor||e).prototype;return t in this&&!(t in r&&this[t]===r[t])}),r=null,g.call(this,t)}),y=function(t,e){var n,o,c,i=0;(n=function(){this.valueOf=0}).prototype.valueOf=0,o=new n;for(c in o)g.call(o,c)&&i++;return n=o=null,i?y=2==i?function(t,e){var r,n={},o="[object Function]"==d.call(t);for(r in t)o&&"prototype"==r||g.call(n,r)||!(n[r]=1)||!g.call(t,r)||e(r)}:function(t,e){var r,n,o="[object Function]"==d.call(t);for(r in t)o&&"prototype"==r||!g.call(t,r)||(n="constructor"===r)||e(r);(n||g.call(t,r="constructor"))&&e(r)}:(o=["valueOf","toString","toLocaleString","propertyIsEnumerable","isPrototypeOf","hasOwnProperty","constructor"],y=function(t,e){var n,c,i="[object Function]"==d.call(t),a=!i&&"function"!=typeof t.constructor&&r[typeof t.hasOwnProperty]&&t.hasOwnProperty||g;for(n in t)i&&"prototype"==n||!a.call(t,n)||e(n);for(c=o.length;n=o[--c];a.call(t,n)&&e(n));}),y(t,e)},!c("json-stringify")){var w={92:"\\\\",34:'\\"',8:"\\b",12:"\\f",10:"\\n",13:"\\r",9:"\\t"},T=function(t,e){return("000000"+(e||0)).slice(-t)},_=function(t){for(var e='"',r=0,n=t.length,o=!C||n>10,c=o&&(C?t.split(""):t);r<n;r++){var i=t.charCodeAt(r);switch(i){case 8:case 9:case 10:case 12:case 13:case 34:case 92:e+=w[i];break;default:if(i<32){e+="\\u00"+T(2,i.toString(16));break}e+=o?c[r]:t.charAt(r)}}return e+'"'},N=function(t,e,r,n,o,c,i){var a,l,f,u,h,p,j,v,C,O,w,U,J,m,x,M;try{a=e[t]}catch(P){}if("object"==typeof a&&a)if("[object Date]"!=(l=d.call(a))||g.call(a,"toJSON"))"function"==typeof a.toJSON&&("[object Number]"!=l&&"[object String]"!=l&&"[object Array]"!=l||g.call(a,"toJSON"))&&(a=a.toJSON(t));else if(a>-1/0&&a<1/0){if(A){for(h=S(a/864e5),f=S(h/365.2425)+1970-1;A(f+1,0)<=h;f++);for(u=S((h-A(f,0))/30.42);A(f,u+1)<=h;u++);h=1+h-A(f,u),p=(a%864e5+864e5)%864e5,j=S(p/36e5)%24,v=S(p/6e4)%60,C=S(p/1e3)%60,O=p%1e3}else f=a.getUTCFullYear(),u=a.getUTCMonth(),h=a.getUTCDate(),j=a.getUTCHours(),v=a.getUTCMinutes(),C=a.getUTCSeconds(),O=a.getUTCMilliseconds();a=(f<=0||f>=1e4?(f<0?"-":"+")+T(6,f<0?-f:f):T(4,f))+"-"+T(2,u+1)+"-"+T(2,h)+"T"+T(2,j)+":"+T(2,v)+":"+T(2,C)+"."+T(3,O)+"Z"}else a=null;if(r&&(a=r.call(e,t,a)),null===a)return"null";if("[object Boolean]"==(l=d.call(a)))return""+a;if("[object Number]"==l)return a>-1/0&&a<1/0?""+a:"null";if("[object String]"==l)return _(""+a);if("object"==typeof a){for(m=i.length;m--;)if(i[m]===a)throw s();if(i.push(a),w=[],x=c,c+=o,"[object Array]"==l){for(J=0,m=a.length;J<m;J++)U=N(J,a,r,n,o,c,i),w.push(U===b?"null":U);M=w.length?o?"[\n"+c+w.join(",\n"+c)+"\n"+x+"]":"["+w.join(",")+"]":"[]"}else y(n||a,function(t){var e=N(t,a,r,n,o,c,i);e!==b&&w.push(_(t)+":"+(o?" ":"")+e)}),M=w.length?o?"{\n"+c+w.join(",\n"+c)+"\n"+x+"}":"{"+w.join(",")+"}":"{}";return i.pop(),M}};n.stringify=function(t,e,n){var o,c,i,a;if(r[typeof e]&&e)if("[object Function]"==(a=d.call(e)))c=e;else if("[object Array]"==a){i={};for(var l,f=0,u=e.length;f<u;l=e[f++],("[object String]"==(a=d.call(l))||"[object Number]"==a)&&(i[l]=1));}if(n)if("[object Number]"==(a=d.call(n))){if((n-=n%1)>0)for(o="",n>10&&(n=10);o.length<n;o+=" ");}else"[object String]"==a&&(o=n.length<=10?n:n.slice(0,10));return N("",(l={},l[""]=t,l),c,i,o,"",[])}}if(!c("json-parse")){var U,J,m=a.fromCharCode,x={92:"\\",34:'"',47:"/",98:"\b",116:"\t",110:"\n",102:"\f",114:"\r"},M=function(){throw U=J=null,u()},F=function(){for(var t,e,r,n,o,c=J,i=c.length;U<i;)switch(o=c.charCodeAt(U)){case 9:case 10:case 13:case 32:U++;break;case 123:case 125:case 91:case 93:case 58:case 44:return t=C?c.charAt(U):c[U],U++,t;case 34:for(t="@",U++;U<i;)if((o=c.charCodeAt(U))<32)M();else if(92==o)switch(o=c.charCodeAt(++U)){case 92:case 34:case 47:case 98:case 116:case 110:case 102:case 114:t+=x[o],U++;break;case 117:for(e=++U,r=U+4;U<r;U++)(o=c.charCodeAt(U))>=48&&o<=57||o>=97&&o<=102||o>=65&&o<=70||M();t+=m("0x"+c.slice(e,U));break;default:M()}else{if(34==o)break;for(o=c.charCodeAt(U),e=U;o>=32&&92!=o&&34!=o;)o=c.charCodeAt(++U);t+=c.slice(e,U)}if(34==c.charCodeAt(U))return U++,t;M();default:if(e=U,45==o&&(n=!0,o=c.charCodeAt(++U)),o>=48&&o<=57){for(48==o&&(o=c.charCodeAt(U+1))>=48&&o<=57&&M(),n=!1;U<i&&(o=c.charCodeAt(U))>=48&&o<=57;U++);if(46==c.charCodeAt(U)){for(r=++U;r<i&&(o=c.charCodeAt(r))>=48&&o<=57;r++);r==U&&M(),U=r}if(101==(o=c.charCodeAt(U))||69==o){for(o=c.charCodeAt(++U),43!=o&&45!=o||U++,r=U;r<i&&(o=c.charCodeAt(r))>=48&&o<=57;r++);r==U&&M(),U=r}return+c.slice(e,U)}if(n&&M(),"true"==c.slice(U,U+4))return U+=4,!0;if("false"==c.slice(U,U+5))return U+=5,!1;if("null"==c.slice(U,U+4))return U+=4,null;M()}return"$"},k=function(t){var e,r;if("$"==t&&M(),"string"==typeof t){if("@"==(C?t.charAt(0):t[0]))return t.slice(1);if("["==t){for(e=[];"]"!=(t=F());r||(r=!0))r&&(","==t?"]"==(t=F())&&M():M()),","==t&&M(),e.push(k(t));return e}if("{"==t){for(e={};"}"!=(t=F());r||(r=!0))r&&(","==t?"}"==(t=F())&&M():M()),","!=t&&"string"==typeof t&&"@"==(C?t.charAt(0):t[0])&&":"==F()||M(),e[t.slice(1)]=k(F());return e}M()}return t},D=function(t,e,r){var n=E(t,e,r);n===b?delete t[e]:t[e]=n},E=function(t,e,r){var n,o=t[e];if("object"==typeof o&&o)if("[object Array]"==d.call(o))for(n=o.length;n--;)D(o,n,r);else y(o,function(t){D(o,t,r)});return r.call(t,e,o)};n.parse=function(t,e){var r,n;return U=0,J=""+t,r=k(F()),"$"!=F()&&M(),U=J=null,e&&"[object Function]"==d.call(e)?E((n={},n[""]=r,n),"",e):r}}}return n.runInContext=t,n}var e="function"==typeof define&&define.amd,r={"function":!0,object:!0},n=r[typeof exports]&&exports&&!exports.nodeType&&exports,o=r[typeof window]&&window||this,c=n&&r[typeof module]&&module&&!module.nodeType&&"object"==typeof global&&global;if(!c||c.global!==c&&c.window!==c&&c.self!==c||(o=c),n&&!e)t(o,n);else{var i=o.JSON,a=o.JSON3,l=!1,f=t(o,o.JSON3={noConflict:function(){return l||(l=!0,o.JSON=i,o.JSON3=a,i=a=null),f}});o.JSON={parse:f.parse,stringify:f.stringify}}e&&define(function(){return f})}).call(this);