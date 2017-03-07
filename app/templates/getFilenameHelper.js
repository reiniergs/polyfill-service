const path = require('path');

module.exports = function (pathToFile) {
    return path.basename(pathToFile);
};