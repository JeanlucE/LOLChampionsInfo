import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/28/14
 * Time: 11:57 PM
 */
public class ChampionsMap {
    //map to get id for riot api
    private Map<String, Long> internalIDMap = new HashMap<>(124);
    //map to get name from id
    private Map<Long, String> internalNameMap = new HashMap<>(124);

    private static ChampionsMap instance;

    public static ChampionsMap getInstance(){
        if(instance == null)
            instance = new ChampionsMap();

        return instance;
    }

    private ChampionsMap(){

        JSONObject champsArray =  RiotAPI.getInstance().getChampions();
        JSONArray champNames =  champsArray.names();

        for (int i = 0; i < champNames.length(); i++) {
            String champName = champNames.getString(i);
            long id = champsArray.getJSONObject(champName).getLong("id");

            champName = champName.toLowerCase();
            internalIDMap.put(champName, id);
            internalNameMap.put(id, champName);
        }
    }

    private long getID(String champion) throws ChampionException {
        champion = champion.toLowerCase();

        //special cases where the champion name is not equal to his key
        if(champion.equals("wukong"))
            champion = "monkeyking";

        if(!championExists(champion))
            throw new ChampionException(champion);

        return internalIDMap.get(champion);
    }

    public Champion getChampionInfoByName(String name) throws ChampionException {
        //removes all whitespace and removes all special characters
        name = name.replaceAll("[^a-zA-Z]+", "").trim();

        return getChampionInfoByID(getID(name));
    }

    public Champion getChampionInfoByID(long id) throws ChampionException {
        if(!championExists(id))
            throw new ChampionException(id);

        RiotAPI riotAPI = RiotAPI.getInstance();

        JSONObject championJSON = riotAPI.getChampionByID(id);

        String name = getName(id);

        Image loadingScreenImage = riotAPI.getChampionLoadingScreenImage(name);

        return new Champion(championJSON, loadingScreenImage);
    }

    private String getName(long id)
    {
        return internalNameMap.get(id);
    }

    private boolean championExists(String name){
        return internalIDMap.containsKey(name);
    }

    private boolean championExists(long id)
    {
        return internalNameMap.containsKey(id);
    }
}
