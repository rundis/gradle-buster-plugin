var config = module.exports;

config["My tests"] = {    
    environment: "browser", // or "node"
    sources: ["lib/*.js"],
    tests: ["test/**/*-tests.js"]
};