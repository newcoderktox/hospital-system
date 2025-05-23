package ui;

import database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

//this is the main app class it starts everything
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException { //startruns when app opens
        System.out.println("DEBUG: app  started");

        try {
            //db tables are ready
            Database.setupTables();
            System.out.println("DEBUG: db tables made or checked");
            //find login screen fxml file
            URL loginUrl = getClass().getResource("/fxml/LoginUI.fxml");
            if (loginUrl == null) {
                System.err.println("ERROR: LoginUI.fxml not found check path /fxml/LoginUI.fxml");
                System.out.println("DEBUG: fxml not found app closing");
                System.exit(1); //exit app if not found
            }
            System.out.println("DEBUG: LoginUI.fxml url found " + loginUrl);
            //load fxml file
            FXMLLoader myLoader = new FXMLLoader(loginUrl);
            System.out.println("DEBUG: FXMLLoader made");
            //make the scene from fxml
            Scene mainScene = new Scene(myLoader.load(), 800, 600);
            System.out.println("DEBUG: Scene made and fxml loaded");

            //load css style file
            try {
                URL styleUrl = getClass().getResource("/resoruces/application.css");
                if (styleUrl == null) {
                    System.err.println("ERROR: err check css");
                } else {
                    //add css to scene
                    mainScene.getStylesheets().add(styleUrl.toExternalForm());
                    System.out.println("DEBUG: application.css url found " + styleUrl);
                }
            } catch (Exception e) { //catch any css error
                System.err.println("ERROR: css load problem " + e.getMessage());
                e.printStackTrace();
            }

            //set window title and show it
            stage.setTitle("Hospital App");
            System.out.println("DEBUG: stage title set");
            stage.setScene(mainScene);
            System.out.println("DEBUG: scene set to stage");
            stage.show(); //show the window
            System.out.println("DEBUG: stage shown app start method finished");

        } catch (IOException e) { //catch fxml loading errors
            System.err.println("ERROR: fxml load or start io error " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: error app closing");
            System.exit(1); //exit app on error
        } catch (Exception e) { //catch any other errors
            System.err.println("ERROR: general app start error " + e.getMessage());
            e.printStackTrace();
            System.out.println("DEBUG: general error app closing");
            System.exit(1); //exit app on error
        }
    }

    public static void main(String[] args) { //main method runs first
        System.out.println("DEBUG: app main method started");
        launch(); //start the JavaFX app
        System.out.println("DEBUG: app main method finished");
    }
}
