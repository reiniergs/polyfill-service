import React from 'react';
import { connect } from 'react-redux'

import Polyfill from './Polyfill';
import VersionsSupport from './../VersionsSupport';
import SearchBox from './../SearchBox';

import {
    loadPolyfills,
    loadSupportStatus,
    quickFilterChange
} from './../../actions/Polyfills';

import './styles.scss';

const TABLE_HEADERS = ['Polyfills', 'Chrome', 'Firefox', 'Safari', 'IE & Edge'];
const BROWSERS_WITH_SUPPORT_STATUS = ['chrome', 'firefox', 'safari', 'ie'];

class PolyfillList extends React.Component {

    componentDidMount() {
        this.props.loadData();
    }

    render() {
        const { polyfills, handleFilter, filterString } = this.props;
        return (
            <div className="">
                <div className="slds-grid polyfill-list-header">
                    <div className="slds-col slds-has-flexi-truncate">
                        <div className="slds-media slds-no-space slds-media--center">
                            <div className="slds-media__body">
                                <h1 className="slds-page-header__title slds-align-middle">List of Polyfills</h1>
                                <p className="slds-text-title--caps slds-line-height--reset">Total: {polyfills.size}</p>
                            </div>
                        </div>
                    </div>
                    <div className="slds-col slds-no-flex slds-grid slds-align-top">
                        <SearchBox placeholder="Search ..." value={filterString} onChange={handleFilter} />
                    </div>
                </div>
                <table className="slds-table slds-no-row-hover polyfills-table">
                    <caption>
                        <p>Keys: 
                            <span className="status-missing">Polyfill missing</span>,
                            <span className="status-polyfilled">Polyfill supported</span>,
                            <span className="status-native">Feature supported natively</span>
                        </p>
                    </caption>
                    <thead>
                        <tr className="slds-text-title_caps">
                            {TABLE_HEADERS.map(header =>
                                <th>{header}</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        {polyfills.map(polyfill =>
                            <tr>
                                <td>
                                    <Polyfill
                                        name={polyfill.get('name')}
                                        sizeRaw={polyfill.get('sizeRaw')}
                                        sizeMin={polyfill.get('sizeMin')}
                                        key={polyfill.get('name')}
                                    />
                                </td>
                                {BROWSERS_WITH_SUPPORT_STATUS.map(browser =>
                                    <td>
                                        {this.renderSupportStatus(polyfill, browser)}
                                    </td>
                                )}
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        );
    }

    renderSupportStatus(polyfill, browser) {
        const versionsSupport = this.props.supportStatus[polyfill.get('name')];
        if (versionsSupport) {
            return <VersionsSupport data={versionsSupport[browser]} />;
        }
        return null;
    }
}

const mapStateToProps = (state) => {
    return {
        polyfills: state.Polyfills.get('filteredItems'),
        filterString: state.Polyfills.get('filterString'),
        supportStatus: state.Polyfills.get('supportStatus')
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        loadData: () => {
            dispatch(loadPolyfills());
            dispatch(loadSupportStatus());
        },
        handleFilter: value => dispatch(quickFilterChange(value))
    };
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(PolyfillList);
