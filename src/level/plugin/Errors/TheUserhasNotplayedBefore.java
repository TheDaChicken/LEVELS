package level.plugin.Errors;

public class TheUserhasNotplayedBefore extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6252896912137108858L;
	public TheUserhasNotplayedBefore() {
		super("This User has not played before on this server!");
	}
}
