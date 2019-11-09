package level.plugin.Exceptions.Player;

public class PlayerNameDoesntExist extends Exception {

    /**
     * Player name doesn't exist.
     */
    private static final long serialVersionUID = 1L;

    public PlayerNameDoesntExist() {
        super("Player name doesn't exist.");
    }

}
