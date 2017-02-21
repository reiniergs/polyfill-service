/* eslint-env mocha, browser*/
/* global proclaim, it */

describe('Basic functionality', function () {
	var
	object = {},
	property = 'foo',
	value = 'bar';

	it('Returns the object being defined', function () {
		proclaim.deepEqual(Object.defineProperty(object, property, {
			configurable: true,
			enumerable: true,
			writable: true
		}), object);
	});

	it('Assigns a property', function () {
		Object.defineProperty(object, property, {
			configurable: true,
			enumerable: true,
			writable: true
		});
		proclaim.equal(property in object, true);
	});

	it('Assigns a property with a value', function () {
		Object.defineProperty(object, property, {
			configurable: true,
			enumerable: true,
			value: value,
			writable: true
		});

		proclaim.equal(object[property], value);
	});

	it('Assigns a property with a getter if getters are supported by the engine, else throws', function () {
		try {
			Object.defineProperty(object, property, {
				configurable: true,
				enumerable: true,
				get: function () {
					return value;
				}
			});
		} catch (e) {
			if (e.message !== "Getters & setters cannot be defined on this javascript engine") {
				throw e;
			}
		}

		proclaim.equal(object[property], value);
	});
});

describe('Error handling', function () {
	var
	object = {},
	property = 'foo',
	value = 'bar';

	it('Throws an error when called on a non-object', function() {
		proclaim.throws(function () {
			Object.defineProperty();
		});

		proclaim.throws(function () {
			Object.defineProperty(undefined);
		});

		proclaim.throws(function () {
			Object.defineProperty(null);
		});

		proclaim.throws(function () {
			Object.defineProperty('');
		});
	});

	it('Throws an error when descriptor is a non-object', function() {
		proclaim.throws(function () {
			Object.defineProperty(object, property);
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, undefined);
		});

		// Crashes Edge 14 on Sauce Labs, works on BrowserStack
		proclaim.throws(function () {
			Object.defineProperty(object, property, null);
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, '');
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property);
		}, /^Property description must be an object/);
	});

	it('Throws an error when both an accessor and a value are specified', function () {
		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				value: value,
				writable: true,
				enumerable: true,
				configurable: true,
				get: function () {}
			});
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				value: value,
				writable: true,
				enumerable: true,
				configurable: true,
				set: function () {}
			});
		});
	});

	it('Throws an error when an accessor is specified and writable is set', function () {
		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				get: function () {},
				writable: false
			});
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				get: function () {},
				writable: true
			});
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				set: function () {},
				writable: false
			});
		});

		proclaim.throws(function () {
			Object.defineProperty(object, property, {
				set: function () {},
				writable: true
			});
		});
	});
});
