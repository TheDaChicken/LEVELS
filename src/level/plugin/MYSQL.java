package me.thedachicken.Storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MYSQL {

    private Connection connection = null;
    private String host, database, username, password;
    private int port;

    public MYSQL(String host, String database, String username, String password, int port) {
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
        if(connection != null) {
            try {
                return !connection.isClosed();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private PreparedStatement createPrepareStatement(String command) throws SQLException {
        /*
         Creates PrepareStatement.
         @return returns Statement, null if unable to create statement.
         */
        if(!isConnected()) {
            openConnection();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[MYSQL STATEMENT] " + ChatColor.RESET + command);
        return connection.prepareStatement(command);
    }

    public boolean createTable(String table_name, HashMap<String, String> hashMap) {
        /*
         Creates Table in MYSQL Database.
         @return True if no error. False if error.
         */
        synchronized (this) {
            String table_statement = "CREATE TABLE IF NOT EXISTS " + table_name + " (" + hashMap.keySet().stream()
                    .map(key -> key + " " + hashMap.get(key))
                    .collect(Collectors.joining(", ")) + ")";
            try {
                PreparedStatement preparedStatement = createPrepareStatement(table_statement);
                preparedStatement.execute(table_statement);
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public ArrayList<HashMap<String, Object>> getRows(String table_name, List<String> GET) {
        return this.getRowsContains(table_name, null, GET);
    }

    public ArrayList<HashMap<String, Object>> getRowsContains(String table_name, HashMap<String, Object> WHERE, List<String> GET) {
        synchronized (this) {
            String table_statement = "SELECT * from " + table_name;
            if(WHERE != null) {
                table_statement += " where " + WHERE.keySet().stream().map(key -> key + "=" + "?").
                        collect(Collectors.joining(",")) + "";
            }
            try {
                PreparedStatement preparedStatement = createPrepareStatement(table_statement);
                if(WHERE != null) {
                    Object[] valuesArray = WHERE.values().toArray();
                    for(int i=0; i < WHERE.size(); i++){
                        preparedStatement.setObject(i + 1, valuesArray[i]);
                    }
                }
                ArrayList<HashMap<String, Object>> ArrayListHashMap = new ArrayList<>();
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()) {
                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    for(String check : GET) {
                        hashMap2.put(check, rs.getObject(check));
                    }
                    ArrayListHashMap.add(hashMap2);
                }
                preparedStatement.close();
                rs.close();
                return ArrayListHashMap;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public boolean RowExits(String table_name, HashMap<String, String> WHERE) {
        synchronized (this) {
            String table_statement = "SELECT * from " + table_name +
                    " where " + WHERE.keySet().stream().map(key -> key + "=" + "?").
                    collect(Collectors.joining(" AND ")) + "";
            try {
                PreparedStatement preparedStatement = createPrepareStatement(table_statement);
                Object[] valuesArray = WHERE.values().toArray();
                for(int i=0; i < WHERE.size(); i++){
                    preparedStatement.setString(i + 1, (String) valuesArray[i]);
                }
                ResultSet rs = preparedStatement.executeQuery();
                boolean found = rs.next();
                rs.close();
                preparedStatement.close();
                return found;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean insertRow(String table_name, HashMap<String, Object> VALUES) {
        synchronized (this) {
            String table_statement = "INSERT INTO " + table_name +
                    " (" + VALUES.keySet().stream().map(key -> key).collect(Collectors.joining(",")) + ") "
                    + "VALUES (" + VALUES.keySet().stream().map(key -> "?").collect(Collectors.joining(",")) + ");";
            try {
                PreparedStatement preparedStatement = createPrepareStatement(table_statement);
                Object[] valuesArray = VALUES.values().toArray();
                for(int i=0; i < valuesArray.length; i++){
                    preparedStatement.setObject(i + 1, valuesArray[i]);
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean removeRow(String table_name, HashMap<String, Object> WHERE) {
        String table_statement = "DELETE FROM " + table_name +
                " where " + WHERE.keySet().stream().map(key -> key + "=" + "?").
                collect(Collectors.joining(" AND ")) + "";
        try {
            PreparedStatement preparedStatement = createPrepareStatement(table_statement);
            Object[] WHEREArray = WHERE.values().toArray();
            for(int i=0; i < WHEREArray.length; i++){
                preparedStatement.setObject(i + 1, WHEREArray[i]);
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRow(String table_name, HashMap<String, Object> SET, HashMap<String, Object> WHERE) {
        synchronized (this) {
            String table_statement = "UPDATE " + table_name + " SET " + SET.keySet().stream().map(key -> key + "=" + "?").collect(Collectors.joining(",")) +
                    " where " + WHERE.keySet().stream().map(key -> key + "=" + "?").
                    collect(Collectors.joining(" AND ")) + "";
            try {
                PreparedStatement preparedStatement = createPrepareStatement(table_statement);
                Object[] setArray = SET.values().toArray();
                Object[] whereArray = WHERE.values().toArray();
                for(int i=0; i < setArray.length; i++){
                    preparedStatement.setObject(i + 1, setArray[i]);
                }
                for(int i=0; i < whereArray.length; i++){
                    preparedStatement.setObject(i + setArray.length + 1, whereArray[i]);;
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }


}
