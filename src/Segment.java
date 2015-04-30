/**
 * Created with IntelliJ IDEA.
 * User: Jean-Luc
 * Date: 30.04.2015
 * Time: 13:34
 */
public class Segment implements Cloneable {
    public AbilityDescription.SegmentType segmentType;
    public String content;

    public Segment(AbilityDescription.SegmentType segmentType, String content) {
        this.segmentType = segmentType;
        this.content = content;
    }

    public Segment clone() {
        return new Segment(segmentType, content);
    }
}
