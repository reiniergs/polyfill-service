package org.polyfill.api.services;

import org.polyfill.api.components.Feature;
import org.polyfill.api.components.Query;
import org.polyfill.api.interfaces.QueryLoaderService;
import org.polyfill.api.interfaces.ResourceLoaderService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smo on 4/21/17.
 */
@Primary
@Service("xml")
class XMLQueryLoaderService implements QueryLoaderService, ResourceLoaderService {

    @Override
    public Query loadQuery(String filePath) throws IOException {
        File file = new File(filePath);
        InputSource is = new InputSource(file.toURI().toASCIIString());
        Document doc = loadXMLDoc(is);
        return new Query(getFeatures(doc));
    }

    @Override
    public Query loadQuery(InputStream inputStream) throws IOException {
        InputSource is = new InputSource(inputStream);
        Document doc = loadXMLDoc(is);
        return new Query(getFeatures(doc));
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
     * Get xml document parser
     * @return xml document parser
     */
    private Document loadXMLDoc(InputSource is) throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }
}
