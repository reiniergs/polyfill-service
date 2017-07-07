import React, { PropTypes, Component } from 'react';
import { Link } from 'react-router';
import './styles.scss';

export default class AppMenu extends Component {
    render() {
        return (
            <ul className="app-menu-container">
                <li className="menu-option">
                    <Link to="about">About</Link>
                </li>
                <li className="menu-option">
                    <Link to="features">Features</Link>
                </li>
                <li className="menu-option is-selected">
                    <Link to="reference">API reference</Link>
                </li>
            </ul>
        );
    }
}