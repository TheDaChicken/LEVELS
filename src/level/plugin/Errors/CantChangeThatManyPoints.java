package level.plugin.Errors;

public class CantChangeThatManyPoints extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	public CantChangeThatManyPoints(){
		super("You can't change the points that much!");
	}
}
