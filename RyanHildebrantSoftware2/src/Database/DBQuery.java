package Database;

import java.sql.ResultSet;
import java.sql.Statement;


public class DBQuery {
    
    private static String QUERY;
    private static Statement STMT;
    private static ResultSet RESULT;
    
    public static void makeQuery(String q) {
        QUERY = q;
        
        try {
            //Create a Statement Object
            Statement stmt = DBConnection.makeConnection().createStatement();
            
            //Determine QUERY execution
            if(QUERY.toLowerCase().startsWith("select")) 
                RESULT = stmt.executeQuery(QUERY);
            
            if (QUERY.toLowerCase().startsWith("delete") || QUERY.toLowerCase().startsWith("insert") || QUERY.toLowerCase().startsWith("update"))
                stmt.executeUpdate(QUERY);    
        }
        catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }
    
    public static ResultSet getResult() {
        return RESULT;
    }
}