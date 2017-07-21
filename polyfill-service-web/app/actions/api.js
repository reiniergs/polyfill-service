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

export function getDocs(name, onSuccess, onError) {
    get(`${README_HOST}/${name}/README.md`, onSuccess, onError);
}
