/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 15/3/2015
 * Time: 6:05 PM
 */

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class WindowController {
    @FXML
    private TextField summonerNameField;
    @FXML
    private ComboBox<String> summonerRegionBox;


    @FXML
    private Label championName;
    @FXML
    private ImageView championImage;

    @FXML
    private ImageView passiveSkillImage;
    @FXML
    private ImageView skill1Image;
    @FXML
    private ImageView skill2Image;
    @FXML
    private ImageView skill3Image;
    @FXML
    private ImageView skill4Image;

    @FXML
    private Label passiveName;
    @FXML
    private Label skill1Name;
    @FXML
    private Label skill2Name;
    @FXML
    private Label skill3Name;
    @FXML
    private Label skill4Name;

    @FXML
    private Label passiveDescription;
    @FXML
    private Label skill1Description;
    @FXML
    private Label skill2Description;
    @FXML
    private Label skill3Description;
    @FXML
    private Label skill4Description;

    @FXML
    private TilePane upperChampionPanel;
    @FXML
    private TilePane lowerChampionPanel;

    @FXML
    void querySummoner(ActionEvent event) {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    String champName = summonerNameField.getText();
                    final Champion champion = ChampionsMap.getInstance().getChampionInfo(champName);

                    if (champion != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                championName.setText(champion.getName());

                                championImage.setImage(champion.getLoadingImage());

                                PassiveAbility passive = champion.getPassive();
                                ActiveAbility q = champion.getQ();
                                ActiveAbility w = champion.getW();
                                ActiveAbility e = champion.getE();
                                ActiveAbility r = champion.getR();

                                passiveSkillImage.setImage(passive.getImage());
                                passiveName.setText(passive.getName());
                                passiveDescription.setText(passive.getDescription());

                                skill1Image.setImage(q.getImage());
                                skill1Name.setText(q.getName());
                                skill1Description.setText(q.getDescription());

                                skill2Image.setImage(w.getImage());
                                skill2Name.setText(w.getName());
                                skill2Description.setText(w.getDescription());

                                skill3Image.setImage(e.getImage());
                                skill3Name.setText(e.getName());
                                skill3Description.setText(e.getDescription());

                                skill4Image.setImage(r.getImage());
                                skill4Name.setText(r.getName());
                                skill4Description.setText(r.getDescription());
                            }
                        });
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();

                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

}

