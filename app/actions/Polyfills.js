
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

        request.open('GET', '/api/web/polyfills', true);
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