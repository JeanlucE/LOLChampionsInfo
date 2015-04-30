import org.json.JSONArray;

/**
 * Created with IntelliJ IDEA.
 * User: Jean-Luc
 * Date: 30.04.2015
 * Time: 12:40
 */
public interface AbilityConversion {
    Segment resolveString(JSONArray effectBurn, JSONArray vars);
}
