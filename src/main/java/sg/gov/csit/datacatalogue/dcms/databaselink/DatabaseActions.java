package sg.gov.csit.datacatalogue.dcms.databaselink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

    public boolean createDatatable(String tableName, List<String> headerList, List<String> headerTypes, List<List<String>> records, String datasetName) {
        Connection conn = null;
        try {
            conn = getConnection();
            String tableString = createTableString(headerList, headerTypes);

            PreparedStatement create = conn.prepareStatement("CREATE TABLE "+ datasetName + ".dbo."+ tableName +" (id int NOT NULL IDENTITY(1,1), " + tableString + " )");
            create.executeUpdate();
            boolean insert = insertValuesToTable(tableName, headerList, records, datasetName);
            //insert table function here
            if (insert) {
                return true;
            }else{
                //deleteTable(tableName);
                //add delete table here
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        } finally {
            System.out.println("Table creation function completed");
        }
    }

    private boolean insertValuesToTable(String tableName, List<String> headerList, List<List<String>> records, String datasetName) {
        try{
            Connection con = getConnection();
            //create a string - comma separated
            String headerListCommaSeparated = String.join(",", headerList);

            //set to 1 to avoid the headers
            for (int i = 0; i < records.size(); i++) {
                PreparedStatement insert = null;
                insert = con.prepareStatement("INSERT INTO " + datasetName + ".dbo." + tableName + "(" + headerListCommaSeparated +  ")" + " VALUES" + "(" + String.join(",", records.get(i)) + ")");

                insert.executeUpdate();
                System.out.println(insert);
            }
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        finally {
            System.out.println("Inserting of values completed");
        }
    }

    //creates table string eg. firstname varchar(200), lastname varchar(200)
    public static String createTableString(List<String> headerList , List<String> headerTypes) {
        // http://www.pellegrino.link/2015/08/22/string-concatenation-with-java-8.html
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < headerList.size(); i++) {
            String s = headerList.get(i) + headerTypes.get(i);
            result.append(s);
            //ensures that we do not add a comma to the last value
            if (i < headerList.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }
}
