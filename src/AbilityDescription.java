import org.json.JSONArray;
import org.json.JSONException;
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
    public class Segment implements Cloneable {
        public SegmentType segmentType;
        public String content;

        public Segment(SegmentType segmentType, String content) {
            this.segmentType = segmentType;
            this.content = content;
        }

        public Segment clone()
        {
            return new Segment(segmentType, content);
        }
    }

    public enum SegmentType {
        Normal, AP, AD, M_DMG, P_DMG, Special, Replace
    }

    private List<Segment> segments = new ArrayList<>(10);

    public AbilityDescription(JSONObject activeJSON) throws Exception {

        //ability key from riot api
        String abilityKey = activeJSON.getString("key");

        //Ability description
        String description = activeJSON.getString("sanitizedTooltip");

        //Replace effect Variables
        JSONArray effectBurn = activeJSON.getJSONArray("effectBurn");
        //find patterns of effect: {{ e4 }}
        Matcher matcher = Pattern.compile("(\\{\\{ e\\d \\}\\})").matcher(description);

        while (matcher.find()) {
            String group = matcher.group();
            int effectKey = Character.getNumericValue(group.charAt(4));

            //add special case for e variables here

            String replacement;
            try {
                replacement = effectBurn.getString(effectKey);
            } catch (JSONException e) {
                replacement = group;
                System.out.println("var e" + effectKey + " in " + abilityKey + " not found!");
            }

            description = description.replace(matcher.group(), replacement);
        }

        //find all a and f avariables to replace
        List<String> allMatches = new ArrayList<>();
        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();

        matcher = Pattern.compile("\\{\\{ [af]\\d \\}\\}").matcher(description);

        while (matcher.find()) {
            allMatches.add(matcher.group());
            starts.add(matcher.start());
            ends.add(matcher.end());
        }

        //No special vars --> we are done
        if (allMatches.size() == 0) {
            addNewSegment(SegmentType.Normal, description);
            return;
        }


        int currentIndex = 0;
        //for each match
        for (int i = 0; i < allMatches.size(); i++) {
            //add text before variable
            if (currentIndex != starts.get(i))
                addNewSegment(SegmentType.Normal, description.substring(currentIndex, starts.get(i)));
            //add variable text
            addNewSegment(SegmentType.Replace, allMatches.get(i));

            currentIndex = ends.get(i);
        }
        //add text after last variable
        if (currentIndex < description.length() - 1)
            addNewSegment(SegmentType.Normal, description.substring(currentIndex, description.length()));

        //if this code is reached vars should exist otherwise no matches were foudn and we are done
        if (!activeJSON.has("vars"))
            throw new Exception("activeJSON of " + abilityKey + " doesn't have vars but they" +
                    " are expected.");

        //get vars array
        JSONArray vars = activeJSON.getJSONArray("vars");

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);
            if (s.segmentType == SegmentType.Replace) {

                int keyIndex = s.content.indexOf('a');
                if (keyIndex == -1)
                    keyIndex = s.content.indexOf('f');

                //get key for vars array
                String key = s.content.substring(keyIndex, keyIndex + 2);

                boolean varFound = false;
                //find key in vars array
                for (int j = 0; j < vars.length(); j++) {

                    JSONObject var = vars.getJSONObject(j);

                    //key found
                    if (var.getString("key").equals(key)) {
                        varFound = true;

                        //add special cases for certain a and f variables

                        //Determine segment type
                        SegmentType segmentType;
                        String scalingType;
                        String link = var.getString("link");

                        if (link.equals("spelldamage")) {
                            segmentType = SegmentType.AP;
                            scalingType = "AP";
                        } else if (link.equals("attackdamage")) {
                            segmentType = SegmentType.AD;
                            scalingType = "AD";
                        } else if (link.equals("bonusattackdamage")) {
                            segmentType = SegmentType.AD;
                            scalingType = "Bonus AD";
                        } else {
                            System.out.println(link);
                            segmentType = SegmentType.Normal;
                            scalingType = link;
                        }

                        //determine replacement string
                        JSONArray coeff = var.getJSONArray("coeff");
                        String content = "";
                        if (coeff.length() > 0) {
                            content += coeff.getDouble(0);

                            for (int k = 1; k < coeff.length(); k++) {
                                content += "/" + coeff.getDouble(k);
                            }
                        }

                        //add scaling to content
                        content += " " + scalingType;

                        s.segmentType = segmentType;
                        s.content = content;
                    }
                }

                //preliminary check to see which abilities need special cases
                if(!varFound){
                    System.out.println("var " + key + " in " + abilityKey + " not found!");
                }

            }
        }

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
    private void SpecialCases(JSONArray effectBurn, JSONArray vars, ActiveAbility.Type type, long championId) {

    }

    private void addNewSegment(SegmentType segmentType, String content) {
        segments.add(new Segment(segmentType, content));
    }

    public Segment[] getSegments() {
        Segment[] segmentsClone = new Segment[segments.size()];

        for (int i = 0; i < segments.size(); i++) {
            segmentsClone[i] = segments.get(i).clone();
        }

        return segmentsClone;
    }

    @Override
    public String toString() {
        String toReturn = "";

        for (Segment s : segments)
            toReturn += s.content;

        return toReturn;
    }
}
