package Control;

import Application.Main;
import Data.DBase;
import Data.ParagraphType;
import Model.Paragraph;
import Model.Template;
import Parsers.ParagraphBuilder;
import Parsers.TemplateBuilder;
import Parsers.TemplateWriter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class MainPageController extends AbstractController {
    public MainPageController(){}
    private static final int popUpWidth = 600, popUpHeight = 400;
    private Template template;

    private FileChooser templatefileChooser = new FileChooser();
    private FileChooser saveDocFileChooser = new FileChooser();
    @FXML private TextFlow editorFlow;
    @FXML private TextFlow viewFlow;
    @FXML private Text viewFinalText;
    @FXML private MenuItem save;
    @FXML private MenuItem quit;
    @FXML private MenuItem newFile;
    @FXML private MenuItem newParagraph;
    @FXML private MenuItem newTemplate;

    private final static String removeButtonText  = "Remove", editButtonText = "Edit", insertButtonText = "Insert paragraph";

    @Override
    @FXML public void initialize() {
        saveDocFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        saveDocFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Docx document", ".docx"));
        templatefileChooser.setInitialDirectory(new File(DBase.GetTemplatesFolder()));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Template files with in XML format (*.xml)","*.xml");
        templatefileChooser.getExtensionFilters().add(filter);
        save.setOnAction(event -> {
            if (template != null) {
                saveDocFileChooser.setInitialFileName(template.getTemplateName());
                File saveFile = saveDocFileChooser.showSaveDialog(stage);
                if(!saveFile.getName().contains(".docx")) {
                    saveFile = new File(saveFile.getAbsolutePath() + ".docx");
                }
                try {
                    TemplateWriter.WriteTemplateInstance(template, saveFile);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        quit.setOnAction(event -> stage.close());
        newFile.setOnAction(event -> {
            File file = templatefileChooser.showOpenDialog(stage);
            if (file != null) {
                String templateName = file.getName().substring(0, file.getName().lastIndexOf("."));
                /*DEBUG*/System.out.print("CHOOSEN TEMPLATE : ");
                /*DEBUG*/System.out.println(templateName);
                lateInit(main, stage, templateName);
            }
        });
        newParagraph.setOnAction(event -> loadParagraphEditWindow());
    }

    @Override
    public void lateInit(Main main, Stage stage, String ...args) {
        this.main = main;
        this.stage = stage;
        editorFlow.getChildren().clear();
        viewFinalText.textProperty().unbind();
        if (args != null) {
            try {
                template = TemplateBuilder.parse(args[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        if (template != null) try {
            for (Paragraph paragraph : template) {
                Button removeParagraph = new Button(removeButtonText);
                removeParagraph.setOnAction(event -> {
                    int parNum = template.indexOf(paragraph);
                    template.removeAtIndex(parNum);
                    lateInit(main, stage, null);
                });
                Button editParagraph = new Button(editButtonText);
                editParagraph.setOnAction(event -> {
                    String[] args1 = new String[3];
                    args1[0] = paragraph.getType();
                    args1[1] = paragraph.getName();
                    args1[2] = paragraph.getLayout();
                    int parNum = template.indexOf(paragraph);
                    popUpParagraphEditWindow(parNum, args1);

                });
                ComboBox<String> pickParagraph = new ComboBox<>();
                pickParagraph.setValue(paragraph.getName());
                pickParagraph.setItems(FXCollections.observableArrayList(DBase.GetParagraphTypeByName(paragraph.getType()).getNames()));
                pickParagraph.setOnAction(event -> {
                    try {
                        String parName = pickParagraph.getSelectionModel().getSelectedItem();
                        String parType = paragraph.getType();
                        int parNum = template.indexOf(paragraph);
                        template.replaceAtIndex(parNum, ParagraphBuilder.load(parType, parName));
                        lateInit(main, stage, null);
                    } catch (IOException | NullPointerException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                });
                ArrayList<Node> nodes = new ArrayList<>(Arrays.asList(paragraph.getContent()));
                int i = 0;
                if (((Text) nodes.get(0)).getText().equals(System.lineSeparator() + System.lineSeparator())) {
                    i = 1;
                }
                nodes.add(i, removeParagraph);
                nodes.add(i+1, editParagraph);
                nodes.add(i + 2, pickParagraph);
                editorFlow.getChildren().addAll(nodes);
                editorFlow.getChildren().remove(editorFlow.getChildren().size() - 1);
                editorFlow.getChildren().add(new Text(System.lineSeparator() + System.lineSeparator()));
                ComboBox<ParagraphType> insertParagraphButton = new ComboBox<>();
                insertParagraphButton.setPromptText(insertButtonText);
                insertParagraphButton.setItems(FXCollections.observableArrayList(DBase.getParagraphTypes()));
                insertParagraphButton.setOnAction(event -> {
                    try {
                        int parNum = template.indexOf(paragraph) + 1;
                        String parType = insertParagraphButton.getSelectionModel().getSelectedItem().getTypeName();
                        String parName = insertParagraphButton.getSelectionModel().getSelectedItem().getNames().get(0);
                        template.addAtIndex(parNum, ParagraphBuilder.load(parType, parName));
                        lateInit(main, stage, null);
                    } catch (IOException | NullPointerException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                });
                editorFlow.getChildren().add(insertParagraphButton);
                editorFlow.getChildren().add(new Text(System.lineSeparator() + System.lineSeparator()));
            }
            viewFinalText.textProperty().bind(template.getTextExpression());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void popUpParagraphEditWindow(int parNum) {
        popUpParagraphEditWindow(parNum, null);
    }

    private void popUpParagraphEditWindow(int parNum, String[] args) {
        ParagraphEditorController editorController = (ParagraphEditorController)main.loadPopUp(stage, Main.getParagraphEditorPage(), popUpWidth, popUpHeight, args);
        Paragraph edited = editorController.getEdited();
        if (edited != null) {
            System.out.print(System.lineSeparator() + "EDITED PARAGRAPH NUMBER : ");
            System.out.println(parNum);
            System.out.println(edited.getText());
            template.replaceAtIndex(parNum, edited);
            lateInit(main, stage, null);
        }
    }

    private void loadParagraphEditWindow() {
        main.loadPage(Main.getParagraphEditorPage());
    }
}
