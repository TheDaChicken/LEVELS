package level.plugin.Leaderboard;

import java.util.Comparator;

public class LeaderboardComparator implements Comparator<LeaderboardScore> {

    public int compare(LeaderboardScore score1, LeaderboardScore score2) {
        int sc1 = score1.level;
        int sc2 = score2.level;

        if (sc1 > sc2){
            return -1;
        }else if (sc1 < sc2){
            return +1;
        }else{
            return 0;
        }
    }


}
