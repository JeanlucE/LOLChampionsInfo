import javafx.scene.image.Image;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/29/14
 * Time: 3:57 PM
 */
public class ActiveAbility {
    private AbilityDescription description;
    private String name;
    private Type type;
    private Image image;

    public ActiveAbility(JSONObject activeJSON, Type t, long championId) {
        //Ability name
        name = activeJSON.getString("name");

        description = new AbilityDescription(activeJSON, t, championId);

        this.type = t;

        String imageURL = activeJSON.getJSONObject("image").getString("full");
        image = RiotAPI.getInstance().getChampionAbilityImage(imageURL);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description.toString();
    }

    public AbilityDescription.Segment[] getRichTextDescription()
    {
        return null;
    }

    public Type getType() {
        return type;
    }

    public Image getImage() {

        return image;
    }

    public enum Type {
        Q, W, E, R
    }
}