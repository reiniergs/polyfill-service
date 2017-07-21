import { fromJS } from 'immutable';
import { LOAD_DOCS } from './../actions/Docs';

const initialState = fromJS({});

export default function(state = initialState, action) {
    switch(action.type) {
        case LOAD_DOCS:
            const { name, content } = action;
            return state.set(name, content);

        default:
            return state;
    }
}
