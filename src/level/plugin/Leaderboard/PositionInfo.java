package level.plugin.Leaderboard;

public class PositionInfo {
    public int position = 0;
    public String username;
    public int level;
    public PositionInfo(String username, int position, int level) {
        this.username = username;
        this.position = position;
        this.level = level;
    }
}
