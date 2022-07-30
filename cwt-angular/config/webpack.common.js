const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const helpers = require('./helpers');

const appCssExtractTextPlugin = new ExtractTextPlugin({
    filename: "app_[hash]_[name].css",
    disable: false,
    allChunks: true
});

const bootstrapCssExtractTextPlugin = new ExtractTextPlugin({
    filename: "bootstrap_[hash]_[name].css",
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
                test: /\.m?js$/,
                //exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: [
                            [
                                '@babel/preset-env',
                                {
                                    targets: "defaults"
                                }
                            ]
                        ]
                    }
                },
            },
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
                exclude: /(favicon.ico|favicon.png|loading.gif|flags\/.*?.png|weapons\/.*?.png)|grave\.png|water\.gif$/,
                use: ['file-loader?name=assets/[name].[hash].[ext]']
            },
            {
                test: /grave.png$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /water.gif$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /flags\/.*?.png$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /weapons\/.*?.png$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /favicon.ico$/,
                use: ['file-loader?name=favicon.ico']
            },
            {
                test: /loading.gif$/,
                use: ['file-loader?name=loading.gif']
            },
            {
                test: /favicon.png$/,
                use: ['file-loader?name=assets/favicon.png']
            },
            {
                type: 'javascript/auto',
                test: /manifest.json$/,
                use: ['file-loader?name=manifest.json']
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
