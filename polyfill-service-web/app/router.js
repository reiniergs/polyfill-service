import React, { PropTypes } from 'react';
import { Router, Route, hashHistory, IndexRedirect } from 'react-router';
import { Provider } from 'react-redux';
import App from './components/App';
import Features from './components/Features';
import About from './components/About';
import RestApiDocs from './components/RestApiDocs';
import JavaApiDocs from './components/JavaApiDocs';
import Polyfill from './components/Polyfill';

import ReactGA from 'react-ga';
ReactGA.initialize('UA-101744464-1');

function fireTracking() {
    ReactGA.pageview(window.location.hash);
}

const Root = ({ store }) => (
    <Provider store={ store }>
        <Router history={ hashHistory } onUpdate={ fireTracking }>
            <Route path="/" component={ App }>
                <IndexRedirect to="features"/>
                <Route path="features" component={ Features }/>
                <Route path="about" component={ About }/>
                <Route path="java-api-docs" component={ JavaApiDocs }/>
                <Route path="rest-api-docs" component={ RestApiDocs }/>
                <Route path="polyfill/:pName" component={ Polyfill }/>
            </Route>
        </Router>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired
};

export default Root;
