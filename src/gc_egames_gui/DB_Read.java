package gc_egames_gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB_Read
{
    private String dbURL;
    private String usrID;
    private String usrPWD;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private int recordCount;
    private String errorMessage;
    private Object [] [] objDataSet;
    private String [] stringCSVData;
    private int maxCompID;
    
    // constructor
    public DB_Read (String sql, String qryType)
    {
        dbURL = "";
        usrID = "";
        usrPWD = "";
        recordCount = 0;
        errorMessage = "";
        objDataSet = null;
        stringCSVData = null;
        maxCompID = 0;
        
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
            conn = DriverManager.getConnection(dbURL, usrID, usrPWD);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            
            rs = stmt.executeQuery(sql);
            
            if (rs != null)
            {
                rs.beforeFirst();
                rs.last();
                recordCount = rs.getRow();
            }
            
            if (recordCount > 0)
            {
                int counter = 0;
                objDataSet = new Object[recordCount][];
                stringCSVData = new String[recordCount];
                
                maxCompID = 0;
                
                rs.beforeFirst();
                
                while (rs.next())
                {
                    if (qryType.equals("competition"))
                    {
                        Object [] obj = new Object [5];
                        obj[0] = rs.getString("gameName");
                        obj[1] = rs.getString("team1");
                        obj[2] = rs.getInt("team1Points");
                        obj[3] = rs.getString("team2");
                        obj[4] = rs.getInt("team2Points");
                        
                        objDataSet[counter] = obj;
                        counter++;
                        
                       // System.out.println(obj[0] + "," + obj[1] + "," + obj[2] + "," + obj[3] + "," + obj[4]);
                    }
                    
                    else if (qryType.equals("competition"))
                            {
                                stringCSVData[counter] = rs.getString("name")
                                        + "," + rs.getString("contact")
                                        + "," + rs.getString("phone")
                                        + "," + rs.getString("email");
                                        counter++;
                            }
                    
                    else if (qryType.equals("event"))
                    {
                        
                    }
                    
                    else if (qryType.equals("game"))
                    {
                        
                    }
                    
                }
                
                conn.close();
            }
        }
        
        catch (SQLException sqlE)
        {
            errorMessage = sqlE.getMessage();
        }
        
        catch (Exception e)
        {
            errorMessage = e.getMessage();
        }
    }
    
    public int getRecordCount ()
    {
        return recordCount;
    }
    
    public String getErrorMessage ()
    {
        return errorMessage;
    }
    
    public int getMaxCompID ()
    {
        return maxCompID;
    }
    
    public String[] getStringCSVData ()
    {
        return stringCSVData;
    }
    
    public Object[] getObjDataSet ()
    {
        return objDataSet;
    }
    
    public static String formatDateToString(String inputDateString)
    {
        String formattedDateStr = "";
        String day = inputDateString.substring(8, 10);
        String year = inputDateString.substring(0, 4);
        String month = "Jan";
        String monthNbr = inputDateString.substring(5, 7);
        switch(monthNbr)
        {
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
        }
        
        formattedDateStr = day + "-" + month + "-" + year;
        return formattedDateStr;
    }
    
    @Override public String toString()
    {
        return "Database URL = " + dbURL + " USR ID = " + usrID + " USR PWD = " + usrPWD;
    }
}
