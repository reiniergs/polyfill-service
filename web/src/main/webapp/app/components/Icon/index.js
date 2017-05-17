import React from 'react';
import classnames from 'classnames';

function getIconSprite(name) {
    return name.split(':')[0];
}

function getIconName(name) {
    return name.split(':')[1];
}

export default class Icon extends React.Component {
    render() {
        return (
            <span className={ this.getContainerClasses() } title={ this.props.assistiveText }>
                <IconSvg iconName={ this.props.iconName } iconSize={ this.props.iconSize }/>
                <span className="slds-assistive-text">{ this.props.assistiveText }</span>
            </span>
        );
    }

    getContainerClasses() {
        const { className } = this.props;
        return classnames('slds-icon_container', `slds-icon-${ getIconSprite(this.props.iconName) }-${ getIconName(this.props.iconName) }`, className);
    }
}

Icon.propTypes = {
    iconName: React.PropTypes.string.isRequired,
    className: React.PropTypes.string
};

export class IconSvg extends React.Component {
    render() {
        let useTag = `<use xlink:href="assets/icons/${ getIconSprite(this.props.iconName) }-sprite/svg/symbols.svg#${ getIconName(this.props.iconName ) }"></use>`;
        return <svg aria-hidden="true" className={ this.getSvgClasses.call(this) } dangerouslySetInnerHTML={{__html: useTag }}/>
    }

    getSvgClasses() {
        return classnames({
            'slds-icon': !this.props.className,
            'slds-icon-text-default': getIconSprite(this.props.iconName) === 'utility' && !this.props.className,
            'slds-icon--x-small': this.props.iconSize === 'x-small',
            'slds-icon--small': this.props.iconSize === 'small',
            'slds-icon--large': this.props.iconSize === 'large'
        }, this.props.className);
    }
}

