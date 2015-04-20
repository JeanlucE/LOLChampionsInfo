import javafx.scene.image.Image;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/29/14
 * Time: 4:07 PM
 */
public class PassiveAbility {
    private String name;
    private String description;
    private Image image;

    public PassiveAbility(JSONObject passiveJSON){
        name = passiveJSON.getString("name");
        description = passiveJSON.getString("sanitizedDescription");

        String imageURL = passiveJSON.getJSONObject("image").getString("full");
        image = RiotAPI.getInstance().getChampionPassiveImage(imageURL);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Image getImage() {
        return image;
    }
}
