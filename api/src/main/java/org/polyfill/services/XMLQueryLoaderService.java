package org.polyfill.services;

import org.polyfill.components.Feature;
import org.polyfill.components.Query;
import org.polyfill.interfaces.QueryLoaderService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smo on 4/21/17.
 */
@Primary
@Service("xml")
class XMLQueryLoaderService implements QueryLoaderService {

    @Override
    public Query loadQuery(String filePath) {
        Document xmlDoc = loadXMLDoc(filePath);
        if (xmlDoc == null) {
            return null;
        }
        return new Query(getFeatures(xmlDoc));
    }

    /**
     * Get a list of features from a Document object
     * @param doc document object containing query configurations
     * @return a list of features
     */
    private List<Feature> getFeatures(Document doc) {
        List<Feature> features = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName("polyfill");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Feature feature = getFeature(nodeList.item(i));
            if (feature != null) {
                features.add(feature);
            }
        }
        return features;
    }

    /**
     * Get Feature info from a node
     * @param node node object extracted from xml file
     * @return feature object
     */
    private Feature getFeature(Node node) {
        String name = getNodeValue(node);
        if (name != null) {
            boolean isGated = getBoolAttr(node, "gated");
            boolean isAlways = getBoolAttr(node, "always");
            return new Feature(name, isGated, isAlways);
        }
        return null;
    }

    /**
     * Get node value from node through its text node
     * @param node node to extract text
     * @return node's value
     */
    private String getNodeValue(Node node) {
        Node textNode = node.getFirstChild();
        if (textNode != null) {
            return textNode.getNodeValue();
        }
        return null;
    }

    /**
     * Get boolean value from a node attribute
     * @param node node containing the boolean attribute
     * @param attrName attribute name of the boolean
     * @return boolean value of the attribute
     */
    private boolean getBoolAttr(Node node, String attrName) {
        Node attr = node.getAttributes().getNamedItem(attrName);
        return attr != null && Boolean.valueOf(attr.getNodeValue());
    }

    /**
     * Load XML file from filePath into a Document object
     * @param filePath file path to the xml file
     * @return Document object containing data from xml file
     */
    private Document loadXMLDoc(String filePath) {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            return null;
        }
    }
}
