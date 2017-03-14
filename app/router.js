import React, { PropTypes } from 'react';
import { Router, Route, hashHistory, IndexRedirect } from 'react-router';
import { Provider } from 'react-redux';
import App from './components/App';
import Home from './components/Home';
import Polyfill from './components/Polyfill';

const Root = ({ store }) => (
    <Provider store={ store }>
        <Router history={ hashHistory }>
            <Route path="/" component={ App }>
                <IndexRedirect to="home"/>
                <Route path="home" component={ Home }/>
                <Route path="polyfill/:pName" component={ Polyfill }/>
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired
};

export default Root;