import React, { PropTypes } from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { loadPolyfillMeta } from './../../actions/Polyfills';
import { getTestUrl } from './../../actions/api';
import Highlight from 'react-highlight';
import './styles.scss';
import './foundation-theme.css';

class Polyfill extends React.Component {

    componentDidMount() {
        const { loadMeta } = this.props;
        const { routeParams: { pName }} = this.props;

        loadMeta(pName);
    }

    componentWillUpdate(nextProps) {
        const { loadMeta } = this.props;
        const { routeParams: { pName }} = this.props;
        const { routeParams: { pName: nextName }} = nextProps;

        if (nextName !== pName) {
            loadMeta(nextName);
        }
    }

    render() {
        const { routeParams: { pName }, meta } = this.props;

        return (
            <div className="polyfill-page-container">
                <h1 className="slds-text-heading--large slds-text-color--weak slds-m-bottom--small">
                    { pName }
                </h1>

                { meta ?
                    <div className="meta-container">
                        <table className="slds-table slds-table--bordered slds-max-medium-table--stacked slds-no-row-hover">
                            <thead>
                                <tr className="site-text-heading--label">
                                    <th className="slds-theme--shade">Browsers</th>
                                    <th className="slds-theme--shade">Dependencies</th>

                                    {/* license column header */}
                                    { meta.get('license') ?
                                    <th className="slds-theme--shade">License</th> : null }

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    {/* browsers */}
                                    <td className="slds-align-top" scope="row">
                                        <ul>{ this.renderBrowsers() }</ul>
                                    </td>

                                    {/* dependencies */}
                                    <td className="slds-cell-wrap slds-align-top" scope="col">
                                        <ul>{ this.renderDeps() }</ul>
                                    </td>

                                    {/* license cell */}
                                    { meta.get('license') ?
                                    <td className="slds-size--1-of-3 slds-align-top">
                                        <h1><strong>{ meta.get('license') }</strong></h1>
                                        <strong>Repository:</strong>
                                        <p><a href={ meta.get('repo') } target="_black">{ meta.get('repo') }</a>
                                        </p>
                                    </td> : null }
                                </tr>
                            </tbody>
                        </table>

                        {/* Source section */}
                        <h1 className="slds-m-top--medium slds-text-title--caps">
                            Source &nbsp;
                            <a href={getTestUrl(pName)} target="_blank">(Run Tests)</a>
                        </h1>
                        <Highlight className='javascript'>
                            { meta.get('sourceRaw') }
                        </Highlight>

                    </div> : null }
            </div>
        )
    }

    renderBrowsers() {
        const { meta } = this.props;
        return meta.get('browsers')
            .map((value, browser) => <li key={ browser }>{ browser }: <b>{ value }</b></li>);
    }

    renderDeps() {
        const { meta } = this.props;
        return meta.get('dependencies') ? meta.get('dependencies')
            .map(dep => <li key={ dep }><Link to={ `polyfill/${dep}`}>{ dep }</Link></li>) : 'None';
    }
}

const mapStateToProps = (state, ownProps) => {
    const { routeParams: { pName }} = ownProps;
    return {
        meta: state.PolyfillsMeta.get(pName)
    }
};

const mapDispatchToProps = dispatch => {
    return {
        loadMeta: pName => dispatch(loadPolyfillMeta(pName))
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Polyfill);
