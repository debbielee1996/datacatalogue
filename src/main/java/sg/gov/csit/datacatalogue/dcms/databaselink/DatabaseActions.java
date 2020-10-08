package sg.gov.csit.datacatalogue.dcms.databaselink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseActions {
    public Connection getConnection() {
        try {
            Connection conn = null;
            Class.forName(GetBean.currentDataBaseDriver);
            conn = DriverManager.getConnection(GetBean.currentDataBaseUrl, GetBean.userName, GetBean.password);

            System.out.println("Connected");
            return conn;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean createDatabase(String dbName) throws SQLException {
        Connection conn = null;
        try {
            // connect to db
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("CREATE DATABASE "+ dbName);
            ps.execute();
            return true;
        } catch (Exception e) {
            if(conn != null) {
                conn.close();
            }
            return false;
        }
    }
}
