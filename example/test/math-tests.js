buster.spec.expose(); // Make some functions global

describe("A math module", function () {
    this.foo = new myapp.Math();

    it("squares 1", function () {
        buster.assert.equals(1, this.foo.square(1));
    });

    it("it raises any number to its power", function () {
        buster.assert.equals(4, this.foo.square(2));
    });

    it("double as you might expect", function() {
        buster.assert.equals(4, this.foo.double(2));
    });


});
