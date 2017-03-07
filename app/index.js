import React from 'react';
import { render } from 'react-dom';
import { createStore, applyMiddleware } from 'redux';
import thunkMiddleware from 'redux-thunk'
import Root from './router';
import reducers from './reducers';

const store = createStore(
    reducers,
    applyMiddleware(
        thunkMiddleware
    )
);

render(<Root store={ store }/>, document.getElementById('app'));