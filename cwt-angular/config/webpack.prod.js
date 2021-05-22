const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpackMerge = require('webpack-merge');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const commonConfig = require('./webpack.common.js');
const helpers = require('./helpers');
const env = helpers.requireEnv(__filename);

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
        new HtmlWebpackPlugin(helpers.indexTemplate(env)),
        new webpack.LoaderOptionsPlugin({
            options: {
                htmlLoader: {
                    minimize: false, // workaround for ng2
                    caseSensitive: true // https://stackoverflow.com/a/39735050/2015430#comment82236134_39735050
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
            'process.env': env
        })
    ]
});
