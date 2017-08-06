(function(_getPrototypeOf, Object) {
	Object.getPrototypeOf = function getPrototypeOf(o) {
		if (o === null || o === undefined) {
			throw new TypeError("Cannot convert undefined or null to object");
		}
		if (_getPrototypeOf === undefined) {
			return object.constructor ? object.constructor.prototype : null;
        }
		return _getPrototypeOf(Object(o));
	}
})(Object.getPrototypeOf, Object);