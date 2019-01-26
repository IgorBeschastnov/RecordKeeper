package Data;

import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class DBase implements Serializable {
    private static ArrayList<ParagraphType> paragraphTypes = new ArrayList<>();
    private static ArrayList<String> templatesNames;
    private static final String TemplatesFolder = "./Templates";
    private static final String SourcesFolder = "./Templates/Source";
    private static final String paragraphExtension = ".lyt";

    public static void WriteDBase() {

    }

    public static void ReadDBase() {
        File workspace = new File(TemplatesFolder);
        if (workspace.exists()) {
            String[] directories = workspace.list((dir, name) -> new File(dir, name).isDirectory());

            /*DEBUG*/
            System.out.println("DIRECTORIES IN WORKSPACE : ");
            System.out.print("|");
            for (String directory : directories) {
                System.out.print(" " + directory + " |");
            }
            System.out.println();
            /*DEBUG*/

            String[] files = workspace.list((dir, name) -> new File(dir, name).isFile());
            String[] templates;
            int templastesCount = 0;
            for (String file : files) {
                if (file.contains(".xml")) {
                    templastesCount++;
                }
            }
            int templateNum = 0;
            templates = new String[templastesCount];
            for (String file : files) {
                if (file.contains(".xml")) {
                    templates[templateNum] = file;
                    templateNum++;
                }
            }
            //cutting the file extension
            for (int i = 0; i < templates.length; i++) {
                templates[i] = templates[i].substring(0, templates[i].length() - 4);
            }

            /*DEBUG*/
            System.out.println("TEMPLATES IN WORKSPACE : ");
            System.out.print("|");
            for (String template : templates) {
                System.out.print(" " + template + " |");
            }
            System.out.println();
            /*DEBUG*/

            templatesNames = new ArrayList<>(Arrays.asList(templates));

            File sources = new File(SourcesFolder);
            if (sources.exists()) {
                String[] types = sources.list(((dir, name) -> new File(dir, name).isDirectory()));

                /*DEBUG*/
                System.out.println("PARAGRAPHS TYPES IN SOURCE : ");
                System.out.print("|");
                for (String type : types) {
                    System.out.print(" " + type + " |");
                }
                System.out.println();
                /*DEBUG*/

                for (String type : types) {
                    File typeDir = new File(SourcesFolder + File.separator + type);
                    String[] typeDirFiles = typeDir.list((dir, name) -> new File(dir, name).isFile());
                    String[] paragraphs;
                    int paragraphsCount = 0;
                    for (String file : typeDirFiles) {
                        if (file.contains(".lyt")) {
                            paragraphsCount++;
                        }
                    }
                    int paragraphNum = 0;
                    paragraphs = new String[paragraphsCount];
                    for (String file : typeDirFiles) {
                        if (file.contains(".lyt")) {
                            paragraphs[paragraphNum] = file;
                            paragraphNum++;
                        }
                    }
                    //cutting the file extension
                    for (int i = 0; i < paragraphs.length; i++) {
                        paragraphs[i] = paragraphs[i].substring(0, paragraphs[i].length() - 4);
                    }

                    paragraphTypes.add(new ParagraphType(type, new ArrayList<>(Arrays.asList(paragraphs))));

                    /*DEBUG*/
                    System.out.println("PARAGRAPHS IN TYPE " + type + " : ");
                    System.out.print("|");
                    for (String par : paragraphs) {
                        System.out.print(" " + par + " |");
                    }
                    System.out.println();
                    /*DEBUG*/
                }
            }
            else {
                sources.mkdir();
                /*DEBUG*/System.out.println("CREATED SOURCES FOLDER");
            }
        }
        else {
            workspace.mkdir();
            /*DEBUG*/System.out.println("CREATED WORKSPACE");
            File sources = new File(SourcesFolder);
            sources.mkdir();
            /*DEBUG*/System.out.println("CREATED SOURCES FOLDER");

        }
    }

    public static ArrayList<ParagraphType> getParagraphTypes() {
        return paragraphTypes;
    }

    public static ParagraphType GetParagraphTypeByName(String name) {
        for (ParagraphType paragraphType: paragraphTypes) {
            if (paragraphType.getTypeName().equals(name)) {
                return paragraphType;
            }
        }
        return null;
    }

    public static ParagraphType NewParagraphType(String name) {
        File tempFolder = new File(SourcesFolder+File.separator+name);
        tempFolder.mkdir();
        ParagraphType paragraphType = new ParagraphType(name);
        paragraphTypes.add(paragraphType);
        return paragraphType;
    }

    // TODO: 29.05.2018 paragraph type deletions
    public static void DeleteParagraphType(String name) {

    }

    public static void DeleteParagraphType(ParagraphType type) {

    }

    public static ArrayList<String> GetTemplatesNames() {
        return templatesNames;
    }

    public static ArrayList<Pair<String, String>> getAllParagraphsTypeNamePairs() {
        ArrayList<Pair<String, String>> res = new ArrayList<>();
        for (ParagraphType paragraphType: paragraphTypes) {
            String type = paragraphType.getTypeName();
            ArrayList<String> names = paragraphType.getNames();
            for (String name: names) {
                res.add(new Pair<>(type, name));
            }
        }
        return res;
    }

    public static String GetSourcesFolder() {
        return SourcesFolder;
    }

    public static String GetTemplatesFolder() {
        return TemplatesFolder;
    }

    public static void SaveParagraph(String type, String name, String newText) {
        try {
            org.apache.commons.io.IOUtils.write(newText, new FileOutputStream(SourcesFolder + File.separator + type + File.separator + name + paragraphExtension, false));
            if (!GetParagraphTypeByName(type).hasName(name)) {
                GetParagraphTypeByName(type).addName(name);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static ParagraphType getParagraphsType(String name) {
        for (ParagraphType paragraphType: paragraphTypes) {
            if (paragraphType.hasName(name)) {
                return paragraphType;
            }
        }
        return null;
    }
    public static String GetParagraphExtension() {
        return paragraphExtension;
    }
}
