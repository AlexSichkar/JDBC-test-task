package mate.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/taxi_db";
        String userName = "root";
        String password = "1234567";
    try (Connection connection = DriverManager.getConnection(url, userName, password)) {
      return connection;
         } catch (SQLException e) {
            throw new RuntimeException("Can't create connection to db", e);
        }
    }
}
