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
    private TextFlow skill1Description;
    @FXML
    private TextFlow skill2Description;
    @FXML
    private TextFlow skill3Description;
    @FXML
    private TextFlow skill4Description;

    @FXML
    private TilePane upperChampionPanel;
    @FXML
    private TilePane lowerChampionPanel;

    //Non-FX components
    private SegmentToTextConverter s2tConverter;

    public void init()
    {
        Font normalFont = Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 12);

        //ap: 99FF99, ad: FF8C00
        s2tConverter = new SegmentToTextConverter(normalFont, Color.WHITE);
        s2tConverter.addStyle(AbilityDescription.SegmentType.AD, normalFont, Color.rgb(255, 140, 0));
        s2tConverter.addStyle(AbilityDescription.SegmentType.AP, normalFont, Color.rgb(153, 255, 153));
        s2tConverter.addStyle(AbilityDescription.SegmentType.Special, normalFont, Color.BLUE);
        s2tConverter.addStyle(AbilityDescription.SegmentType.Replace, normalFont, Color.RED);
    }

    @FXML
    void querySummoner(ActionEvent event) {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    String champName = summonerNameField.getText();
                    final Champion champion = ChampionsMap.getInstance().getChampionInfoByName(champName);

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
                                Text[] richTextDescription1 = s2tConverter.convert(q.getRichTextDescription());
                                skill1Description.getChildren().setAll(richTextDescription1);

                                skill2Image.setImage(w.getImage());
                                skill2Name.setText(w.getName());
                                Text[] richTextDescription2 = s2tConverter.convert(w.getRichTextDescription());
                                skill2Description.getChildren().setAll(richTextDescription2);

                                skill3Image.setImage(e.getImage());
                                skill3Name.setText(e.getName());
                                Text[] richTextDescription3 = s2tConverter.convert(e.getRichTextDescription());
                                skill3Description.getChildren().setAll(richTextDescription3);

                                skill4Image.setImage(r.getImage());
                                skill4Name.setText(r.getName());
                                Text[] richTextDescription4 = s2tConverter.convert(r.getRichTextDescription());
                                skill4Description.getChildren().setAll(richTextDescription4);
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

