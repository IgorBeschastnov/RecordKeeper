package Model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Paragraph implements Serializable, Iterable<Node> {
    private String type;
    private String name;
    private String layout;
    private Node content[];
    private ArrayList<Pair<String, Node>> namedNodes;
    private int size;
    private StringExpression value;

    public Paragraph(String layout, String type, String name, List<Node> nodes, ArrayList<Pair<String, Node>> namedNodes) {
        this.layout = layout;
        this.type = type;
        this.name = name;
        this.namedNodes = namedNodes;
        int i = 0;
        content = new Node[nodes.size()];
        for (Node node: nodes) {
            content[i] = node;
            i++;
        }
        size = content.length;
        reBindTextExpression();
    }

    public void reBindTextExpression() {
        value = new SimpleStringProperty("");
        for (Node node: content) {
            StringProperty text = new SimpleStringProperty("");
            switch (node.getClass().toString()) {
                case "class javafx.scene.control.TextField":
                    text = ((javafx.scene.control.TextField)node).textProperty();
                    break;
                case "class javafx.scene.control.Button":
                    break;
                case "class javafx.scene.control.ComboBox":
                    break;
                default:
                    text = ((javafx.scene.text.Text)node).textProperty();
                    break;
            }
            value = Bindings.concat(value, text);
        }
    }

    public ArrayList<Pair<String, Node>> getNamedNodes() {
        return namedNodes;
    }

    public int getSize() {
        return size;
    }

    public Node[] getContent() {
        return content;
    }

    public void removeByIndex(int i) throws IllegalArgumentException {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            Node[] contentNew = new Node[content.length-1];
            int j = 0;
            int a = 0;
            for (Node node: content) {
                if (j != i) {
                    contentNew[a] = node;
                    a++;
                }
                j++;
            }
            content = contentNew;
            size--;
        }
    }

    public Node getByIndex (int i) throws IllegalArgumentException {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException(Integer.toString(i) + " is not a valid element index");
        }
        else {
            return content[i];
        }
    }

    public StringExpression getValueExpression() {
        return value;
    }

    public String getText() {
        return value.get();
    }

    public String getLayout() {
        return layout;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public Iterator<Node> iterator() {
        return new NodeIterator();
    }

    public class NodeIterator implements Iterator<Node> {
        private int currentNode = 0;

        @Override
        public boolean hasNext() {
            if (currentNode >= size) {
                currentNode = 0;
                return false;
            }
            else {
                return true;
            }
        }

        @Override
        public Node next() {
            currentNode++;
            return content[currentNode-1];
        }
    }
}
