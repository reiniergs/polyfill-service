(function(_getPrototypeOf, Object) {
	Object.getPrototypeOf = function getPrototypeOf(o) {
		if (o == null) {
			throw new TypeError("Cannot convert undefined or null to object");
		}
		if (_getPrototypeOf == null) {
			return object.constructor ? object.constructor.prototype : null;
		}
		return _getPrototypeOf(Object(o));
	}
})(Object.getPrototypeOf, Object);
