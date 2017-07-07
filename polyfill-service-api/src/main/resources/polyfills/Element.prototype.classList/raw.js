
// Element.prototype.classList
Object.defineProperty(Element.prototype, 'classList', {
	configurable: true,
	get: function () {

		function pull() {
			var className = (typeof element.className === "object" ? element.className.baseVal : element.className);
			[].splice.apply(classList, [0, classList.length].concat((className || '').replace(/^\s+|\s+$/g, '').split(/\s+/)));
		}

		function push() {
			if (element.attachEvent) {
				element.detachEvent('onpropertychange', pull);
			}

			if (typeof element.className === "object") {
				element.className.baseVal = original.toString.call(classList);
			} else {
				element.className = original.toString.call(classList);
			}

			if (element.attachEvent) {
				element.attachEvent('onpropertychange', pull);
			}
		}

		var element = this;
		var original = _DOMTokenList.prototype;
		var ClassList = function ClassList() {};
		var classList;

		ClassList.prototype = new _DOMTokenList;

		ClassList.prototype.item = function item(index) { // eslint-disable-line no-unused-vars
			return pull(), original.item.apply(classList, arguments);
		};

		ClassList.prototype.toString = function toString() {
			return pull(), original.toString.apply(classList, arguments);
		};

		ClassList.prototype.add = function add() {
			return pull(), original.add.apply(classList, arguments), push();
		};

		ClassList.prototype.contains = function contains(token) { // eslint-disable-line no-unused-vars
			return pull(), original.contains.apply(classList, arguments);
		};

		ClassList.prototype.remove = function remove() {
			return pull(), original.remove.apply(classList, arguments), push();
		};

		ClassList.prototype.toggle = function toggle(token) {
			return pull(), token = original.toggle.apply(classList, arguments), push(), token;
		};

		classList = new ClassList;

		if (element.attachEvent) {
			element.attachEvent('onpropertychange', pull);
		}

		return classList;
	}
});
