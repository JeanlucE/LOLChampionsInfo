import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 11/2/2015
 * Time: 1:22 AM
 */
public class Summoner {
    private String name;
    private long ID;
    private int profileIconID;

    public static Summoner getSummoner(String name)
    {
        name = name.toLowerCase().replaceAll("\\s", "");
        JSONObject summonerJSON = RiotAPI.getInstance().getSummoner(name);
        if(summonerJSON != null)
        {
            JSONObject summonerInfo = summonerJSON.getJSONObject(name);
            String actualName = summonerInfo.getString("name");
            long ID = summonerInfo.getLong("id");
            int profileIconID = summonerInfo.getInt("profileIconId");

            return new Summoner(actualName, ID, profileIconID);
        }
        else
        {
            return null;
        }
    }

    private Summoner(String name, long ID, int profileIconID) {
        this.name = name;
        this.ID = ID;
        this.profileIconID = profileIconID;
    }

    public String getName() {
        return name;
    }

    public long getID() {
        return ID;
    }

    public int getProfileIconID() {
        return profileIconID;
    }

    public void printInfo()
    {
        System.out.println("Name: " + name);
        System.out.println("Summoner ID:" + ID);
        System.out.println("Profile Icon ID:" + profileIconID);
    }
}
