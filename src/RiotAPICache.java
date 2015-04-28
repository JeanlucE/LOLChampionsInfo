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
                System.out.println("Cache folder already exists!");
            }

        } catch (IOException e) {
            System.out.println("failed!");
            e.printStackTrace();
        }
    }

    public void clear() {
        RiotAPI.APIContext[] allContexts = RiotAPI.APIContext.values();

        for(RiotAPI.APIContext apic: allContexts)
        {
            String cacheDirectory = apic.getCacheDirectory();
            Path path;

            //skip api contexts where the cache is stored directly in the main cache folder
            //these files are deleted afterwards
            if(cacheDirectory == "")
                continue;
            else
                path = Paths.get(directory + cacheDirectory);

            File dir = path.toFile();

            if(dir.exists()){
                //Delete all files in directory
                for(File f: dir.listFiles())
                {
                    if (f.delete()) {
                        System.out.println("Deleted: " + f.getName());
                    }
                }

                //delete directory folder
                if (dir.delete()) {
                    System.out.println("Deleted directory: " + dir.getName());
                }
            }
        }

        //clear main directory
        File mainDir = Paths.get(directory).toFile();
        if(mainDir.exists())
        {
            for(File f: mainDir.listFiles())
            {
                if(f.delete())
                {
                    System.out.println("Deleted: " + f.getName());
                }
            }

            System.out.println("Deleted main directory files.");
        }

        System.out.println("Cache cleared!");
    }

    public JSONObject get(RiotAPI.APIContext apiContext, String key)
    {
        Path path = Paths.get(directory + apiContext.getCacheDirectory() + key + ".json");
        File f = path.toFile();

        try {
            JSONObject result = FileToJSON(f);
            System.out.println("Loaded from cache: "  + f.getName());
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public void put(RiotAPI.APIContext apiContext, String key, JSONObject jsonObject)
    {
        Path path = Paths.get(directory + apiContext.getCacheDirectory() + key + ".json");
        File f = path.toFile();

        if(f.exists())
            return;

        try {
            JSONToFile(path, jsonObject);
            System.out.println("Stored in cache: " + key + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject FileToJSON(File f) throws IOException {
        if (f.exists()) {
            //read out file from cache
            JSONTokener jsonTokener;
            FileReader fileReader = new FileReader(f);
            jsonTokener = new JSONTokener(fileReader);

            JSONObject result = new JSONObject(jsonTokener);
            fileReader.close();
            return result;
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
