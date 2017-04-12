import React, { PropTypes } from 'react';
import { Router, Route, hashHistory, IndexRedirect } from 'react-router';
import { Provider } from 'react-redux';
import App from './components/App';
import Features from './components/Features';
import About from './components/About';
import ApiReference from './components/ApiReference';
import Polyfill from './components/Polyfill';

const Root = ({ store }) => (
    <Provider store={ store }>
        <Router history={ hashHistory }>
            <Route path="/" component={ App }>
                <IndexRedirect to="features"/>
                <Route path="features" component={ Features }/>
                <Route path="about" component={ About }/>
                <Route path="reference" component={ ApiReference }/>
                <Route path="polyfill/:pName" component={ Polyfill }/>
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired
};

export default Root;