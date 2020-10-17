const fs = require('fs');

module.exports = (identifier) => {
    browser.takeScreenshot().then(data => fs.writeFile(`${__dirname}/screenshots/${identifier}.png`, data, 'base64', console.error));
};
