import React from 'react';
import { Link } from 'react-router';
import Icon from '../Icon';

export default ({ name, sizeMin, sizeRaw }) => {
    return (
        <div className="slds-tile slds-media">
            <div className="slds-media__figure">
                <Icon iconName="standard:solution" assistiveText={name} />
            </div>
            <div className="slds-media__body">
                <h3 className="slds-truncate" title="SLDS_038.zip">
                    <Link to={`polyfill/${name}`}>{name}</Link>
                </h3>
                <div className="slds-tile__detail slds-text-body--small">
                    <ul className="slds-list--horizontal slds-has-dividers--right">
                        <li className="slds-item">Size Raw</li>
                        <li className="slds-item">{`${getKbSize(sizeRaw)}KB`}</li>
                    </ul>
                    <ul className="slds-list--horizontal slds-has-dividers--right">
                        <li className="slds-item">Size Min</li>
                        <li className="slds-item">{`${getKbSize(sizeMin)}KB`}</li>
                    </ul>
                </div>
            </div>
        </div>
    )
}

function getKbSize(bytes) {
    return Math.round(bytes / 1024 * 100)/100;
}