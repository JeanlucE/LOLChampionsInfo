import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 17/3/2015
 * Time: 6:52 PM
 */
//TODO special cases for abilities
public class AbilityDescription {
    public class Segment {
        public SegmentType segmentType;
        public String content;

        public Segment(SegmentType segmentType, String content) {
            this.segmentType = segmentType;
            this.content = content;
        }
    }

    public enum SegmentType {
        Normal, AP, AD, M_DMG, P_DMG, Special
    }

    private List<Segment> segments = new ArrayList<>(10);

    public AbilityDescription(JSONObject activeJSON, ActiveAbility.Type type, long championId) {
        //Ability description
        String description = activeJSON.getString("tooltip");

        //Replace effect Variables
        JSONArray effectBurn = activeJSON.getJSONArray("effectBurn");
        //find patterns of effect: {{ e4 }}
        Matcher matcher = Pattern.compile("(\\{\\{ e\\d \\}\\})").matcher(description);

        while (matcher.find()) {
            String group = matcher.group();
            int effectKey = Character.getNumericValue(group.charAt(4));
            String replacement = effectBurn.getString(effectKey);

            description = description.replace(matcher.group(), replacement);
        }

        //find all a anf f avariables to replace
        List<String> allMatches = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        matcher = Pattern.compile("\\{\\{ [af]\\d \\}\\}").matcher(description);

        while(matcher.find())
        {
            allMatches.add(matcher.group());
            starts.add(matcher.start());
            ends.add(matcher.end());
        }

        //no variables found and we are done
        if(allMatches.size() == 0)
            addNewSegment(SegmentType.Normal, description);

        //If the ability has vars
        if(activeJSON.has("vars")) {
            //get the vars
            JSONArray vars = activeJSON.getJSONArray("vars");

            //Try to match vars to patterns matched
            for (int i = 0; i < allMatches.size(); i++) {
                //Attempt to the variable
                String key = allMatches.get(i).substring(3, 5);
                for (int j = 0; j < vars.length(); j++) {

                    JSONObject var = vars.getJSONObject(j);

                    if (var.getString("key").equals(key)) {
                        //Determine segment type
                        SegmentType segmentType;
                        String link = var.getString("link");
                        if (link.equals("spelldamage")) {
                            segmentType = SegmentType.AP;
                        } else if (link.equals("attackdamage")) {
                            segmentType = SegmentType.AD;
                        } else if (link.equals("bonusattackdamage")) {
                            segmentType = SegmentType.AD;
                        } else {
                            System.out.println(link);
                            segmentType = SegmentType.Normal;
                        }

                        //determine replacement string

                    }
                }
            }
        }

        /*int endOfLastSegment = 0;

        //Replace ratio variables (a and f)
        if (activeJSON.has("vars")) {
            JSONArray vars = activeJSON.getJSONArray("vars");

            //find "a" variables
            //find patterns of vars: (+{{ a2 }})
            pattern = Pattern.compile("\\(?\\+?\\{\\{ [af]\\d \\}\\}\\)?");
            matcher = pattern.matcher(description);

            while (matcher.find()) {
                //get matched string
                String group = matcher.group();

                //get varKey
                int indexOfKey = group.indexOf("a");
                if (indexOfKey == -1)
                    indexOfKey = group.indexOf("f");

                String varKey = group.substring(indexOfKey, indexOfKey + 2);
                String replacement = "";

                //has parantheses?
                boolean hasParantheses = group.contains("(+");

                boolean varFound = false;
                for (int i = 0; i < vars.length(); i++) {
                    JSONObject var = vars.getJSONObject(i);
                    if (var.getString("key").equals(varKey)) {

                        if (hasParantheses)
                            replacement += "(+";

                        //get all ratios
                        JSONArray ratioJSON = var.getJSONArray("coeff");
                        double[] ratios = new double[ratioJSON.length()];
                        for (int j = 0; j < ratioJSON.length(); j++) {
                            ratios[j] = ratioJSON.getDouble(j);
                        }

                        for (int j = 0; j < ratios.length; j++) {
                            replacement += ratios[j];

                            if (j < ratios.length - 1) {
                                replacement += "/"; //add slash when not at end
                            }
                        }

                        replacement += " ";

                        //check what dyn is
                        String dyn = var.optString("dyn");
                        if (!dyn.equals("")) {
                            System.out.println("dyn:" + dyn);
                        }

                        SegmentType segmentType = SegmentType.Normal;
                        //AP ratio or AD ratio
                        String link = var.getString("link");
                        if (link.equals("spelldamage")) {
                            replacement += "AP";
                            segmentType = SegmentType.AP;
                        } else if (link.equals("attackdamage")) {
                            replacement += "AD";
                            segmentType = SegmentType.AD;
                        } else if (link.equals("bonusattackdamage")) {
                            replacement += "Bonus AD";
                            segmentType = SegmentType.AD;
                        } else {
                            System.out.println(link);
                            replacement += link;
                        }

                        if (hasParantheses)
                            replacement += ")";

                        addNewSegment(SegmentType.Normal, description.substring(endOfLastSegment, matcher.start()));
                        addNewSegment(segmentType, replacement);

                        description = description.replace(group, replacement);

                        varFound = true;
                        break;    //if found we are done
                    }
                }

                if (!varFound) {
                    if (hasParantheses)
                        replacement = "(+" + varKey + ")";
                    else
                        replacement = varKey;

                    addNewSegment(SegmentType.Normal, description.substring(endOfLastSegment, matcher.start()));
                    addNewSegment(SegmentType.Special, replacement);
                    endOfLastSegment = matcher.start() + replacement.length();

                    description = description.replace(group, replacement);
                }

                matcher = pattern.matcher(description);
            }
        }*/

        //addNewSegment(SegmentType.Normal, description.substring(endOfLastSegment));

        /*//Ability Damage type, can contain more than 1 damage type
        String tooltip = activeJSON.getString("sanitizedTooltip");
        if (tooltip.toLowerCase().contains("magic damage"))
            damageType = DamageType.MagicDamage;
        else if (tooltip.toLowerCase().contains("physical damage"))
            damageType = DamageType.AttackDamage;
        else if (tooltip.toLowerCase().contains("true damage"))
            damageType = DamageType.TrueDamage;
        else
            damageType = DamageType.NoDamage;    */
    }

    /*TODO think about how to add special cases
    reqs:
        - can add logic that uses effectburn and vars
        - can add just a string
        - checks for special cases from a dictionary
        - adding special cases easily
    */
    private void SpecialCases(JSONArray effectBurn, JSONArray vars, ActiveAbility.Type type, long championId)
    {

    }

    private void addNewSegment(SegmentType segmentType, String content) {
        segments.add(new Segment(segmentType, content));
    }

    @Override
    public String toString() {
        String toReturn = "";

        for (Segment s : segments)
            toReturn += s.content;

        return toReturn;
    }
}
