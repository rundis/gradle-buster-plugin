buster.spec.expose(); // Make some functions global

describe("A math module 2", function () {
    this.foo = new myapp.Math();

    it("squares 1", function () {
        expect(this.foo.square(1)).toEqual(1);
    });

    it("it raises any number to its power", function () {
        expect(this.foo.square(2)).toEqual(4);
    });

    it("double as you might expect", function() {
       expect(this.foo.double(2)).toEqual("4");
    });

});