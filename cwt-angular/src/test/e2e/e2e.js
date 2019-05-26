exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    specs: ['playoff-tree.spec.js'],
    allScriptsTimeout: 999999,
    jasmineNodeOpts: {
        defaultTimeoutInterval: 999999
    },
    onPrepare: function () {
        browser.manage().window().maximize();
    }
};
