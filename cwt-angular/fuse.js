const fsbx = require('fuse-box');

const fuseBox = fsbx.FuseBox.init({
    homeDir: 'src/main/webapp/',
    sourceMap: {
        bundleReference: 'app.js.map',
        outFile: './dist/app.js.map',
    },
    outFile: './dist/app.js',
    plugins: [
        fsbx.CSSPlugin(),
        fsbx.TypeScriptHelpers(),
        fsbx.JSONPlugin(),
        fsbx.HTMLPlugin({useDefault: false})
    ]
});

fuseBox.devServer('>app/main.ts', {
    port: 8080,
});
