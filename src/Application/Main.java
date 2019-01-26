package Application;

import Data.DBase;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.VBox;

import Control.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @FXML
    private Stage primaryStage;
    @FXML
    private  VBox rootLayout;

    private Rectangle2D screenBounds;

    private static String mainPage = "main_page.fxml", paragraphEditorPage = "paragraph_editor_page.fxml", typenamePage = "typename_page.fxml";
    private int mainHeight = 600, mainWidth = 800;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //сохраняем размеры экрана
        screenBounds = Screen.getPrimary().getVisualBounds();
        //запускаем окно входа (loginPage)
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Records");

        //FOR DEBUG
        loadPage(mainPage, screenBounds.getWidth(), screenBounds.getHeight(), true, null);
    }

    //метод загрузки окна
    public Controller loadView(String pageAdress, double pageWidth, double pageHeight, boolean maximized, boolean popUp, String ...args) {

        /*DEBUG*/
        File debug = new File(pageAdress);
        System.out.println(debug.getAbsolutePath());
        /*DEBUG*/

        FXMLLoader loader = new FXMLLoader();
        //загружаем иерархию элементов из файла по адресу mainPage
        loader.setLocation(Main.class.getResource(pageAdress));

        try {
            rootLayout = (VBox) loader.load();

            //сохраняем контроллер сцены на случай если еще понадобится
            Controller controller = loader.getController();
            Scene scene = new Scene(rootLayout, pageWidth, pageHeight);
            if (!popUp) {
                primaryStage.setScene(scene);
                primaryStage.setMaximized(maximized);
                controller.lateInit(this, primaryStage, args);
                primaryStage.show();
            }
            else {
                controller.initPopUp();
                Stage dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.initOwner(primaryStage);
                dialog.setScene(scene);
                controller.lateInit(this, dialog, args);
                dialog.showAndWait();
            }
            return controller;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void loadPage(String pageAdress, double pageWidth, double pageHeight, boolean maximized, String ...args) {
        loadView(pageAdress, pageWidth, pageHeight, maximized, false, args);
    }

    public void loadPage(String pageAdress, String ...args) {
        loadPage(pageAdress,screenBounds.getWidth(),screenBounds.getHeight(),true, args);
    }

    public void loadPage(String pageAdress) {
        loadPage(pageAdress, null);
    }


    public Controller loadPopUp(Stage stage, String pageAdress, double pageWidth, double pageHeight, String ...args) {
        return loadView(pageAdress, pageWidth, pageHeight, false, true, args);
    }

    public static String getMainPage() {
        return mainPage;
    }

    public static String getParagraphEditorPage() {
        return paragraphEditorPage;
    }

    public static String getTypenamePage() {
        return typenamePage;
    }

    public static void main(String[] args) {
        DBase.ReadDBase();
        launch(args);
    }
}
