import React from 'react';
import GlobalHeader from './../GlobalHeader';
import AppMenu from './../AppMenu';
import './styles.scss';

export default ({children}) => {
    return (
        <div className="app-container">
            <GlobalHeader />
            <div className="app-content-container">
                <div className="app-content">
                    <div className="app-content-layout">
                        <div className="sidebar">
                            <AppMenu />
                        </div>
                        <div className="content">
                            { children }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
