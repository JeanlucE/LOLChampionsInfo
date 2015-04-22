import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 22/4/2015
 * Time: 2:09 PM
 */
public class SegmentToTextConverter {
    private Map<AbilityDescription.SegmentType, Font> fontMap;
    private Map<AbilityDescription.SegmentType, Color> fillMap;

    public SegmentToTextConverter(Font normalFont, Color normalColor)
    {
        fontMap = new HashMap<>(7);
        fillMap = new HashMap<>(7);

        addStyle(AbilityDescription.SegmentType.Normal, normalFont, normalColor);
    }

    public void addStyle(AbilityDescription.SegmentType key, Font font, Color color)
    {
        if(fontMap.containsKey(key))
        {
            System.out.println("Warning! Reassigning key " + key.name() + " in fontMap and colorMap");
        }
        fontMap.put(key, font);
        fillMap.put(key, color);
    }

    //TODO find a good way to format text for long ability descriptions
    public Text[] convert(AbilityDescription.Segment[] segments)
    {
        Text[] richText = new Text[segments.length];

        for (int i = 0; i < segments.length; i++) {
            AbilityDescription.Segment segment = segments[i];

            //get Font
            Font f = fontMap.get(segment.segmentType);
            Color c = fillMap.get(segment.segmentType);
            if(f == null || c == null) {
                f = fontMap.get(AbilityDescription.SegmentType.Normal);
                c = fillMap.get(AbilityDescription.SegmentType.Normal);
                System.out.println("Warning! No key assigned to segment type " + segment.segmentType + " in fontMap " +
                        "and fillMap. Using normal font on content: " + segment.content);
            }

            //Set content
            Text t = new Text(segment.content);
            //set font and color
            t.setFont(f);
            t.setFill(c);

            //Better AA
            t.setFontSmoothingType(FontSmoothingType.LCD);

            richText[i] = t;
        }
        return richText;
    }
}
