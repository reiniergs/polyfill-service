import { fromJS } from 'immutable';
import {
    LOAD_POLYFILLS,
    LOAD_SUPPORT_STATUS,
    QUICK_FILTER_CHANGED
} from './../actions/Polyfills';

const initialState = fromJS({
    items: [],
    filteredItems: [],
    filterString: '',
    supportStatus: {}
});

export default (state = initialState, action) => {
    switch (action.type) {
        case LOAD_POLYFILLS:
            const polyfills = fromJS(action.polyfills);
            state = state.set('items', polyfills.sort());
            return makeFilter(state);

        case QUICK_FILTER_CHANGED:
            state = state.set('filterString', action.filterString);
            return makeFilter(state);

        case LOAD_SUPPORT_STATUS:
            return state.set('supportStatus', action.supportStatus);

        default:
            return state;
    }
}

function makeFilter(state) {
    const items = state.get('items');
    const filterString = state.get('filterString');

    let reg = new RegExp('');
    try {
        reg = new RegExp(filterString, 'i');
    } catch(e) {}

    const filteredItems = items
        .filter(polyfill => reg.test(polyfill.get('name')));

    return state.set('filteredItems', filteredItems);
}
