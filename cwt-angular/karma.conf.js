module.exports = function (config) {
    config.set({
        basePath: '',
        frameworks: ['jasmine'],
        files: [
            {pattern: 'src/test/webapp/app/main.js', watched: false}
        ],
        exclude: [],
        preprocessors: {
            'src/test/webapp/app/main.js': ['webpack', 'sourcemap']
        },
        webpack: require('./config/webpack.test'),
        reporters: ['progress'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: false,
        // browsers: ['Chrome'],
        browsers: ['ChromeHeadless'],
        singleRun: true,
        concurrency: Infinity
    })
}
