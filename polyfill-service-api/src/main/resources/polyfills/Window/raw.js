
// Window
(function(global) {
	if (global.constructor) {
		global.Window = global.constructor;
	} else {
		(global.Window = global.constructor = new Function('return function Window() {}')()).prototype = this;
	}
}(this));
