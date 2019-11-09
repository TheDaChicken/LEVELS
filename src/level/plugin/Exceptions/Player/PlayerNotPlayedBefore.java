package level.plugin.Exceptions.Player;

public class PlayerNotPlayedBefore extends Exception {

    /**
     * Player has not joined on the server before.
     */
    private static final long serialVersionUID = 1L;

    public PlayerNotPlayedBefore() {
        super("Player has not played on the server before.");
    }

}
