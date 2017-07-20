import { combineReducers } from 'redux';
import Polyfills from './Polyfills';
import PolyfillsMeta from './PolyfillsMeta';
import Docs from './Docs';

export default combineReducers({
    Docs,
    Polyfills,
    PolyfillsMeta
});
