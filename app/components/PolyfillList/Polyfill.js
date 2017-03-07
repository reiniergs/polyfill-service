import React from 'react';

export default ({name, sizeMin, sizeRaw}) => {
    const useTag = '<use xlink:href="/assets/icons/standard-sprite/svg/symbols.svg#solution"></use>';
    return (
        <li className="slds-item">
            <div className="slds-tile slds-media">
                <div className="slds-media__figure">
                    <span className="slds-icon_container slds-icon-standard-solution">
                        <svg className="slds-icon" aria-hidden="true" dangerouslySetInnerHTML={{__html: useTag }}/>
                    </span>
                </div>
                <div className="slds-media__body">
                    <h3 className="slds-truncate" title="SLDS_038.zip"><a href="javascript:void(0);">{ name }</a></h3>
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
        </li>
    )

}

function getKbSize(bytes) {
    return Math.round(bytes / 1024 * 100)/100;
}