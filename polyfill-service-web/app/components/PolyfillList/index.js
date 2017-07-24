import React from 'react';
import { connect } from 'react-redux'
import Polyfill from './Polyfill';
import CompatStatus from './../CompatStatus';
import SearchBox from './../SearchBox';
import { loadPolyfill, quickFilterChange } from './../../actions/Polyfills';
import './styles.scss';

class PolyfillList extends React.Component {
    componentDidMount() {
        const { load } = this.props;
        load();
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
                                <p className="slds-text-title--caps slds-line-height--reset">Total: { polyfills.size }</p>
                            </div>
                        </div>
                    </div>
                    <div className="slds-col slds-no-flex slds-grid slds-align-top">
                        <SearchBox
                            placeholder="Search ..."
                            value={ filterString }
                            onChange={ handleFilter }
                        />
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
                            <th>Polyfill</th>
                            <th>Chrome</th>
                            <th>Firefox</th>
                            <th>Safari</th>
                            <th>IE & Edge</th>
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
                                <td>
                                    <CompatStatus name={polyfill.get('name')} browser="chrome" />
                                </td>
                                <td>
                                    <CompatStatus name={polyfill.get('name')} browser="firefox" />
                                </td>
                                <td>
                                    <CompatStatus name={polyfill.get('name')} browser="safari" />
                                </td>
                                <td>
                                    <CompatStatus name={polyfill.get('name')} browser="ie" />
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        polyfills: state.Polyfills.get('filteredItems'),
        filterString: state.Polyfills.get('filterString')
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        load: () => dispatch(loadPolyfill()),
        handleFilter: value => dispatch(quickFilterChange(value))
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(PolyfillList);
