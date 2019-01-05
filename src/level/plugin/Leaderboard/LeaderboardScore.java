package level.plugin.Leaderboard;

import java.io.Serializable;

public class LeaderboardScore implements Serializable {
    public int level;
    public String name;

    public LeaderboardScore(String playername, int levelnumber) {
        level = levelnumber;
        name = playername;
    }
}
