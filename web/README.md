# Polyfill Service Website Module
This is a website module for Polyfill Service. It also contains examples of using the api and rest modules.

## Routes
- `/`
    - main website
- `/api/web/polyfills`
    - get all polyfill metadata as json
- `/api/web/polyfill/${polyfilln=Name}`
    - get a polyfill's metadata as json
- `/api/tests/test`
    - run mocha tests for polyfills
- `/api/polyfill[.min].js`
    - imported from rest module
