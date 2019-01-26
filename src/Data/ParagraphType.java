package Data;

import java.io.Serializable;
import java.util.ArrayList;

public class ParagraphType implements Serializable {
    private String name;
    private ArrayList<String> names = new ArrayList<>();

    ParagraphType(String name) {
        this.name = name;
    }

    protected ParagraphType(String name, ArrayList<String> names) {
        this.name = name;
        this.names = names;
    }

    public String getTypeName() {
        return name;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public boolean hasName(String testName) {
        return names.contains(testName);
    }

    public void addName(String newName) {
        names.add(newName);
    }

    @Override
    public String toString() {
        return name;
    }
}
