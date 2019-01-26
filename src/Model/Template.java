package Model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.util.*;
import java.util.ArrayList;

public class Template implements Iterable<Paragraph> {
    private ArrayList<Paragraph> paragraphs;
    private HashMap<String, ArrayList<Node>> namedNodes;
    private String templateName;
    private StringExpression fullText;

    public Template(ArrayList<Paragraph> paragraphs, String templateName, HashMap<String, ArrayList<Node>> namedNodes) {
        this.paragraphs = paragraphs;
        this.templateName = templateName;

        /*paragraphs.get(0).removeByIndex(0);
        this.paragraphs.get(0).reBindTextExpression();*/

        reBindFullTextExpression();
        
        this.namedNodes = namedNodes;
        reBindNamedNodes();

    }

    public void reArrangeNamedNodes() {
        namedNodes = new HashMap<>();
        for (Paragraph paragraph: paragraphs) {
            ArrayList<Pair<String, Node>> parNames = paragraph.getNamedNodes();
            // TODO: 28.05.2018 check for correct types of samely named fields
            for (Pair<String, javafx.scene.Node> entry : parNames) {
                if (namedNodes.containsKey(entry.getKey())) {
                    namedNodes.get(entry.getKey()).add(entry.getValue());
                } else {
                    namedNodes.put(entry.getKey(), new ArrayList<>());
                    namedNodes.get(entry.getKey()).add(entry.getValue());
                }
            }
        }
    }

    public void reBindNamedNodes() {
        // TODO: 28.05.2018 make bindings not only for TextFields
        for (Map.Entry<String, ArrayList<Node>> entry: this.namedNodes.entrySet()) {
            if (entry.getValue().size() >= 2) {
                Node proxyNode = entry.getValue().get(0);
                for (Node node: entry.getValue().subList(1,entry.getValue().size())) {
                    ((TextField)node).textProperty().bindBidirectional(((TextField)proxyNode).textProperty());
                }
            }
        }
    }

    public void reBindFullTextExpression() {
        fullText = new SimpleStringProperty("");
        ArrayList<StringProperty> textProperties = new ArrayList<>();
        for (Paragraph paragraph: paragraphs) {
            fullText = Bindings.concat(fullText, paragraph.getValueExpression());
        }
    }

    public String getTemplateName() {
        return templateName;
    }

    public int getParagraphsCount() {
        return paragraphs.size();
    }

    public Paragraph getByIndex (int i) throws IllegalArgumentException {
        if (i < 0 || i >= paragraphs.size()) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            return paragraphs.get(i);
        }
    }

    public void replaceAtIndex(int i, Paragraph paragraph) throws IllegalArgumentException{
        if (i < 0 || i >= paragraphs.size()) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            paragraphs.add(i, paragraph);
            paragraphs.remove(i+1);
            reArrangeNamedNodes();
            reBindNamedNodes();
            reBindFullTextExpression();
        }
    }

    public void removeAtIndex(int i) throws IllegalArgumentException{
        if (i < 0 || i >= paragraphs.size()) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            paragraphs.remove(i);
            reArrangeNamedNodes();
            reBindNamedNodes();
            reBindFullTextExpression();
        }
    }

    public void addAtIndex(int i, Paragraph paragraph) throws IllegalArgumentException{
        if (i < 0 || i > paragraphs.size()) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            paragraphs.add(i, paragraph);
            reArrangeNamedNodes();
            reBindNamedNodes();
            reBindFullTextExpression();
        }
    }

    public int  indexOf(Paragraph paragraph) {
        return paragraphs.indexOf(paragraph);
    }

    public StringExpression getTextExpression() {
        return fullText;
    }

    public String getFullText() {
        return fullText.get();
    }

    @Override
    public Iterator<Paragraph> iterator() {
        return new TemplateIterator();
    }

    public class TemplateIterator implements Iterator<Paragraph> {
        int currentParagraph = 0;

        @Override
        public boolean hasNext() {
            if (currentParagraph >= paragraphs.size()) {
                currentParagraph = 0;
                return false;
            }
            else {
                return true;
            }
        }

        @Override
        public Paragraph next() {
            currentParagraph++;
            return paragraphs.get(currentParagraph-1);
        }
    }
}
