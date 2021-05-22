var path = require('path');
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

