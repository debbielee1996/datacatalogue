package sg.gov.csit.datacatalogue.dcms.databaselink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

    public boolean createDatatable(String tableName, List<String> headerList, List<String> headerTypes, List<List<String>> records, String datasetName, boolean dataTableExists) throws SQLException {
        Connection conn = null;
        conn = getConnection();
        String tableString = createTableString(headerList, headerTypes);

        // drop table if it exists (this is for table updates)
        if (dataTableExists) {
            PreparedStatement drop = null;
            drop = conn.prepareStatement("IF OBJECT_ID('"+ datasetName +".dbo."+ tableName +"', 'U') IS NOT NULL DROP TABLE "+ datasetName + ".dbo."+ tableName +"");
            drop.executeUpdate();
        }
        System.out.println("CREATE TABLE "+ datasetName + ".dbo."+ tableName +" (id int NOT NULL IDENTITY(1,1), " + tableString + " )");
        // create table for insertion
        PreparedStatement create = conn.prepareStatement("CREATE TABLE "+ datasetName + ".dbo."+ tableName +" (id int NOT NULL IDENTITY(1,1), " + tableString + " )");
        create.executeUpdate();

        // insert values to table
        boolean insert = insertValuesToTable(tableName, headerList, records, datasetName);
        if (insert) {
            System.out.println("Table creation function completed");
            return true;
        }else{
            return false;
        }

    }

    private boolean insertValuesToTable(String tableName, List<String> headerList, List<List<String>> records, String datasetName) throws SQLException {
        Connection conn = getConnection();
        //create a string of the headers for the preparedStatement - comma separated
        String headerListCommaSeparated = String.join(",", headerList);

        for (int i = 0; i < records.size(); i++) {
            PreparedStatement insert = null;
            List<String> subRecordList = records.get(i);
            // goes through every string to check for single quotes. if yes then escape it
            for (int j=0; j<subRecordList.size(); j++) {
                if (subRecordList.get(j).contains("'")) {
                    String[] splitted = subRecordList.get(j).split("'");
                    String newS = "";
                    for (String split:splitted) {
                        if(split=="'") {
                            newS += "'";
                        }
                        newS += split;
                    }
                    subRecordList.set(j, newS);
                }
            }
            // add double quotes to all strings for insert statement
            String subRecords = String.join("," ,
                    records.get(i)
                    .stream()
                    .map(name -> ("'" + name + "'"))
                    .collect(Collectors.toList())
            );
            insert = conn.prepareStatement("INSERT INTO " + datasetName + ".dbo." + tableName + "(" + headerListCommaSeparated +  ")" + " VALUES" + "(" + subRecords + ")");
            insert.executeUpdate();
        }
        System.out.println("Inserting of values completed");
        return true;

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
