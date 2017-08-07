'getPrototypeOf' in Object && (function() {
    try {
        Object.getPrototypeOf('x');
        return true;
    } catch (e) {
        return false;
    }
})()
