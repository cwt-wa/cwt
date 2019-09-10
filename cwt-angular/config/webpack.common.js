const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const helpers = require('./helpers');

const appCssExtractTextPlugin = new ExtractTextPlugin({
    filename: "app_[name].css",
    disable: false,
    allChunks: true
});

const bootstrapCssExtractTextPlugin = new ExtractTextPlugin({
    filename: "bootstrap_[name].css",
    disable: false,
    allChunks: true
});

module.exports = {
    entry: {
        'app': './src/main/webapp/app/main.ts'
    },

    resolve: {
        extensions: ['*', '.ts', '.js']
    },

    optimization: {
        splitChunks: {
            cacheGroups: {
                commons: {
                    test: /[\\/]node_modules[\\/]/,
                    name: "vendor",
                    chunks: "initial",
                },
            },
        },
    },

    module: {
        rules: [
            {
                test: /\.(scss)$/,
                use: bootstrapCssExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader', 'sass-loader']
                })

            },
            {
                test: /\.ts$/,
                use: ['ts-loader', 'angular2-template-loader']
            },
            {
                test: /\.html$/,
                use: ['html-loader']
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                use: ['file-loader?name=assets/[name].[hash].[ext]']
            },
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'main', 'webapp', 'app'),
                use: appCssExtractTextPlugin.extract({
                    use: "css-loader",
                    fallback: "style-loader"
                })
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'main', 'webapp', 'app'),
                use: ['raw-loader']
            }
        ]
    },

    plugins: [
        new HtmlWebpackPlugin({
            template: './src/main/webapp/app/index.html'
        }),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            "window.jQuery": "jquery",
            Popper: ["popper.js", "default"],
        }),
        bootstrapCssExtractTextPlugin,
        appCssExtractTextPlugin
    ]
};
