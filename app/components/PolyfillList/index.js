import React from 'react';
import { connect } from 'react-redux'
import Polyfill from './Polyfill';
import { loadPolyfill, quickFilterChange } from './../../actions/Polyfills';
import './styles.scss';
import SearchBox from './../SearchBox';

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
                                <h1 className="slds-page-header__title slds-align-middle">List of polyfills</h1>
                                <p className="slds-text-title--caps slds-line-height--reset">Total: { polyfills.size }</p>
                            </div>
                        </div>
                    </div>
                    <div className="slds-col slds-no-flex slds-grid slds-align-top">
                        <SearchBox placeholder="Search ..."
                                   value={ filterString }
                                   onChange={ handleFilter }/>
                    </div>
                </div>
                <ul className="slds-has-dividers--bottom-space">
                    {
                        polyfills.map(polyfill => <Polyfill name={polyfill.get('name')}
                                                            sizeRaw={polyfill.get('sizeRaw')}
                                                            sizeMin={polyfill.get('sizeMin')}
                                                            key={polyfill.get('name')}/>)
                    }
                </ul>
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