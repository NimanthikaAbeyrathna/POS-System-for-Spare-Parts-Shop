package lk.ijse.dep10.possystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static DbConnection dbConnection;
    private final Connection connection;

    private DbConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://dep10.lk:3306/spare_parats","root","Gaya/123&1994");
        } catch (SQLException e) {
            System.out.println("fail to obtain connection");
            System.exit(1);
            throw new RuntimeException(e);
        }

    }

    public Connection getConnection(){
        return connection;
    }

    public static DbConnection getInstance(){
        return (dbConnection==null)?dbConnection=new DbConnection():dbConnection;
    }

}
