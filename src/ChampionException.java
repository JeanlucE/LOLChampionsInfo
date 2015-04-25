/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 23/4/2015
 * Time: 12:40 AM
 */
public class ChampionException extends Exception {
    public ChampionException(long id) {
        super("Champion with id " + id + " does not exist");
    }

    public ChampionException(String name) {
        super("Champion with name " + name + " does not exist");
    }
}
