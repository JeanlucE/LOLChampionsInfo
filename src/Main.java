import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/26/14
 * Time: 5:41 PM
 */

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("main.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent p = fxmlLoader.load(location.openStream());

        WindowController controller = fxmlLoader.getController();
        //initialize controller here
        controller.init();

        primaryStage.setScene(new Scene(p));//set width and height here
        primaryStage.setTitle("League of Legends Champions Info v0.1");
        primaryStage.show();


    }
}