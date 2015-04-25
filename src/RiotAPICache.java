import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jean-Luc
 * Date: 24.04.2015
 * Time: 13:24
 */
public class RiotAPICache {
    //TODO cache:
    // - summoner id
    // - champion id -> name
    // - key -> champion id
    // - champion data

    private static RiotAPICache instance;

    private final String directory = ".cache/";

    public static RiotAPICache getInstance() {
        if (instance == null)
            instance = new RiotAPICache();

        return instance;
    }

    private RiotAPICache() {
        //TODO check version for cache updates
        //create cache directory
        File f = new File(directory);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject findChampion(long id) {
        File f = new File(directory + "champion/" + id + ".json");
        //if file exists
        if (f.exists()) {
            //read out file from cache
            JSONTokener jsonTokener = new JSONTokener("");
            try {
                jsonTokener = new JSONTokener(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JSONObject result = new JSONObject(jsonTokener);
            return result;
        } else
            return null;
    }

    public void storeChampion(long id, JSONObject championJSON) {
        File f = new File(directory + "champion/" + id + ".json");

        if (!f.exists()) {
            try {
                championJSON.write(new FileWriter(f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
