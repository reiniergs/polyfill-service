import { getDocs } from './api';

export const DOCS = {
    JAVA_API: 'polyfill-service-api',
    REST_API: 'polyfill-service-rest',
    ABOUT: ''
};

export const LOAD_DOCS = 'LOAD_DOCS';
export const loadDocs = (name) => {
    return (dispatch) => {
        getDocs(name,
            (data) => {
                dispatch({
                    type: LOAD_DOCS,
                    name: name,
                    content: data
                });
            }
        );
    }
};
