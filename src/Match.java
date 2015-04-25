import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 25/2/2015
 * Time: 11:08 PM
 */
public class Match {
    private ArrayList<Participant> team1 = new ArrayList<>(5);
    private ArrayList<Participant> team2 = new ArrayList<>(5);

    public static Match getCurrentMatch(Summoner s)
    {
        JSONObject matchJSON = RiotAPI.getInstance().getCurrentGame(s.getID());

        Match match = new Match();
        JSONArray allParticipants = matchJSON.getJSONArray("participants");
        for (int i = 0; i < allParticipants.length(); i++)
        {
            JSONObject participant = allParticipants.getJSONObject(i);

            String name = participant.getString("summonerName");
            long championID = participant.getLong("championId");

            Summoner summoner = Summoner.getSummoner(name);
            Champion champion = null;
            try {
                champion = ChampionsMap.getInstance().getChampionInfoByID(championID);
            } catch (ChampionException e) {
                e.printStackTrace();
            }

            Participant p = new Participant(summoner, champion);
            //Blue side
            if(participant.getInt("teamId") == 100)
            {
                match.team1.add(p);
            }
            //Red side
            else if(participant.getInt("teamId") == 200)
            {
                match.team2.add(p);
            }
        }

        return match;
    }

    private Match()
    {

    }

    public static class Participant
    {
        private Summoner summoner;
        private Champion champion;

        public Participant(Summoner summoner, Champion champion)
        {
            this.summoner = summoner;
            this.champion = champion;
        }

        public Summoner getSummoner() {
            return summoner;
        }

        public Champion getChampion() {
            return champion;
        }

        @Override
        public String toString() {
            return summoner.getName() + ": " + champion.getName();
        }
    }

    public void printInfo()
    {
        System.out.println("Match Info:");

        System.out.println("Blue Side:");
        for (Participant p : team1) {
            System.out.println(p);
        }

        System.out.println("Red Side:");
        for (Participant p : team2) {
            System.out.println(p);
        }
    }
}
