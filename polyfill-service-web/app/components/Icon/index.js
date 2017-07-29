import React from 'react';
import classnames from 'classnames';

function getIconSprite(name) {
    return name.split(':')[0];
}

function getIconName(name) {
    return name.split(':')[1];
}

export class IconSvg extends React.Component {
    render() {
        const { iconName } = this.props;
        const svgPath = `assets/icons/${getIconSprite(iconName)}-sprite/svg/symbols.svg#${getIconName(iconName)}`;
        const useTag = `<use xlink:href="${svgPath}"></use>`;

        return (
            <svg aria-hidden="true" className={this.getSvgClasses()} dangerouslySetInnerHTML={{__html: useTag}} />
        );
    }

    getSvgClasses() {
        const { className, iconName, iconSize } = this.props;
        return classnames({
            'slds-icon': !className,
            'slds-icon-text-default': getIconSprite(iconName) === 'utility' && !className,
            'slds-icon--x-small': iconSize === 'x-small',
            'slds-icon--small': iconSize === 'small',
            'slds-icon--large': iconSize === 'large'
        }, className);
    }
}

export default class Icon extends React.Component {
    render() {
        const { assistiveText, iconName, iconSize } = this.props;
        return (
            <span className={ this.getContainerClasses() } title={ assistiveText }>
                <IconSvg iconName={ iconName } iconSize={ iconSize }/>
                <span className="slds-assistive-text">{ assistiveText }</span>
            </span>
        );
    }

    getContainerClasses() {
        const { className, iconName } = this.props;
        return classnames(
            'slds-icon_container',
            `slds-icon-${getIconSprite(iconName)}-${getIconName(iconName)}`,
            className
        );
    }
}

Icon.propTypes = {
    iconName: React.PropTypes.string.isRequired,
    className: React.PropTypes.string
};
