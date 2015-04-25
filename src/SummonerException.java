/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 23/4/2015
 * Time: 12:41 AM
 */
public class SummonerException extends Exception {
    public SummonerException(String name) {
        super("Summoner with name " + name + " does not exist.");
    }

    public SummonerException(long id)
    {
        super("Summoner with id " + id + " does not exist.");
    }
}
