import org.json.JSONObject;
import org.json.JSONTokener;

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

    private static RiotAPICache instance;

    private final String directory = "cache/";
    private final String champDirectory = directory + "champion/";
    private final String allChamps = directory + "allchampions.json";

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
            if (!path.toFile().exists()) {
                System.out.print("Creating cache...");
                Files.createDirectories(path);
                System.out.println("successful!");
            } else {
                System.out.println("Cache already exists!");
            }

        } catch (IOException e) {
            System.out.println("failed!");
            e.printStackTrace();
        }
    }

    public void clear() {
        Path championPath = Paths.get(champDirectory);
        File championDir = championPath.toFile();

        //clear champion data
        if (championDir.exists()) {
            for (File f : championDir.listFiles()) {
                if (f.delete()) {
                    System.out.println("Deleting: " + f.getName());
                }
            }

            if (championDir.delete()) {
                System.out.println("Deleting: " + championDir.getName());
            }
        }

        System.out.println("Cache cleared!");
    }

    public JSONObject findChampion(long id) {
        Path path = Paths.get(champDirectory + id + ".json");

        File f = path.toFile();
        try {
            JSONObject result = FileToJSON(f);
            return result;
        } catch (IOException e) {
            return null;
        }

    }

    public void storeChampion(long id, JSONObject championJSON) {
        Path path = Paths.get(champDirectory + id + ".json");
        File f = path.toFile();


        if (f.exists())
            return;

        try {
            JSONToFile(path, championJSON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject findAllChampions() {
        Path path = Paths.get(allChamps);

        File f = path.toFile();

        try {
            JSONObject result = FileToJSON(f);
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public void storeAllChampions(JSONObject jsonObject)
    {
        Path path = Paths.get(allChamps);
        File f = path.toFile();

        if(f.exists())
            return;

        try {
            JSONToFile(path, jsonObject);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //TODO fix
    private JSONObject FileToJSON(File f) throws IOException {
        if (f.exists()) {
            //read out file from cache
            JSONTokener jsonTokener;
            FileReader fileReader = new FileReader(f);
            jsonTokener = new JSONTokener(fileReader);
            fileReader.close();

            return new JSONObject(jsonTokener);
        } else
            throw new FileNotFoundException();
    }

    private void JSONToFile(Path path, JSONObject jsonObject) throws IOException {
        Files.createDirectories(path.getParent());
        Files.createFile(path);

        BufferedWriter bfw = new BufferedWriter(new FileWriter(path.toFile()));

        jsonObject.write(bfw);
        bfw.flush();
        bfw.close();
    }
}
