const path = require('path');

module.exports = function getFileName(pathToFile) {
    return path.basename(pathToFile);
};
