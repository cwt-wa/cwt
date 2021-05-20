const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const commonConfig = require('./webpack.common.js');
const helpers = require('./helpers');
const env = helpers.requireEnv(__filename);

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
            'process.env': env
        })
    ]
});
