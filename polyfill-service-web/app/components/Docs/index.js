import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import ReactMarkdown from 'react-markdown';

import { loadDocs } from './../../actions/Docs'; 
import './styles.scss';

class Docs extends React.Component {

    componentDidMount() {
        const { name, loadDocs } = this.props;
        loadDocs(name);
    }

    render() {
        const { content } = this.props;
        return (
            <ReactMarkdown className="markdown" source={content} />
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    const { name } = ownProps;
    return {
        content: state.Docs.get(name)
    }
};

const mapDispatchToProps = (dispatch) => {
    return {
        loadDocs: (name) => dispatch(loadDocs(name))
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(Docs);
