package level.plugin.Exceptions.Player;

public class PlayerNotOnline extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PlayerNotOnline() {
        super("Requires Player to be online.");
    }

}
