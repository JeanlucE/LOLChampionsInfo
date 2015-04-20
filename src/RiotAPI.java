import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/29/14
 * Time: 12:02 AM
 */
public class RiotAPI {

    private final String API_KEY = "api_key=5b2361e1-7563-478a-8348-5c6ad50d9672";
    private final String API_BASE_Global = "https://global.api.pvp.net";
    private final String API_BASE_EUW = "https://euw.api.pvp.net";
    private final String API_GET_CHAMPIONS = API_BASE_Global + "/api/lol/static-data/euw/v1.2/champion";
    private final String API_GET_CHAMPION = API_BASE_Global + "/api/lol/static-data/euw/v1.2/champion/";
    private final String API_GET_CURRENT_GAME = API_BASE_EUW +
            "/observer-mode/rest/consumer/getSpectatorGameInfo/EUW1/";
    private final String API_GET_SUMMONERBYNAME = API_BASE_EUW + "/api/lol/euw/v1.4/summoner/by-name/";
    private final String API_DATA_DRAGON = "http://ddragon.leagueoflegends.com/cdn/";
    private final String API_GET_REALM = API_BASE_Global + "/api/lol/static-data/euw/v1.2/realm";

    private static RiotAPI instance;

    public static RiotAPI getInstance(){
        if(instance == null)
            instance = new RiotAPI();

        return instance;
    }

    private RiotAPI() {
    }

    public JSONObject getChampions(){
        String requestURL = API_GET_CHAMPIONS + "?" + API_KEY;

        JSONObject response =  getResponse(requestURL, "getChampions()");
        if(response != null)
            return response.getJSONObject("data");
        else
            return response;
    }

    public JSONObject getChampionByID(long id){
        String requestURL = API_GET_CHAMPION + id + "?" + "champData=all" + "&" + API_KEY;

        return getResponse(requestURL, "getChampionByID()");
    }

    public JSONObject getCurrentGame(long summonerID){
        String requestURL = API_GET_CURRENT_GAME + summonerID + "?" + API_KEY;
        return  getResponse(requestURL, "getCurrentGame()");
    }

    public JSONObject getSummoner(String summonerName)
    {
        summonerName = summonerName.replaceAll("\\s", "");

        String requestURL = API_GET_SUMMONERBYNAME + summonerName + "?" + API_KEY;

        return getResponse(requestURL, "getSummoner()");
    }

    public Image getChampionLoadingScreenImage(String champion)
    {
        String requestURL = "./images/loading/" + champion + "_0.jpg";

        return getImage(requestURL, "getChampionLoadingScreenImage()");
    }

    public Image getChampionPassiveImage(String imageString)
    {
        String requestURL = "./images/passive/" + imageString;

        return getImage(requestURL, "getChampionPassiveImage()");
    }

    public Image getChampionAbilityImage(String imageString)
    {
        String requestURL = "./images/spell/" + imageString;

        return getImage(requestURL, "getChampionAbilityImage()");
    }

    private String getDataDragonVersion()
    {
        String requestURL = API_GET_REALM + "?" + API_KEY;
        JSONObject realmData = getResponse(requestURL, "getDataDragonVersion()");
        return realmData.getString("dd");
    }

    //TODO catch response codes
    private JSONObject getResponse(String requestURL, String context)
    {
        try
        {
            URL request = new URL(requestURL);
            InputStream in = request.openStream();
            Scanner scanner = new Scanner(in);
            String response = "";
            while(scanner.hasNextLine())
            {
                response += scanner.nextLine();
            }

            scanner.close();

            return new JSONObject(response);

        }
        catch (MalformedURLException e)
        {
            System.err.println("Malformed URL in " + context + "!");
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            System.err.println("IOException in " + context + "!");
            e.printStackTrace();
            return null;
        }
    }

    private Image getImage(String requestURL, String context)
    {
        Image image;
        try
        {
            BufferedImage bfImage = ImageIO.read(new File(requestURL));
            image = SwingFXUtils.toFXImage(bfImage, null);
        }
        catch (MalformedURLException e)
        {
            System.err.println("Malformed URL in " + context + "!");
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            System.err.println("IOException in " + context + "!");
            e.printStackTrace();
            return null;
        }

        return image;
    }
}
