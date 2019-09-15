var webpack = require('webpack');
var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

module.exports = webpackMerge(commonConfig, {
    devtool: 'cheap-module-eval-source-map',

    mode: 'development',

    output: {
        path: helpers.root('target', 'dist'),
        publicPath: '/',
        filename: '[name].js',
        chunkFilename: '[id].chunk.js'
    },

    devServer: {
        historyApiFallback: true,
        stats: 'minimal'
    },

    plugins: [
        new webpack.DefinePlugin({
            'process.env': {
                'apiEndpoint': JSON.stringify('http://localhost:9000/api/'),
                'binaryDataStoreEndpoint': null,
                'captchaKey': JSON.stringify('6LdAgLYUAAAAAJp86PhBUHQA33EQeJrDHBi-iWNR'),
                'liveStreamProducer': JSON.stringify('http://localhost:9999/producer'),
            }
        })
    ]
});
