const API_URL_BASE = '/api/web';
const README_HOST = 'https://raw.githubusercontent.com/reiniergs/polyfill-service/master/';

export function get(url, onSuccess, onError) {
    const request = new XMLHttpRequest();

    request.addEventListener('load', function () {
        if (this.status === 200) {
            if (typeof onSuccess === 'function') {
                onSuccess(this.response);
            }
        } else {
            if (typeof onError === 'function') {
                onError(this.response);
            }
        }
    });

    request.open('GET', url, true);
    request.send();
}

export function getPolyfills(onSuccess, onError) {
    get(`${API_URL_BASE}/polyfills`, onSuccess, onError);
}

export function getPolyfill(name, onSuccess, onError) {
    get(`${API_URL_BASE}/polyfill/${name}`, onSuccess, onError);
}

const ERROR_CONTENT = "Sorry :( Unable to load documentations at this time.";
export function getDocs(name, onLoad) {
    get(`${README_HOST}/${name}/README.md`,
        (content) => {
            // convert git links to our own routes
            content = content.replace(/\/polyfill-service-api\/README\.md/g, "#java-api-docs");
            content = content.replace(/\/polyfill-service-rest\/README\.md/g, "#rest-api-docs");
            content = content.replace(/\/polyfill-service-perf\/README\.md/g, "#perf-docs");
            onLoad(content);
        },
        () => {
            onLoad(ERROR_CONTENT);
        }
    );
}

export function getSupportStatus(onSuccess, onError) {
    get(`${API_URL_BASE}/support-status`, onSuccess, onError);
}
