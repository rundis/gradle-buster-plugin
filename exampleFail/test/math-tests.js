buster.spec.expose(); // Make some functions global

describe("A math module", function () {
    this.foo = new myapp.Math();

    it("squares 1", function () {
        expect(this.foo.square(1)).toEqual(0);
    });

    it("it raises any number to its power", function () {
        expect(this.foo.square(2)).toEqual(0);
    });

    it("double as you might expect", function() {
       expect(this.foo.double(2)).toEqual(0);
    });

});