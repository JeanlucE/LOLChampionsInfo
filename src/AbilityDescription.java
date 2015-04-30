import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 17/3/2015
 * Time: 6:52 PM
 */
//TODO special cases for abilities!

public class AbilityDescription {
    private List<Segment> segments = new ArrayList<>(10);
    private static Map<String, AbilityConversion> specialCases = new HashMap<>(50);

    static {
        specialCases.put("ahrifoxfiref1", new AbilityConversion() {
            @Override
            public Segment resolveString(JSONArray effectBurn, JSONArray vars) {
                return new Segment(SegmentType.Normal, "60%");
            }
        });
    }

    public enum SegmentType {
        Normal, AP, AD, M_DMG, P_DMG, Special, Replace
    }

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
        List<TextVariable> allMatches = new ArrayList<>();

        matcher = Pattern.compile("\\{\\{ [af]\\d \\}\\}").matcher(description);

        while (matcher.find()) {
            TextVariable tv = new TextVariable();
            tv.match = matcher.group();
            tv.startIndex = matcher.start();
            tv.endIndex = matcher.end();

            //check for plus
            if (tv.startIndex - 1 >= 0 && description.charAt(tv.startIndex - 1) == '+') {
                //add + and adjust start index
                tv.startIndex--;
                tv.prefix = "+";
            }

            //check for parantheses
            if ((tv.startIndex - 1 >= 0 && description.charAt(tv.startIndex - 1) == '(')
                    && (tv.endIndex < description.length() && description.charAt(tv.endIndex) == ')')) {
                //adjust indeces
                tv.startIndex--;
                tv.endIndex++;

                //add suffix and prefix
                tv.prefix = "(" + tv.prefix;
                tv.suffix = tv.suffix + ")";
            }
            allMatches.add(tv);
        }

        //No special vars --> we are done
        if (allMatches.size() == 0) {
            addNewSegment(SegmentType.Normal, description);
            return;
        }


        int currentIndex = 0;
        //for each match
        for (TextVariable tv : allMatches) {
            //add text before variable
            if (currentIndex != tv.startIndex)
                addNewSegment(SegmentType.Normal, description.substring(currentIndex, tv.startIndex));
            //add variable text
            addNewSegment(SegmentType.Replace, tv.match);

            currentIndex = tv.endIndex;
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

        for (Segment s : segments) {
            if (s.segmentType == SegmentType.Replace) {

                //get Textvariable
                TextVariable tv = findTV(s.content, allMatches);

                //the tv will always exist because a segment is only created with the text variable
                int keyIndex = tv.match.indexOf('a');
                if (keyIndex == -1)
                    keyIndex = tv.match.indexOf('f');

                //get key for vars array
                String key = tv.match.substring(keyIndex, keyIndex + 2);

                JSONObject var = findVar(key, vars);
                //preliminary check to see which abilities need special cases
                Segment replacement;
                if (var == null) {

                    //add special cases for certain a and f variables that do exist in vars
                    replacement = SpecialCases(abilityKey.toLowerCase(), key, effectBurn, vars);
                    System.out.println("var " + key + " in " + abilityKey + " not found! Replacement: " + replacement.content);
                } else {
                    replacement = new Segment(null, "");
                    //Determine segment type
                    String scalingType;
                    String link = var.getString("link");

                    switch (link) {
                        case "spelldamage":
                            replacement.segmentType = SegmentType.AP;
                            scalingType = "AP";
                            break;
                        case "attackdamage":
                            replacement.segmentType = SegmentType.AD;
                            scalingType = "AD";
                            break;
                        case "bonusattackdamage":
                            replacement.segmentType = SegmentType.AD;
                            scalingType = "Bonus AD";
                            break;
                        default:
                            System.out.println(link);
                            replacement.segmentType = SegmentType.Normal;
                            scalingType = link;
                            break;
                    }

                    //determine replacement string
                    JSONArray coeff = var.getJSONArray("coeff");
                    replacement.content = tv.prefix + "";


                    //standard behaviour for coefficients
                    if (coeff.length() > 0) {
                        replacement.content += coeff.getDouble(0);

                        for (int k = 1; k < coeff.length(); k++) {
                            replacement.content += "/" + coeff.getDouble(k);
                        }
                    }

                    //add scaling to content
                    replacement.content += " " + scalingType;

                    //add suffix
                    replacement.content += tv.suffix;
                }

                replacement = replacement == null ? new Segment(null, null) : replacement;

                s.segmentType = replacement.segmentType == null ? s.segmentType : replacement.segmentType;
                s.content = replacement.content == null ? s.content : replacement.content;
                //TODO highlight physical, magic or true damage
            }
        }
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

    private TextVariable findTV(String match, List<TextVariable> list) {
        for (TextVariable tv : list) {
            if (tv.match.equals(match)) {
                return tv;
            }
        }

        return null;
    }

    private JSONObject findVar(String key, JSONArray vars) {
        for (int j = 0; j < vars.length(); j++) {

            JSONObject var = vars.getJSONObject(j);

            //key found
            if (var.getString("key").equals(key)) {
                return var;
            }
        }
        return null;
    }

    /*TODO think about how to add special cases
    reqs:
        - can add logic that uses effectburn and vars
        - can add just a string
        - checks for special cases from a dictionary
        - adding special cases easily
    */
    private Segment SpecialCases(String abilityKey, String varKey, JSONArray effectBurn, JSONArray vars) {
        String key = abilityKey + varKey;
        if (specialCases.containsKey(key)) {
            return specialCases.get(abilityKey + varKey).resolveString(effectBurn, vars);
        } else {
            return new Segment(null, null);
        }
    }

    private void addNewSegment(SegmentType segmentType, String content) {
        segments.add(new Segment(segmentType, content));
    }


    private class TextVariable {
        String match = "";
        int startIndex;
        int endIndex;
        String prefix = "";
        String suffix = "";
    }
}
