package Control;

import Application.Main;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public abstract class AbstractController implements Controller {
    public AbstractController() {}

    protected Main main;
    protected Stage stage;

    @Override
    public void lateInit(Main main, Stage stage, String... args) throws Exception{
        this.main = main;
        this.stage = stage;
    }

    @Override
    public void initPopUp() {

    }

    @Override
    @FXML public void initialize() {

    }
}
