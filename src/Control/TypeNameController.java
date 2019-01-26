package Control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TypeNameController extends AbstractController {
    private StringProperty name = new SimpleStringProperty("");

    @FXML private TextField typenameField;
    @FXML private Button doneButton, cancelButton;

    @Override
    @FXML public void initialize() {
        name.bind(typenameField.textProperty());
        doneButton.setOnAction(event -> stage.close());
        cancelButton.setOnAction(event -> {
            name.unbind();
            name.setValue("");
            stage.close();
        });
    }

    public String getName() {
        return name.get();
    }
}
