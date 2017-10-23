var helpers = require('./helpers');

module.exports = {
    devtool: 'inline-source-map',

    resolve: {
        extensions: ['.ts', '.js']
    },

    module: {
        rules: [
            {
                test: /\.ts$/,
                use: ['ts-loader', 'angular2-template-loader']
            },
            {
                test: /\.html$/,
                use: 'html-loader'

            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                use: 'null-loader'
            },
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'main', 'webapp', 'app'),
                use: 'null-loader'
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'main', 'webapp', 'app'),
                use: 'raw-loader'
            }
        ]
    }
}
