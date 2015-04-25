import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private final String directory = "cache/";

    public static RiotAPICache getInstance() {
        if (instance == null)
            instance = new RiotAPICache();

        return instance;
    }

    private RiotAPICache() {
        //TODO check version for cache updates
        //create cache directory
        Path path = Paths.get(directory);
        try {
            Files.createDirectories(path);
            System.out.println("Cache creation successful!");
        } catch (IOException e) {
            System.out.println("Cache creation failed!");
            e.printStackTrace();
        }
    }

    public JSONObject findChampion(long id) {
        Path path = Paths.get(directory + "champion/" + id + ".json");

        File f = path.toFile();
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
        Path path = Paths.get(directory + "champion/" + id + ".json");
        File f = path.toFile();

        if(f.exists())
            return;

        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);

            BufferedWriter bfw = new BufferedWriter(new FileWriter(f));

            championJSON.write(bfw);
            bfw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
