/* eslint-env mocha, browser*/
/* global proclaim, it */

describe('console', function () {

	it('error()', function () {
		proclaim.doesNotThrow(function () {
			console.error();
		});
	});

});
