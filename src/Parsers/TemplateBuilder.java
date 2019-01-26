package Parsers;

import Model.Paragraph;
import Model.Template;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class TemplateBuilder {

    private final static String templatesPrefix = "Templates/";
    private final static String templatesPostfix = ".xml";

    public static Template parse (String name) throws ParserConfigurationException, IOException, SAXException, TypeNotPresentException {
        String templateName;
        int nodesCount;
        NodeList nodeList;
        int paragraphsCount;
        ArrayList<Paragraph> paragraphs;
        HashMap<String, ArrayList<javafx.scene.Node>> namedNodes;
        try {
            String filepath = templatesPrefix + name + templatesPostfix;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            // Get the root element
            Element template = doc.getDocumentElement();
            // Assuming there is only one "templatename"
            Node nameNode = template.getElementsByTagName("templatename").item(0);

            templateName = nameNode.getNodeValue();
            nodeList = template.getChildNodes();
            nodesCount = nodeList.getLength();
            paragraphs = new ArrayList<>();
            namedNodes = new HashMap<>();

            for (int i = 0; i<nodesCount; i++) {
                /*DEBUG*/System.out.println("NEW CYCLE");
                Node nNode = nodeList.item(i);
                /*DEBUG*/System.out.print("CURRENT NODE NAME : ");
                /*DEBUG*/System.out.println(nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (!((Element)nNode).getAttribute("type").isEmpty()) {
                        /*DEBUG*/System.out.print("TYPE : ");
                        /*DEBUG*/System.out.println(((Element)nNode).getAttribute("type"));
                        Paragraph paragraph = ParagraphBuilder.parse(nNode);
                        ArrayList<Pair<String, javafx.scene.Node>> parNames = paragraph.getNamedNodes();
                        // TODO: 28.05.2018 check for correct types of samely named fields
                        for (Pair<String, javafx.scene.Node> entry: parNames) {
                            if (namedNodes.containsKey(entry.getKey())) {
                                namedNodes.get(entry.getKey()).add(entry.getValue());
                            }
                            else {
                                namedNodes.put(entry.getKey(), new ArrayList<>());
                                namedNodes.get(entry.getKey()).add(entry.getValue());
                            }
                        }
                        paragraphs.add(paragraph);
                    }
                }
            }
            paragraphsCount = paragraphs.size();
            /*DEBUG*/System.out.print("PARAGRPAHS COUNT : ");
            /*DEBUG*/System.out.println(paragraphsCount);

            return new Template(paragraphs, templateName, namedNodes);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw e;
        }
    }


}
