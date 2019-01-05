package level.plugin.Errors;

public class MaxLevel extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	public MaxLevel(){
		super("That Number is higher than the max Level!");
	}
}
