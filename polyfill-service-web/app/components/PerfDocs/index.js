import React, { PropTypes } from 'react';

import { DOCS } from './../../actions/Docs';
import GithubRibbon from './../GithubRibbon';
import Docs from './../Docs';

export default () => (
    <div>
        <Docs name={DOCS.PERF} />
    </div>
)
