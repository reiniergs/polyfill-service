import React from 'react';
import './styles.scss';

const StatusClass = {
    0: 'missing',
    1: 'polyfilled',
    2: 'native'
};

function sortNumStrings(arr) {
    return arr.sort((a, b) => Number(a) - Number(b));
}

export default ({ data }) => (
    <div>
        {sortNumStrings(Object.keys(data)).map(version =>
            <span className={'status-' + StatusClass[data[version]]}>{version}</span>
        )}
    </div>
)
