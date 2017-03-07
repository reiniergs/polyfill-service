import React from 'react';
import { IconSvg } from './../Icon';

export default class SearchBox extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            canCancel: !! props.value,
            value: props.value || ''
        }
    }

    render() {
        const { placeholder } = this.props;
        return (
            <div className="slds-form-element">
                <div className="slds-form-element__control slds-input-has-icon slds-input-has-icon--left-right">
                    <IconSvg iconName="utility:search" className="slds-input__icon slds-input__icon--left slds-icon-text-default"/>
                    <input type="search"
                           value={ this.state.value }
                           className="slds-input"
                           placeholder={ placeholder }
                           onKeyDown={ e => this.onKeyDown(e) }
                           onChange={ e => this.handleChange(e) }/>
                    { this.state.canCancel ?
                        <button className="slds-input__icon slds-input__icon--right slds-button slds-button--icon" onClick={ e => this.onCancel(e) }>
                            <IconSvg iconName="utility:clear" className="slds-button__icon"/>
                            <span className="slds-assistive-text">Clear</span>
                        </button> : null }
                </div>
            </div>
        )
    }

    handleChange(e) {
        const value = e.target.value;
        this.setState({ canCancel: value.length > 0, value: value });
        this.props.onChange && this.props.onChange(value);
    }

    onKeyDown(e) {
        if (e.keyCode === 27) {
            this.onCancel(e);
        }
    }

    onCancel(e) {
        e.nativeEvent.stopPropagation();
        this.setState({ canCancel: false, value: '' });
        this.props.onChange && this.props.onChange('');
    }
}
