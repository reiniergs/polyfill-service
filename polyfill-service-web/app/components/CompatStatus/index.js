import React from 'react';
import CompatData from './compat';
import './styles.scss';

const StatusClass = {
    0: 'missing',
    1: 'polyfilled',
    2: 'native'
};

export default ({ name, browser }) => {
    const polyfillCompatData = CompatData[name] || {};
    const compatStatus = polyfillCompatData[browser] || {};
    const versions = Object.keys(compatStatus).sort((a, b) => Number(a) - Number(b));
    return (
        <div>
            {versions.map(version =>
                <span className={'status-' + StatusClass[compatStatus[version]]}>{version}</span>
            )}
        </div>
    )
}
