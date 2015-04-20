import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/26/14
 * Time: 5:41 PM
 */
//TODO name formatting: remove spaces and symbols for searching
//TODO: bug: wukong doesnt exist
public class Main extends Application{

    //Spells: SanitizedDescription, name
    // vars: spelldamage = AP
    public static void main(String[] args) {
        //ChampionsMap championsMap = ChampionsMap.getInstance();
        //Champion c = championsMap.getChampionInfo("Corki");

        //Summoner s = Summoner.getSummoner("Griphold");

        //Match m = Match.getCurrentMatch(s);

        //m.printInfo();

        //s.printInfo();
        //System.out.println();
        //c.printInfo();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();

        Parent p = fxmlLoader.load(getClass().getResource("main.fxml"));
        WindowController controller = fxmlLoader.getController();
        //initialize controller here

        primaryStage.setScene(new Scene(p));//set width and height here
        primaryStage.setTitle("League of Legends Champions Info v0.1");
        primaryStage.show();
    }
}