var webpack = require('webpack');
var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

const ENV = process.env.NODE_ENV = process.env.ENV = 'production';

module.exports = webpackMerge(commonConfig, {
    devtool: 'source-map',

    mode: 'production',

    output: {
        path: helpers.root('target', 'dist'),
        publicPath: '/',
        filename: '[name].[hash].js',
        chunkFilename: '[id].[hash].chunk.js'
    },

    plugins: [
        new webpack.LoaderOptionsPlugin({
            options: {
                htmlLoader: {
                    minimize: false // workaround for ng2
                }
            }
        }),
        new webpack.NoEmitOnErrorsPlugin(),
        new ExtractTextPlugin({
            filename: "[name].[hash].css",
            disable: false,
            allChunks: true
        }),
        new webpack.DefinePlugin({
            'process.env': {
                'ENV': JSON.stringify(ENV),
                'apiEndpoint': JSON.stringify("/api"),
                'binaryDataStoreEndpoint': JSON.stringify("http://binary.cwtsite.com/api/"),
            }
        })
    ]
});
