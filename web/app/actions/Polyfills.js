
const API_URL_BASE = '/api/web';

export const LOAD_POLYFILLS = 'LOAD_POLYFILLS';
export const loadPolyfill = () => {

    return (dispatch) => {
        const request = new XMLHttpRequest();

        request.addEventListener('load', function () {
            if (this.status === 200) {
                dispatch({
                    type: LOAD_POLYFILLS,
                    polyfills: JSON.parse(this.response)
                })
            }
        });

        request.open('GET', `${API_URL_BASE}/polyfills`, true);
        request.send();
    }
};

export const QUICK_FILTER_CHANGED = 'QUICK_FILTER_CHANGED';
export const quickFilterChange = function (filterString) {
    return {
        type: QUICK_FILTER_CHANGED,
        filterString
    }
};

export const LOAD_POLYFILL_META = 'LOAD_POLYFILL_META';
export const LOAD_POLYFILL_META_ERR = 'LOAD_POLYFILL_META_ERR';
export const loadPolyfillMeta = function (name) {
    return dispatch => {
        const request = new XMLHttpRequest();

        request.addEventListener('load', function () {
            if (this.status === 200) {
                dispatch({
                    type: LOAD_POLYFILL_META,
                    meta: JSON.parse(this.response)
                })
            } else if (this.status === 400) {
                const { message, polyfill } = JSON.parse(this.response);

                dispatch({
                    type: LOAD_POLYFILL_META_ERR,
                    message,
                    polyfill
                })
            }
        });

        request.open('GET', `${API_URL_BASE}/polyfill/${name}`, true);
        request.send();
    }
};