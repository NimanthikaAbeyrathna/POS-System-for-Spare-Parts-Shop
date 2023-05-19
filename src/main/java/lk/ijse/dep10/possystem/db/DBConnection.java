package lk.ijse.dep10.possystem.db;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static DBConnection dbConnection;

    private final Connection connection;

    private DBConnection() {

        Properties properties = new Properties();
        File file = new File("application.properties");
        try {
            FileReader fr = new FileReader(file);
            properties.load(fr);
            fr.close();

            String host = properties.getProperty("parts.host", "dep10.lk");
            String port = properties.getProperty("parts.port", "3306");
            String databaseName = properties.getProperty("parts.name", "spare_parats");
            String root = properties.getProperty("parts.username", "root");
            String password = properties.getProperty("parts.password", "Gaya@12/");
            String query = "?createDatabaseIfNotExist=true&allowMultiQueries=true";
            String initial = "jdbc:mysql://";

            StringBuilder sr = new StringBuilder();
            StringBuilder result = sr.append(initial).append(host).append(":").append(port).append("/").append(databaseName).append(query);
            String sql = result.toString();

            connection = DriverManager.getConnection(sql, root, password);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance() {
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;

    }

    public Connection getConnection() {

        return connection;
    }
}
