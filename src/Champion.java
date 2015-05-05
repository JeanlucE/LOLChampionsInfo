import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10/29/14
 * Time: 3:56 PM
 */
public class Champion {
    private ActiveAbility[] activeAbilities = new ActiveAbility[4];
    private PassiveAbility passiveAbility;
    private String[] enemyTips;
    private String[] allyTips;
    private boolean usesMana;
    private String name;
    private Image loadingImage;

    public Champion(JSONObject championJSON, Image loadingImage) {
        name = championJSON.getString("name");

        usesMana = championJSON.getString("partype").equals("Mana");

        JSONArray enemyTipsArray = championJSON.getJSONArray("enemytips");
        enemyTips = new String[enemyTipsArray.length()];

        for (int i = 0; i < enemyTipsArray.length(); i++) {
            enemyTips[i] = enemyTipsArray.getString(i);
        }

        JSONArray allyTipsArray = championJSON.getJSONArray("allytips");
        allyTips = new String[allyTipsArray.length()];

        for (int i = 0; i < allyTipsArray.length(); i++) {
            allyTips[i] = allyTipsArray.getString(i);
        }

        long id = championJSON.getInt("id");
        JSONArray spellsArray = championJSON.getJSONArray("spells");
        for (int i = 0; i < 4; i++) {
            activeAbilities[i] = new ActiveAbility(spellsArray.getJSONObject(i), ActiveAbility.Type.values()[i]);
        }

        JSONObject passiveJSON = championJSON.getJSONObject("passive");
        passiveAbility = new PassiveAbility(passiveJSON);

        this.loadingImage = loadingImage;
    }

    public String getName() {
        return name;
    }

    public boolean usesMana() {
        return usesMana;
    }

    public String[] getEnemyTips() {
        return enemyTips;
    }

    public String[] getAllyTips() {
        return allyTips;
    }

    public ActiveAbility getQ() {
        return activeAbilities[0];
    }

    public ActiveAbility getW() {
        return activeAbilities[1];
    }

    public ActiveAbility getE() {
        return activeAbilities[2];
    }

    public ActiveAbility getR() {
        return activeAbilities[3];
    }

    public PassiveAbility getPassive() {
        return passiveAbility;
    }

    public Image getLoadingImage() {
        return loadingImage;
    }

    /*
    public void printInfo(){
        Champion c = this;

        System.out.println("Name: " + c.getName());
        System.out.println("Uses Mana: " + c.usesMana());

        int index = 1;
        for(String s: c.getEnemyTips())
            System.out.println("Enemy Tip " + index++ + ": " + s);
        System.out.println();

        index = 1;
        for(String s: c.getAllyTips())
            System.out.println("Ally Tip " + index++ + ": " + s);
        System.out.println();

        PassiveAbility p = c.getPassive();
        System.out.println("Passive - " + p.getName() + ": " + p.getDescription());
        System.out.println();

        System.out.println("Abilities:");
        ActiveAbility q = c.getQ();
        System.out.println("Q - " + q.getName() + ": " + q.getDescription());
        System.out.println("\tDamage: " + q.getDamageType());
        System.out.println("\tScaling: " + q.getMaxScalingCoefficient() + " * " + q.getScalingType());
        System.out.println();

        ActiveAbility w = c.getW();
        System.out.println("W - " + w.getName() + ": " + w.getDescription());
        System.out.println("\tDamage: " + w.getDamageType());
        System.out.println("\tScaling: " + w.getMaxScalingCoefficient() + " * " + w.getScalingType());
        System.out.println();

        ActiveAbility e = c.getE();
        System.out.println("E - " + e.getName() + ": " + e.getDescription());
        System.out.println("\tDamage: " + e.getDamageType());
        System.out.println("\tScaling: " + e.getMaxScalingCoefficient() + " * " + e.getScalingType());
        System.out.println();

        ActiveAbility r = c.getR();
        System.out.println("R - " + r.getName() + ": " + r.getDescription());
        System.out.println("\tDamage: " + r.getDamageType());
        System.out.println("\tScaling: " + r.getMaxScalingCoefficient() + " * " + r.getScalingType());
        System.out.println();
    }*/
}
