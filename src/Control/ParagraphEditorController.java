package Control;

import Application.Main;
import Data.DBase;
import Data.ParagraphType;
import Model.Paragraph;
import Parsers.ParagraphBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ParagraphEditorController extends AbstractController {
    public ParagraphEditorController() {}

    private Paragraph edited = null;
    private StringProperty name;
    private String type;
    private FileChooser fileChooser = new FileChooser();
    private static final int popUpWidth = 333, popUpHeight = 25;

    @FXML private ComboBox<ParagraphType> typeBox;
    @FXML private TextField nameField;
    @FXML private Button newTypeButton;
    @FXML private TextArea paragraphEditArea;

    @FXML private MenuItem openParagraphButton;
    @FXML private MenuItem closeButton;
    @FXML private MenuItem saveButton;

    @Override
    @FXML public void initialize() {
        name = new SimpleStringProperty();
        name.bind(nameField.textProperty());
        typeBox.setOnAction(event -> type = typeBox.getSelectionModel().getSelectedItem().getTypeName());
        fileChooser.setInitialDirectory(new File(DBase.GetSourcesFolder()));
        ObservableList<ParagraphType> types = FXCollections.observableArrayList(DBase.getParagraphTypes());
        typeBox.setItems(types);
        openParagraphButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {

                /*DEBUG*/
                System.out.println("CHOOSE FILE : ");
                System.out.print(file.getParent().substring(file.getParent().lastIndexOf(File.separator)+1) + "/");
                System.out.println(file.getName());
                /*DEBUG*/

                try {
                    lateInit(main, stage, file.getParent().substring(file.getParent().lastIndexOf(File.separator) + 1), file.getName().substring(0,file.getName().lastIndexOf(".")));
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        newTypeButton.setOnAction(event -> {
            TypeNameController typeNameController = (TypeNameController)main.loadPopUp(stage, Main.getTypenamePage(), popUpWidth, popUpHeight);
            String typeName = typeNameController.getName();
            if (!typeName.equals("")) {
                if (DBase.getParagraphsType(typeName) == null) {
                    DBase.NewParagraphType(typeName);
                }
                typeBox.setItems(FXCollections.observableArrayList(DBase.getParagraphTypes()));
                typeBox.getSelectionModel().select(DBase.getParagraphsType(typeName));
            }
        });
        saveButton.setOnAction(event -> {
            if (type != null && !type.equals("")) {
                DBase.SaveParagraph(type, name.get(), paragraphEditArea.getText());
            }
        });
        closeButton.setOnAction(event -> {
            main.loadPage(Main.getMainPage());
        });

    }


    @Override
    public void lateInit(Main main, Stage stage, String... args) throws IOException {
        /*DEBUG*/System.out.println("EDITING PARAGRAPH");
        this.main = main;
        this.stage = stage;

        if (args != null) {
            String fullLayout = "";
            if (args.length == 2) {
                FileInputStream fis = new FileInputStream(DBase.GetSourcesFolder() + File.separator + args[0] + File.separator + args[1] + DBase.GetParagraphExtension());
                 fullLayout = IOUtils.toString(fis);
            }
            if (args.length == 3) {
                fullLayout = args[2];
                if (fullLayout.indexOf(System.lineSeparator()+System.lineSeparator()) == 0) {
                    fullLayout = fullLayout.substring((System.lineSeparator()+System.lineSeparator()).length());
                }
            }
            paragraphEditArea.setText(fullLayout);
            typeBox.setValue(DBase.GetParagraphTypeByName(args[0]));
            type = typeBox.getSelectionModel().getSelectedItem().getTypeName();
            nameField.setText(args[1]);
        }
    }

    @Override
    public void initPopUp() {
        blockChose();
        saveButton.setOnAction(event -> {
            edited = ParagraphBuilder.parse(paragraphEditArea.getText(), type, name.get());
            stage.close();
        });
        closeButton.setOnAction(event -> {
            stage.close();
        });
    }

    public Paragraph getEdited() {
        return edited;
    }

    private void blockChose() {
        typeBox.setDisable(true);
        nameField.setDisable(true);
        newTypeButton.setDisable(true);
        openParagraphButton.setDisable(true);
    }

    private void clearEditor() {
        nameField.setText("");
        typeBox.getSelectionModel().clearSelection();
        paragraphEditArea.setText("");
    }

}
