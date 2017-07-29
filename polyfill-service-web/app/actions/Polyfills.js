import {
    getPolyfills,
    getPolyfill,
    getSupportStatus
} from './api';

export const LOAD_POLYFILLS = 'LOAD_POLYFILLS';
export const loadPolyfills = () => {
    return (dispatch) => {
        getPolyfills(
            (data) => {
                dispatch({
                    type: LOAD_POLYFILLS,
                    polyfills: JSON.parse(data)
                });
            }
        );
    };
};

export const QUICK_FILTER_CHANGED = 'QUICK_FILTER_CHANGED';
export const quickFilterChange = (filterString) => {
    return {
        type: QUICK_FILTER_CHANGED,
        filterString
    };
};

export const LOAD_POLYFILL_META = 'LOAD_POLYFILL_META';
export const LOAD_POLYFILL_META_ERR = 'LOAD_POLYFILL_META_ERR';
export const loadPolyfillMeta = (name) => {
    return (dispatch) => {
        getPolyfill(name,
            (data) => {
                dispatch({
                    type: LOAD_POLYFILL_META,
                    meta: JSON.parse(data)
                });
            }, 
            (errData) => {
                const { message, polyfill } = JSON.parse(errData);
                dispatch({
                    type: LOAD_POLYFILL_META_ERR,
                    message,
                    polyfill
                });
            }
        );
    };
};

export const LOAD_SUPPORT_STATUS = 'LOAD_SUPPORT_STATUS';
export const loadSupportStatus = () => {
    return (dispatch) => {
        getSupportStatus(
            (data) => {
                dispatch({
                    type: LOAD_SUPPORT_STATUS,
                    supportStatus: JSON.parse(data)
                });
            }
        );
    };
};
