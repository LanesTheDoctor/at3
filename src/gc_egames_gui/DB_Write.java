package gc_egames_gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException; 

public class DB_Write
{
    private String dbURL;
    private String usrID;
    private String usrPWD;
    private Connection conn;
    private Statement stmt;
    private String errorMessage;
    
    public DB_Write (String sql)
    {
        dbURL = "";
        usrID = "";
        usrPWD = "";
        errorMessage = "";
        
         try
        {
            BufferedReader br = new BufferedReader(new FileReader("app.config"));
            String line = br.readLine();
            int lineCounter = 1;
            while (line != null)
            {
                switch(lineCounter)
                {
                    case 1:
                        dbURL = line.substring(6, line.length());
                        break;
                    
                    case 2:
                        usrID = line.substring(6, line.length());
                        break;
                        
                    case 3:
                        usrPWD = line.substring(6, line.length());
                        break;
                        
                    default:
                        break;
                }
                
                line = br.readLine();
                
                lineCounter++;
            }
            
            System.out.println("dbURL=" + dbURL);
            System.out.println("usdID=" + usrID);
            System.out.println("usrPWD=" + usrPWD);
            System.out.println(this.toString());
            
        }
         
         catch (IOException ioe)
        {
            System.out.println("Error: External config file not found!");
            errorMessage = ioe.getMessage() + "\n";
        }
        
        catch (Exception e)
        {
            System.out.println("Error: Problem with reading the config file!");
            errorMessage = e.getMessage() + "\n";
        }
         
         try
         {
             conn = java.sql.DriverManager.getConnection(dbURL, usrID, usrPWD);
             stmt = conn.createStatement();
             stmt.executeUpdate(sql);
             conn.close();
         }
         
         catch (SQLException sqle)
         {
             errorMessage += sqle.getMessage()
         }
         
         catch (Exception e)
         {
             errorMessage += e.getMessage();
         }
         
         public String getErrorMessage()
         {
             return errorMessage;
         }
         
         @Override public String toString()
         {
             return "Database URL = " + dbURL + " USR ID = " + usrID + " USR PWD = " + usrPWD;
         }
    }
}
