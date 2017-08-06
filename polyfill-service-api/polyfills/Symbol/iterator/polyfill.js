Object.defineProperty(Symbol, 'iterator', {value: Symbol('iterator')});

(function (Si, AP, SP) {

	function returnThis() { return this; }

	// make Arrays usable as iterators
	// so that other iterables can copy same logic
	if (!AP[Si]) AP[Si] = function () {
		var
			i = 0,
			self = this,
			iterator = {
				next: function next() {
					var done = self.length <= i;
					return done ?
						{done: done} :
						{done: done, value: self[i++]};
				}
			}
		;
		iterator[Si] = returnThis;
		return iterator;
	};

	// make Strings usable as iterators
	// to simplify Array.from and for/of like loops
	if (!SP[Si]) SP[Si] = function () {
		var
			fromCodePoint = String.fromCodePoint,
			self = this,
			i = 0,
			length = self.length,
			iterator = {
				next: function next() {
					var
						done = length <= i,
						c = done ? '' : fromCodePoint(self.codePointAt(i))
					;
					i += c.length;
					return done ?
						{done: done} :
						{done: done, value: c};
				}
			}
		;
		iterator[Si] = returnThis;
		return iterator;
	};

}(Symbol.iterator, Array.prototype, String.prototype));
