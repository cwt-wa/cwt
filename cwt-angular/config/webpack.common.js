const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const FaviconsWebpackPlugin = require('favicons-webpack-plugin');
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
        new FaviconsWebpackPlugin({
            logo: './src/main/webapp/img/favicon.png',
            inject: true,
            favicons: {
                path: "/",                                // Path for overriding default icons path. `string`
                appName: 'CWT',                            // Your application's name. `string`
                appShortName: 'CWT',                       // Your application's short_name. `string`. Optional. If not set, appName will be used
                appDescription: 'Annual high prestige Worms Armageddon tournament.',                     // Your application's description. `string`
                developerName: 'Florian Zemke',                      // Your (or your developer's) name. `string`
                developerURL: "https://zemke.io",                       // Your (or your developer's) URL. `string`
                dir: "auto",                              // Primary text direction for name, short_name, and description
                lang: "en-US",                            // Primary language for name and short_name
                background: "#4e4133",                       // Background colour for flattened icons. `string`
                theme_color: "#4e4133",                      // Theme color user for example in Android's task switcher. `string`
                appleStatusBarStyle: "black-translucent", // Style for Apple status bar: "black-translucent", "default", "black". `string`
                display: "standalone",                    // Preferred display mode: "fullscreen", "standalone", "minimal-ui" or "browser". `string`
                orientation: "any",                       // Default orientation: "any", "natural", "portrait" or "landscape". `string`
                scope: "/",                               // set of URLs that the browser considers within your app
                start_url: "/?homescreen=1",              // Start URL when launching the application from a device. `string`
                version: "1.0",                           // Your application's version string. `string`
                logging: false,                           // Print logs to console? `boolean`
                pixel_art: false,                         // Keeps pixels "sharp" when scaling up, for pixel art.  Only supported in offline mode.
                loadManifestWithCredentials: false,       // Browsers don't send cookies when fetching a manifest, enable this to fix that. `boolean`
                icons: {
                    // Platform Options:
                    // - offset - offset in percentage
                    // - background:
                    //   * false - use default
                    //   * true - force use default, e.g. set background for Android icons
                    //   * color - set background for the specified icons
                    //   * mask - apply mask in order to create circle icon (applied by default for firefox). `boolean`
                    //   * overlayGlow - apply glow effect after mask has been applied (applied by default for firefox). `boolean`
                    //   * overlayShadow - apply drop shadow after mask has been applied .`boolean`
                    //
                    android: true,              // Create Android homescreen icon. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    appleIcon: true,            // Create Apple touch icons. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    appleStartup: true,         // Create Apple startup images. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    coast: true,                // Create Opera Coast icon. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    favicons: true,             // Create regular favicons. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    firefox: true,              // Create Firefox OS icons. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    windows: true,              // Create Windows 8 tile icons. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                    yandex: true                // Create Yandex browser icon. `boolean` or `{ offset, background, mask, overlayGlow, overlayShadow }`
                }
            }
        }),
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
