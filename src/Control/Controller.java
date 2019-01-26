package Control;

import Application.Main;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public interface Controller {
    //метод "поздней" инициализации, после того как fxml прогрузился
    void lateInit(Main main, Stage stage, String ...args) throws Exception;
    void initPopUp();
    @FXML void initialize();
}
