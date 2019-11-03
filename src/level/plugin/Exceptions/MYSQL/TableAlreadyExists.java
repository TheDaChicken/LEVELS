package level.plugin.Exceptions.MYSQL;

public class TableAlreadyExists extends Exception {

    public TableAlreadyExists(String table_name) {
        super("Table '" + table_name + "' already exists!");
    }

}
