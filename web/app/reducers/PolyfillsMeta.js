import { fromJS } from 'immutable';
import { LOAD_POLYFILL_META, LOAD_POLYFILL_META_ERR } from './../actions/Polyfills';

const initialState = fromJS({});

export default function(state = initialState, action) {
    switch(action.type) {
        case LOAD_POLYFILL_META:
            const { name } = action.meta;
            return state.set(name, fromJS(action.meta));

        default:
            return state;
    }
}
