const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const commonConfig = require('./webpack.common.js');
const helpers = require('./helpers');

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
            'process.env': {
                'ENV': JSON.stringify(ENV),
                'apiEndpoint': JSON.stringify("/api/"),
                'captchaKey': JSON.stringify('6LcWgLYUAAAAAOvJrsE-KX2ZZNgHqkd9tBwm-tq4'),
                'liveStreamProducer': JSON.stringify('https://twitch.cwtsite.com/produce'),
                'liveStreamSubscriber': JSON.stringify('https://twitch.cwtsite.com/subscribe'),
                'twitchBotEndpoint':  JSON.stringify('https://twitch-bot.zemke.io'),
            }
        })
    ]
});
