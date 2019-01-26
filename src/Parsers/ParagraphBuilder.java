package Parsers;


import Model.Paragraph;
import javafx.beans.Observable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ParagraphBuilder implements Observable {
    private static final String sourcePrefix = "Templates/Source/";
    private static final String sourcePostfix = ".lyt";
    private static final String defaultName = "default";

    public static Paragraph parse(Node layout) throws IllegalArgumentException, TypeNotPresentException, IOException {
        if (layout.getNodeType() == Node.ELEMENT_NODE) {
            Element layoutElement =(Element)layout;
            String layoutType = layoutElement.getAttribute("type");
            String name = layoutElement.getAttribute("name");
            /*DEBUG*/System.out.println("READ TYPE : ");
            /*DEBUG*/System.out.println(layoutType);
            /*DEBUG*/System.out.println(name);
            if (name.equals("")) {
                return load(layoutType, defaultName);
            }
            else {
                return load(layoutType, name);
            }
        }
        else {
            throw new IllegalArgumentException("Passed Node is not an existing layout");
        }
    }

    public static Paragraph load(String layoutType, String layoutName) throws IOException, TypeNotPresentException{
        FileInputStream fis = new FileInputStream(sourcePrefix + layoutType + "/" + layoutName + sourcePostfix);
        String fullLayout = IOUtils.toString(fis, "UTF-8");
        return parse(fullLayout, layoutType, layoutName);
    }

    public static Paragraph parse(String fullLayout, String layoutType, String layoutName) {
        /*DEBUG*/System.out.println("FULL : ");
        /*DEBUG*/System.out.println(fullLayout);

        Pattern field = Pattern.compile("<[^<>]*>");
        Matcher matcher = field.matcher(fullLayout);

        int start = 0;
        int end = fullLayout.length();
        ArrayList<javafx.scene.Node> nodes = new ArrayList<>();
        ArrayList<Pair<String, javafx.scene.Node>> namedNodes = new ArrayList<>();

        while (matcher.find(start)) {
            end = matcher.start();

            /*DEBUG*/System.out.println("TEXT : ");
            /*DEBUG*/System.out.println(fullLayout.substring(start,end));

            nodes.add(new Text(fullLayout.substring(start,end)));
            start = matcher.end();
            end = fullLayout.length();

            String fieldStr = matcher.group().substring(1,matcher.group().length()-1);
            String fieldName = "";
            int delimPos = fieldStr.indexOf("_");
            if (delimPos == -1) {
                delimPos = fieldStr.length();
            }
            else {
                fieldName = fieldStr.substring(delimPos+1,fieldStr.length());
            }

            String fieldType = fieldStr.substring(0,delimPos);

            /*DEBUG*/System.out.print("NAME : ");
            /*DEBUG*/System.out.println(fieldName);
            javafx.scene.Node fieldNode;
            switch (fieldType) {
                case "str":
                    /*DEBUG*/System.out.println("TEXT FIELD");
                    fieldNode = new TextField();
                    break;
                default:
                    // TODO: 27.05.2018 different types of input fields
                    /*DEBUG*/System.out.println("TEXT FIELD");
                    fieldNode = new TextField();
                    break;
            }
            if (!fieldName.isEmpty()) {
                namedNodes.add(new Pair<>(fieldName, fieldNode));
            }
            nodes.add(fieldNode);
        }
        if (start != end) {
            /*DEBUG*/System.out.println("TEXT : ");
            /*DEBUG*/System.out.println(fullLayout.substring(start,end));
            nodes.add(new Text(fullLayout.substring(start,end)));
        }

        nodes.add(new Text(System.lineSeparator()+System.lineSeparator()+System.lineSeparator()));
        Paragraph paragraph = new Paragraph(fullLayout, layoutType, layoutName, nodes, namedNodes);

        /*DEBUG*/System.out.print("PARAGRAPH INFO : ");
        /*DEBUG*/System.out.println(paragraph.getSize());
        return paragraph;
    }
}
