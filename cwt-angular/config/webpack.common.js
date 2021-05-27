const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const helpers = require('./helpers');
const sass = require("node-sass");
const sassUtils = require("node-sass-utils")(sass);
let theme;
try {
    theme = require('../custom/theme');
} catch (err) {
    if (e.code !== 'MODULE_NOT_FOUND') throw err;
    theme = require('../src/main/webapp/scss/theme');
}

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
        'app': './src/main/webapp/app/main.ts',
        import: [
                    ['favicon.ico', 'src/main/webapp/img/'],
                    ['icon.png', 'src/main/webapp/img/'],
                    ['manifest.json', 'src/main/webapp/'],
                ].map(t => helpers.custom(...t))
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
                    use: [
                        'css-loader',
                        {
                            loader: 'sass-loader',
                            options: {
                                functions: {
                                    "get($key)": key => sassUtils.castToSass(sass.types.Color(...helpers.toRgba(theme[key.getValue()])))
                                }
                            }
                        }
                    ]
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
                exclude: /(favicon.ico|icon.png|loading.gif|flags\/.*?.png|weapons\/.*?.gif)$/,
                use: ['file-loader?name=assets/[name].[hash].[ext]']
            },
            {
                test: /flags\/.*?.png$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /weapons\/.*?.gif$/,
                use: ['file-loader?name=assets/[name].[ext]']
            },
            {
                test: /favicon\.ico$/,
                use: ['file-loader?name=favicon.ico']
            },
            {
                test: /loading.gif$/,
                use: ['file-loader?name=loading.gif']
            },
            {
                test: /icon.png$/,
                use: ['file-loader?name=assets/icon.png']
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
