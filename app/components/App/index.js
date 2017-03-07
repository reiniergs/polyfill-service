import React from 'react';
import GlobalHeader from './../GlobalHeader';
import './styles.scss';

export default ({children}) => {
    return (
        <div className="app-container">
            <GlobalHeader />
            <div className="app-content-container">
                <div className="app-content">
                    { children }
                </div>
            </div>
        </div>
    )
}