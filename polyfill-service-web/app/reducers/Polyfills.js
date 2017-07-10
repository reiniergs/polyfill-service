import { fromJS } from 'immutable';
import { LOAD_POLYFILLS, QUICK_FILTER_CHANGED } from './../actions/Polyfills';

const initialState = fromJS({
    items: [],
    filteredItems: [],
    filterString: ''
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

        default:
            return state;
    }

}

function makeFilter(state) {
    const items = state.get('items');
    const filterString = state.get('filterString');
    const filteredItems = items.filter(function (polyfill) {
        let reg = new RegExp('');
        try {
            reg = new RegExp(filterString, 'i');
        } catch(e) {}
        return reg.test(polyfill.get('name'));
    });

    return state.set('filteredItems', filteredItems);
}