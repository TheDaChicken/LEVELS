package level.plugin;

import level.plugin.Exceptions.MYSQL.TableAlreadyExists;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

class MySQL {

    /*
    TODO REWRITE THIS CLASS TO USE PREPARED STATEMENTS
     */

    private Connection connection = null;
    private Statement statement = null;
    private String host, database, username, password;
    private int port;

    MySQL(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    boolean openConnection() {
        /*
         Create connection to the MYSQL Server.
         @return True if connected, false if unable.
         */
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        synchronized (this) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean isConnected() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private Statement getStatement() {
        /*
         Creates Statement.
         @return returns Statement, null if unable to create statement.
         */
        if (this.statement == null) {
            try {
                this.statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.statement;
    }

    boolean createTable(String table_name, HashMap<String, String> hashMap) throws TableAlreadyExists {
        /*
         Creates Table in MYSQL Database.
         @return True if no error. False if error.
         */
        Statement statement = this.getStatement();
        if (statement != null) {
            synchronized (this) {
                String table_statement = "CREATE TABLE " + table_name + " (" + hashMap.keySet().stream()
                        .map(key -> key + " " + hashMap.get(key))
                        .collect(Collectors.joining(", ")) + ");";

                try {
                    statement.execute(table_statement);
                    return true;
                } catch (SQLException e) {
                    int error_code = e.getErrorCode();
                    if (error_code == 1050) {
                        throw (new TableAlreadyExists(table_name));
                    } else {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    boolean createTableIfNotExist(String table_name, HashMap<String, String> hashMap) {
        /*
         Creates Table in MYSQL Database. Ignore if does Exist.
         @return True if no error. False if error.
         */
        Statement statement = this.getStatement();
        if (statement != null) {
            synchronized (this) {
                String table_statement = "CREATE TABLE IF NOT EXISTS " + table_name + " (" + hashMap.keySet().stream()
                        .map(key -> key + " " + hashMap.get(key))
                        .collect(Collectors.joining(", ")) + ");";

                try {
                    statement.execute(table_statement);
                    return true;
                } catch (SQLException e) {
                    int error_code = e.getErrorCode();
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    boolean insertDatabase(String table_name, HashMap<String, String> valuesHashMap) {
        /*
         Inserts into table in database.
         @return True if no error. False if error.
         Hashmap:
             Key -> Name
             Value -> Value
         */
        Statement statement = this.getStatement();
        if (statement != null) {
            synchronized (this) {
                String table_statement = "INSERT " + table_name + " (" + String.join(", ", valuesHashMap.keySet()) + ") VALUES (" + valuesHashMap.values().stream()
                        .map(value -> "\"" + value + "\"")
                        .collect(Collectors.joining(", ")) + ");";
                try {
                    statement.executeUpdate(table_statement);
                    return true;
                } catch (SQLException e) {
                    int error_code = e.getErrorCode();
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    boolean updateDatabase(String table_name, HashMap<String, String> setHashMap, HashMap<String, String> whereHashMap) {
        /*
         Updates into table in database.
         @return True if no error. False if error.
         setHashMap:
             Key -> Name
             Value -> Value
         whereHashMap:
             Key -> Name
             Value -> Value
         */
        Statement statement = this.getStatement();
        if (statement != null) {
            synchronized (this) {
                String table_statement = "UPDATE " + table_name + " SET " + setHashMap.keySet().stream()
                        .map(key -> key + "=\"" + setHashMap.get(key) + "\"")
                        .collect(Collectors.joining(", ")) + " WHERE " + whereHashMap.keySet().stream()
                        .map(key -> key + "=\"" + whereHashMap.get(key) + "\"")
                        .collect(Collectors.joining(", "));
                try {
                    statement.executeUpdate(table_statement);
                    return true;
                } catch (SQLException e) {
                    int error_code = e.getErrorCode();
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }


}
