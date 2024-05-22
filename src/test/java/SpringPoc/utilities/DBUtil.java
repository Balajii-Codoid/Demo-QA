package SpringPoc.utilities;

import java.sql.*;

import static SpringPoc.utilities.FileUtil.writeLog;
import static SpringPoc.utilities.YamlUtil.getYamlData;

public class DBUtil {

    public static Connection connection = null;


    public static void dbConnection() throws Exception {
        String jdbcUrl = getYamlData("db_connection.jdbc_url");
        String username = getYamlData("db_connection.username");
        String password = getYamlData("db_connection.password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void createTable() throws SQLException  {
        try (Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS REGISTRATION " +
                    "(id INTEGER not NULL AUTO_INCREMENT, " +
                    " first VARCHAR(255), " +
                    " last VARCHAR(255), " +
                    " age INTEGER, " +
                    " PRIMARY KEY (id))";
            stmt.executeUpdate(sql);
            writeLog("Table Created Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void insertDataIntoTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = "INSERT INTO REGISTRATION (id,first, last, age) VALUES " +
                    "(10,'Prakash', 'P', 25), " +
                    "(11,'Yasin', 'M', 30)";
            stmt.executeUpdate(sql);
            writeLog("Inserted the data into the Table");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void retrieveDataFromTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT * FROM REGISTRATION";
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first = resultSet.getString("first");
                String last = resultSet.getString("last");
                int age = resultSet.getInt("age");
                System.out.println("ID: " + id + ", First: " + first + ", Last: " + last + ", Age: " + age);
                writeLog("Retrieve the data from the Table");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}