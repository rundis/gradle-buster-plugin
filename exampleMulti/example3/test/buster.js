var config = module.exports;

config["My tests"] = {
    environment: "browser", // or "node"
    rootPath: "../",
    sources: ["lib/*.js"],
    tests: ["test/**/*-tests.js"]
};