/* eslint-env mocha, browser*/
/* global proclaim, it */

it("Should be able to return a function with 'this' bound to the specified value", function(done){
	var thisVal = {foo:'bar'};
	var func = function(){
		proclaim.deepEqual(this, thisVal);
		done();
	};
	var testFunc = func.bind(thisVal);
	testFunc();
});

it("Handles new BoundFunction", function(done) {
	var thisVal = {foo:'bar'};
	function MyClass(){
		proclaim.notDeepEqual(this, thisVal);
		done();
	};
	var MyClassThing = MyClass.bind(thisVal);
	new MyClassThing();
});

it('Should be able to return a function with the given arguments bound', function(done){
	var thisVal = {foo:'bar'};
	var func = function(arg1, arg2){
		proclaim.deepEqual(this, thisVal);
		proclaim.equal(arg1, 'foo');
		proclaim.equal(arg2, 'bar');
		done();
	};
	var testFunc = func.bind(thisVal, 'foo');
	testFunc('bar');
});
