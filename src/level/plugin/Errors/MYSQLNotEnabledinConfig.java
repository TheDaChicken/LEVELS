package level.plugin.Errors;

public class MYSQLNotEnabledinConfig extends Exception {
    public MYSQLNotEnabledinConfig() {
        super("MYSQL not enabled in the config!");
    }
}
