var config = module.exports;

config["My tests 3"] = {
    environment: "browser", // or "node"
    rootPath: "../",
    sources: ["lib/*.js"],
    tests: ["test/**/*-tests.js"]
};