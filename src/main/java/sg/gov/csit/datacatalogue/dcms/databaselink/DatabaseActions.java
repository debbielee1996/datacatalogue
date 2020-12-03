package sg.gov.csit.datacatalogue.dcms.databaselink;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseActions {
    private static DataSource dataSource;

    public static DataSource getDataSource() { // https://examples.javacodegeeks.com/enterprise-java/hikaricp/hikaricp-connection-pooling-example/
        if(dataSource == null)
        {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(GetBean.currentDataBaseUrl);
            config.setUsername(GetBean.userName);
            config.setPassword(GetBean.password);

            config.setMaximumPoolSize(GetBean.maximumPoolSize);

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public Connection getConnection() {
        try {
            DataSource dataSource = getDataSource();
            Connection conn = dataSource.getConnection();
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
            return false;
        } finally {
            if(conn != null) {
                conn.close();
            }
            System.out.println("Closed connection for creating dataset");
        }
    }

    public boolean createDatatable(String tableName, List<String> headerList, List<String> headerTypes, List<List<String>> records, String datasetName, boolean dataTableExists) throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);

        String tableString = createTableString(headerList, headerTypes);

        try {
            PreparedStatement drop = null;
            // drop table if it exists (this is for table updates)
            if (dataTableExists) {
                drop = conn.prepareStatement("IF OBJECT_ID('"+ datasetName +".dbo."+ tableName +"', 'U') IS NOT NULL DROP TABLE "+ datasetName + ".dbo."+ tableName +"");
                drop.executeUpdate();
            }
            System.out.println("CREATE TABLE "+ datasetName + ".dbo."+ tableName +" (id int NOT NULL IDENTITY(1,1), " + tableString + " )");
            // create table for insertion
            PreparedStatement create = conn.prepareStatement("CREATE TABLE "+ datasetName + ".dbo."+ tableName +" (id int NOT NULL IDENTITY(1,1), " + tableString + " )");
            create.executeUpdate();

            // insert values to table
            boolean insert = insertValuesToTable(tableName, headerList, records, datasetName, headerTypes, conn, dataTableExists, drop, create);
            if (insert) {
                System.out.println("Table creation function completed");
                return true;
            }else{
                return false;
            }
        } finally {
            if(conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
            System.out.println("Closed connection for creating datatable");
        }
    }

    private boolean insertValuesToTable(String tableName, List<String> headerList, List<List<String>> records, String datasetName, List<String> headerTypes, Connection conn, boolean dataTableExists, PreparedStatement drop, PreparedStatement create) throws SQLException {
        //create a string of the headers for the preparedStatement - comma separated
        String headerListCommaSeparated = String.join(",", headerList);

        for (int i = 0; i < records.size(); i++) {
            PreparedStatement insert = null;
            List<String> subRecordList = records.get(i); // every row of data
            String subRecords = String.join("," ,
                    records.get(i)
                    .stream()
                    .map(x -> x.contains("'") ? x.replace("'", "''"): x) // goes through every string to check for single quotes. if yes then escape it
                    .map(name -> ("'" + name + "'")) // add double quotes to all strings for insert statement
                    .collect(Collectors.toList())
            );
            try {
                insert = conn.prepareStatement("INSERT INTO " + datasetName + ".dbo." + tableName + "(" + headerListCommaSeparated +  ")" + " VALUES" + "(" + subRecords + ")");
                insert.executeUpdate();
            } catch (SQLException e) {
                // row number i caused the error
                // iterate each column casting
                int problematicColumnNum = -1;
                String problematicColumnName = "";
                subRecordList= Arrays.asList(subRecords.split(",")); // subRecords has every cell appended with '' and escaped '

                // simulate creation of table for the trial-and-error to detect problematic column
                if (dataTableExists) { drop.executeUpdate(); }
                create.executeUpdate();

                try {
                    for (int j=0; j<headerTypes.size();j++) { // iterate current row and identify the column giving issue
                        problematicColumnNum=j+1;
                        problematicColumnName=headerList.get(j);
                        insert = conn.prepareStatement("INSERT INTO " + datasetName + ".dbo." + tableName + "(" + headerList.get(j) +  ")" + " VALUES" + "(" + subRecordList.get(j) + ")");
                        insert.execute();
                    }
                } catch (SQLException ee) { // do nothing. let main try catch handle
                } finally {
                    if(conn != null) {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                    System.out.println("Closed connection for creating datatable");
                }
                throw new SQLException("row "+(i+2)+ " column "+ problematicColumnNum + " (" + problematicColumnName +") issue: " + e.getMessage(),e);
            }
        }

        if(conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
        }
        System.out.println("Closed connection for creating datatable");
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
