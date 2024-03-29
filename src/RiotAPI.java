import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/29/14
 * Time: 12:02 AM
 */
// performance
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
    private RiotAPICache APICache;
    private boolean useCache = true;

    public static RiotAPI getInstance() {
        if (instance == null)
            instance = new RiotAPI();

        return instance;
    }

    private RiotAPI() {
        APICache = new RiotAPICache();
        //TODO if no internet is available or api is unreachable, dont clear cache

        String apiversion = getAPIVersion();
        useCache = APICache.checkVersion(apiversion);
    }

    public void clearCache() {
        APICache.clear();
    }

    public JSONObject getChampions() {

        JSONObject response = null;

        if (useCache) {
            response = APICache.get(APIContext.GET_ALL_CHAMPIONS, "allchampions");
        }

        if (response == null) {
            String requestURL = API_GET_CHAMPIONS + "?" + API_KEY;
            response = getResponse(requestURL, APIContext.GET_ALL_CHAMPIONS);
        }

        if (response != null) {
            if (useCache) {
                APICache.put(APIContext.GET_ALL_CHAMPIONS, "allchampions", response);
            }
            return response.getJSONObject("data");
        } else
            return null;
    }

    public JSONObject getChampionByID(long id) {

        JSONObject result = null;
        if (useCache) {
            result = APICache.get(APIContext.GET_CHAMPION, String.valueOf(id));
        }

        if (result == null) {
            String requestURL = API_GET_CHAMPION + id + "?" + "champData=all" + "&" + API_KEY;
            result = getResponse(requestURL, APIContext.GET_CHAMPION);
        }

        if (useCache)
            APICache.put(APIContext.GET_CHAMPION, String.valueOf(id), result);

        return result;
    }

    public JSONObject getCurrentGame(long summonerID) {
        String requestURL = API_GET_CURRENT_GAME + summonerID + "?" + API_KEY;
        return getResponse(requestURL, APIContext.GET_GAME);
    }

    public JSONObject getSummoner(String summonerName) {
        summonerName = summonerName.replaceAll("\\s", "");

        String requestURL = API_GET_SUMMONERBYNAME + summonerName + "?" + API_KEY;

        JSONObject summonerJSON = getResponse(requestURL, APIContext.GET_SUMMONER);

        return summonerJSON;
    }

    public Image getChampionLoadingScreenImage(String champion) {
        String requestURL = "./images/loading/" + champion + "_0.jpg";

        return getImage(requestURL, "getChampionLoadingScreenImage()");
    }

    public Image getChampionPassiveImage(String imageString) {
        String requestURL = "./images/passive/" + imageString;

        return getImage(requestURL, "getChampionPassiveImage()");
    }

    public Image getChampionAbilityImage(String imageString) {
        String requestURL = "./images/spell/" + imageString;

        return getImage(requestURL, "getChampionAbilityImage()");
    }

    private String getAPIVersion() {
        String requestURL = API_GET_REALM + "?" + API_KEY;
        JSONObject realmData = getResponse(requestURL, APIContext.GET_API_VERSION);
        if (realmData != null)
            return realmData.getString("v");
        else
            return null;
    }

    private JSONObject getResponse(String requestURL, APIContext context) {
        try {
            URL request = new URL(requestURL);

            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            System.out.println("Response Code: " + code);

            if (code != 200) {
                switch (code) {
                    case 400:
                        System.err.println("Bad Request: Syntax Error!");
                        break;
                    case 401:
                        System.err.println("Unauthorized Request: Invalid or no API key provided!");
                        break;
                    case 404:
                        System.err.println("Not Found: API has no match for API request made or summoner does not " +
                                "exist!");
                        break;
                    case 429:
                        System.err.println("API key rate limit reached: Please wait a moment before another request.");
                        break;
                    case 500:
                        System.err.println("Internal Server Error! Please try again!");
                        break;
                    case 503:
                        System.err.println("API Service Unavailable: Server is currently unreachable.");
                        break;
                }
            }

            InputStream in = connection.getInputStream();
            Scanner scanner = new Scanner(in);
            String response = "";
            while (scanner.hasNextLine()) {
                response += scanner.nextLine();
            }

            scanner.close();

            return new JSONObject(response);

        } catch (MalformedURLException e) {
            System.err.println("Malformed URL in " + context + "!");
            e.printStackTrace();
            return null;
        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException in " + context + "! Please check your internet connection.");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("IOException in " + context + "!");
            e.printStackTrace();
            return null;
        }
    }

    private Image getImage(String requestURL, String context) {
        Image image;
        try {
            BufferedImage bfImage = ImageIO.read(new File(requestURL));
            image = SwingFXUtils.toFXImage(bfImage, null);
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL in " + context + "!");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("IOException in " + context + "!");
            e.printStackTrace();
            return null;
        }

        return image;
    }

    public enum APIContext {
        GET_SUMMONER("getSummoner()", "summoner/"), GET_CHAMPION("getChampionByID()", "champion/"),
        GET_ALL_CHAMPIONS("getChampions()", ""), GET_GAME("getCurrentGame()", "game/"),
        GET_API_VERSION("getDataDragonVersion()", "");

        private String stringContext = "";
        private String cacheDirectory = "";

        private APIContext(String stringContext, String cacheDirectory) {
            this.stringContext = stringContext;
            this.cacheDirectory = cacheDirectory;
        }

        public String toString() {
            return stringContext;
        }

        public String getCacheDirectory() {
            return cacheDirectory;
        }
    }
}
