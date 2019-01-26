package Parsers;

import Data.DBase;
import Model.Paragraph;
import Model.Template;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.security.pkcs11.Secmod;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.UUID;

public abstract class TemplateSaver {
    public static void saveTemplate(Template template, File output) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            output = new File(output.getAbsolutePath().replace(' ', '_'));
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("template");
            doc.appendChild(rootElement);

            Element templateName = doc.createElement("templatename");
            templateName.appendChild(doc.createTextNode(template.getTemplateName()));

            rootElement.appendChild(templateName);

            for (Paragraph paragraph: template) {
                Element paragraphElement = doc.createElement("paragraph");
                paragraphElement.setAttribute("type", paragraph.getType());
                if (!paragraph.isEdited()) {
                    if (!paragraph.getName().equals("default")) {
                        paragraphElement.setAttribute("name", paragraph.getName());
                    }
                }
                else {
                    String uuid = UUID.randomUUID().toString();
                    String name = DBase.SaveTmpParagraph(paragraph.getType(), uuid, paragraph.getLayout());
                    paragraphElement.setAttribute("name", name);
                }
                rootElement.appendChild(paragraphElement);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
