var path = require('path');
const fs = require('fs');
var _root = path.resolve(__dirname, '..');
function root(args) {
    args = Array.prototype.slice.call(arguments, 0);
    return path.join.apply(path, [_root].concat(args));
}
exports.root = root;

exports.requireEnv = function (pp) {
    const p = pp.match(/\.(.*?)\.js$/)[1]
    let env;
    try {
        env = require('./env');
    } catch (err) {
        console.warn(`no env for ${p} falling back to env.default.js`, err);
        env = require('./env.default');
    }
    const resultRaw = { ...(env.common || {}), ...(env[p] || {}) };
    const result = {};
    for (const k in resultRaw) {
        result[k] = JSON.stringify(resultRaw[k]);
    }
    return result;
}

exports.indexTemplate = function (env) {
    return {
        template: './src/main/webapp/app/index.ejs',
        inject: true,
        meta: {
            'description':  JSON.parse(env.description),
            'theme-color': JSON.parse(env.themeColor),
            'application-name': JSON.parse(env.nameLong),
            'apple-mobile-web-app-title': JSON.parse(env.nameShort),
        },
        templateParameters: {
            title: JSON.parse(env.title),
            nameShort: JSON.parse(env.nameShort),
            nameLong: JSON.parse(env.nameLong),
            footer: JSON.parse(env.footer),
        }
    }
};

exports.custom = function (name, fallback) {
    const f = root('custom', name);
    if (fs.existsSync(f)) {
        return f;
    }
    return root(fallback, name);
};

exports.toRgba = function (val) {
    if (val.startsWith('#')) {
        if (!/^#([A-Fa-f0-9]{3}){1,2}$/.test(val)) throw new Error(`Invalid val: ${val}`);
        let c = val.substring(1).split('');
        if (c.length == 3) c = [c[0], c[0], c[1], c[1], c[2], c[2]];
        c = `0xff${c.join('')}`;
        return [Number(c)];
    } else if (val.startsWith('rgb')) {
        return val.substring(val.indexOf("(")+1, val.length-1).split(",").map(v => Number(v.trim()));
    }
}

