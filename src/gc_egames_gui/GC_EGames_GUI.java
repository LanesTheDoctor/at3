package gc_egames_gui;

import gc_egames_gui.DB_Read;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.table.DefaultTableModel;

public class GC_EGames_GUI extends javax.swing.JFrame {

    private DefaultTableModel compResultsTableModel;
    private DefaultTableModel leaderBoardSelectedEventTableModel;
    private DefaultTableModel leaderBoardAllEventsTableModel;
    
    //database connection
    private DB_Read dbRead;
    private DB_Write dbWrite;
    
    private String chosenEvent;
    private String chosenTeam;
    
    //sql creation string
    private String sql;
    
    //these will store table data
    private String [] teamsCSVStrArray;
    private String [] gamesCSVStrArray;
    private String [] eventsCSVStrArray;
    private String [] objArrayForTableDisplay;
    private boolean [] comboBoxStatus;
    
    public GC_EGames_GUI() 
    {
        
        String [] columnNames_CompResults = new String[] {"Game", "Team 1", "Pt", "Team 2", "Pt"};
        compResultsTableModel = new DefaultTableModel();
        compResultsTableModel.setColumnIdentifiers(columnNames_CompResults);
        
        
        String [] columnNames_ChosenEventLeaderBoard = new String[] {"Team", "Total points - chosen event"};
        leaderBoardSelectedEventTableModel = new DefaultTableModel();
        leaderBoardSelectedEventTableModel.setColumnIdentifiers(columnNames_ChosenEventLeaderBoard);
        
        String [] columnNames_AllEventLeaderBoard = new String[] {"Team", "Total points - all event"};
        leaderBoardSelectedEventTableModel = new DefaultTableModel();
        leaderBoardSelectedEventTableModel.setColumnIdentifiers(columnNames_AllEventLeaderBoard);
        
        dbRead = null;
        dbWrite = null;
        
        chosenEvent = "All events";
        chosenTeam = "All teams";
        sql = "";
        
        teamsCSVStrArray = null;
        gamesCSVStrArray = null;
        eventsCSVStrArray = null;
        objArrayForTableDisplay = null;
        
        comboBoxStatus = false;
        
        initComponents();
        
        /*
        sql = " SELCT name, contact, phone, email FROM team ORDER BY name";
        dbRead = new DB_Read(sql, "team");
        if (dbRead.getErrorMessage().length() > 0)
        {
            System.out.println(dbRead.getErrorMessage());
        }
        
        if (dbRead.getRecordCount() > 0)
        {
            for (int i =0; i < dbRead.getStringCSVData().length; i++)
            {
                System.out.println(dbRead.getStringCSVData()[i]);
            }
        }
        */
        
      //  sql = "SELECT competitionID, eventName, gameName, team1, team2, team1Points, team2Points FROM competition";
       // dbRead = new DB_Read(sql, "competitions");
        
        //objArrayForTableDisplay = dbRead.getObjDataSet();
        
        resizeTableColumnsForCompResults();
        
        resizeTableColumnsForSelectedEventsLeaderBoard();
        
        resizeTableColumnsForAllEventsLeaderBoard();
        
        displayCompResults();
        
        displayEventListing();
        
        displayTeamListing();
        
        displayGameListing();
        
        displayTeamData();
        
        displayAllEventsLeaderBoard();
    
    
    //Setting up the local date
    LocalDate dateObj = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
    String todaysDate = dateObj.format(formatter);
    newEventDate_jTextField.setText(todaysDate);
    newEventLocation_jTextField.setText("TAFE Coomera");
    
    comboBoxStatus = true;
 
    }
    
    private void resizeTableColumnsForCompResults()
    {
        float[] columnWidthPercentage = {0.3f, 0.3f, 0.05f, 0.3f, 0.05f};
        
        int tW = compResults_JTable.getWidth();
        javax.swing.table.TableColumn column;
        javax.swing.table.TableColumnModel jTableColumnModel = compResults_JTable.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++)
        {
            column = jTableColumnModel.getColumn(i);
            int pWdith = Math.round(columnWidthPercentage[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }
    
     private void resizeTableColumnsForSelectedEventsLeaderBoard()
    {
        float[] columnWidthPercentage = {0.4f, 0.6f};
        
        int tW = selectedEventLeaderboard_jTable.getWidth();
        javax.swing.table.TableColumn column;
        javax.swing.table.TableColumnModel jTableColumnModel = selectedEventLeaderboard_jTable.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++)
        {
            column = jTableColumnModel.getColumn(i);
            int pWdith = Math.round(columnWidthPercentage[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }
    
    
     private void resizeTableColumnsForAllEventsLeaderBoard()
    {
        float[] columnWidthPercentage = {0.4f, 0.6f};
        
        int tW = allEventsLeaderboard_jTable.getWidth();
        javax.swing.table.TableColumn column;
        javax.swing.table.TableColumnModel jTableColumnModel = allEventsLeaderboard_jTable.getColumnModel();
        int cantCols = jTableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++)
        {
            column = jTableColumnModel.getColumn(i);
            int pWdith = Math.round(columnWidthPercentage[i] * tW);
            column.setPreferredWidth(pWidth);
        }
    }
     
     private void displayCompResults()
     {
         sql = "SELECT gameName, team1, team1Points, team2, team2Points "
                 + "FROM goldcoast_esports.competition";
         
         if (! chosenEvent.equals("All events"))
         {
             sql += " WHERE eventName = '" + chosenEvent + "'";
             
             if (! chosenTeam.equals("All teams"))
             {
                 sql += " AND (team1 = '" + chosenTeam + "' OR team2 = '" + chosenTeam + "')";
             }
         }
         
         else
         {
             if (! chosenTeam.equals("All teams"))
             {
                 sql += " WHERE (team1 = '" + chosenTeam + "'OR team2 '" + chosenTeam + "')";
             }
         }
         
         //testing
         System.out.println("SQL used for competition display: " + sql);
         dbRead = new DB_Read(sql, "competition");
         
         if (dbRead.getErrorMessage().isEmpty() == false)
         {
             System.out.println("ERROR: " + dbRead.getErrorMessage());
             return;
         }
         
         System.out.println("Number of competition results from SQL: " + dbRecordCount());
         
         if (dbRead.getRecordCount() > 0)
         {
             if (compResultsTableModel.getRowCount() > 0)
             {
                for (int i = compResultsTableModel.getRowCount() - 1; i > - 1; i--)
                {
                    compResultsTableModel.removeRow(i);
                }
             
             }
         
            if (dbRead.getObjDataSet() != null)
            {
                for (int row = 0; row < dbRead.getObjDataSet().length; row++)
                {
                    compResultsTableModel.addRow(dbRead.getObjDataSet()[row]);
                }
                compResultsTableModel.fireTableDataChanged();
            }
         }
     
         else
         {
             if (compResultsTableModel.getRowCount() > 0)
             {
                 for (int i = compResultsTableModel.getRowCount() - 1; i> -1; i--)
                 {
                     compResultsTableModel.removeRow(i);
                 }
             }
         }
         
         nbrRecordsFound_JTextField.setText(dbRead.getRecordCount() + "records found");
     }
    
     private void displayTeamListing()
     {
         sql = "SELECT name, contact, phone, email FROM team ORDER BY name";
         dbRead = new DB_Read(sql, "team");
         if (dbRead.getErrorMessage().length() > 0)
         {
             System.out.println(dbRead.getErrorMessage());
             return;
         }
         
         if (dbRead.getRecordCount() > 0)
         {
          /*   for (int i = 0; i < dbRead.getStringCSVData().length; i++)
             {
                 System.out.println(dbRead.getStringCSVData()[i]);
           */  }

           teamsCSVStrArray = dbRead.getStringCSVData();

           teamCompResults_jComboBox.removeAllItems();
           teamTab2_jComboBox.removeAllItems();
           secondTeamTab2_jComboBox.removeAllItems();
           teamName_jComboBox.removeAllItems();

           teamCompResults_jComboBox.addItem("All teams");

           for (int i = 0; i < teamsCSVStrArray.length; i++)
           {
               String[] splitTeamStr = teamsCSVStrArray[i].split(",");
               teamCompResults_jComboBox.addItem(splitTeamStr[0]);
               teamTab2_jComboBox.addItem(splitTeamStr[0]);
               secondTeamTab2_jComboBox.addItem(splitTeamStr[0]);
               teamName_jComboBox.addItem(splitTeamStr[0]);
               
               if (i ==0)
               {
                   contactNameExistingTeam_jTextField.setText(splitTeamStr[1]);
                   phoneNumberExistingTeam_jTextField.setText(splitTeamStr[2]);
                   emailAddressExistingTeam_jTextField.setText(splitTeamStr[3]);
               }
         }
     }
    //chatgpt assisted
     private void displayEventListing() {
    if (comboBoxStatus) {
        // Create an instance of the DB_Read class
        DB_Read dbRead = new DB_Read();

        // Call the method to get the event data; adjust method name as necessary
        List<Event> events = dbRead.getEvents(); // Assuming this method returns a list of events

        // Update your JComboBox with the retrieved events
        eventResults_jComboBox.removeAllItems(); // Clear existing items
        for (Event event : events) {
            eventResults_jComboBox.addItem(event.getName()); // Adjust based on your Event class
        }

        // Optionally, set the selected index if needed
        int selectedIndex_ = eventResults_jComboBox.getSelectedIndex();
        // Do something with selectedIndex_ if needed
    }
}

     
     private void displayGameListing() {
    // Create an instance of the DB_Read class
    DB_Read dbRead = new DB_Read();

    // Retrieve the game data; adjust the method name and return type as necessary
    List<Game> games = dbRead.getGames(); // Assuming this method returns a list of games

    // Create a model for the JTable and populate it
    DefaultTableModel tableModel = new DefaultTableModel();
    
    // Define column names (adjust according to your data structure)
    tableModel.addColumn("Game Name");
    tableModel.addColumn("Date");
    tableModel.addColumn("Location");

    // Populate the model with game data
    for (Game game : games) {
        Object[] rowData = {
            game.getName(),          // Adjust based on your Game class
            game.getDate(),
            game.getLocation()
        };
        tableModel.addRow(rowData);
    }

    // Set the model to the JTable
    compResults_JTable.setModel(tableModel);
}

     
     private void displayTeamData()
     {
         if (comboBoxStatus)
         {
            int selectedIndex_UpdateTeam = teamName_jComboBox.getSelectedIndex();
            String[] splitTeamStr = teamsCSVStrArray(selectedIndex_UpdateTeam).split(",");
            contactNameExistingTeam_jTextField.setText(splitTeamStr[1]);
            phoneNumberExistingTeam_jTextField.setText(splitTeamStr[2]);
            emailAddressExistingTeam_jTextField.setText(splitTeamStr[3]);
        }
     }
     
     private void displayAllEventsLeaderBoard()
     {
         
    DB_Read dbRead = new DB_Read();

    // Retrieve the game data; adjust the method name and return type as necessary
    List<Game> games = dbRead.getGames(); // Assuming this method returns a list of games

    // Create a model for the JTable and populate it
    DefaultTableModel tableModel = new DefaultTableModel();
    
    // Define column names (adjust according to your data structure)
    tableModel.addColumn("Game Name");
    tableModel.addColumn("Date");
    tableModel.addColumn("Location");

    // Populate the model with game data
    for (Game game : games) {
        Object[] rowData = {
            game.getName(),          // Adjust based on your Game class
            game.getDate(),
            game.getLocation()
        };
        tableModel.addRow(rowData);
    }

    // Set the model to the JTable
    allEventLeaderboard_jTable.setModel(tableModel);
     }
     
     private void displaySelectedEventLeaderBoard() {
    DB_Read dbRead = new DB_Read();
    
    // Get the selected event and team from the combo boxes
    String selectedEvent = (String) eventResults_jComboBox.getSelectedItem();
    String selectedTeam = (String) teamCompResults_jComboBox.getSelectedItem();

    // Retrieve game data based on selected event and team; adjust method names as necessary
    List<Game> games = dbRead.getGamesByEventAndTeam(selectedEvent, selectedTeam); // Implement this method in DB_Read

    // Create a model for the JTable and populate it
    DefaultTableModel tableModel = new DefaultTableModel();

    // Define column names (adjust according to your data structure)
    tableModel.addColumn("Game Name");
    tableModel.addColumn("Date");
    tableModel.addColumn("Location");

    // Populate the model with game data
    for (Game game : games) {
        Object[] rowData = {
            game.getName(),          // Adjust based on your Game class
            game.getDate(),
            game.getLocation()
        };
        tableModel.addRow(rowData);
    }

    // Set the model to the JTable
    compResults_JTable.setModel(tableModel);
}

    // Set the model to the JTable
    selectedEventLeaderboard_jTable.setModel(tableModel);
     }
     }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        header_JPanel = new javax.swing.JPanel();
        headerimg_JLabel = new javax.swing.JLabel();
        body_JPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        team_jLabel = new javax.swing.JLabel();
        event_jLabel = new javax.swing.JLabel();
        compresults_jLabel = new javax.swing.JLabel();
        eventLeaderboards_JLablel = new javax.swing.JLabel();
        teamCompResults_jComboBox = new javax.swing.JComboBox<>();
        eventResults_jComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        allEventsLeaderboard_jTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        compResults_JTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        selectedEventLeaderboard_jTable = new javax.swing.JTable();
        nbrRecordsFound_JTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        teamTab2_jComboBox = new javax.swing.JComboBox<>();
        eventTab2_jComboBox = new javax.swing.JComboBox<>();
        gameTab2_jComboBox = new javax.swing.JComboBox<>();
        secondTeamTab2_jComboBox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        newTeamName_jComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        contactName_jComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        phoneNumber__jComboBox = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        emailAddress__jComboBox = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        addNewTeam_jButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        teamName_jComboBox = new javax.swing.JComboBox<>();
        updateExistingTeam_jButton1 = new javax.swing.JButton();
        phoneNumberExistingTeam_jTextField = new javax.swing.JTextField();
        contactNameExistingTeam_jTextField = new javax.swing.JTextField();
        emailAddressExistingTeam_jTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        newEventName_jTextField = new javax.swing.JTextField();
        newEventDate_jTextField = new javax.swing.JTextField();
        newEventLocation_jTextField = new javax.swing.JTextField();
        addNewEvent_jButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gold Coast E-Sports");

        header_JPanel.setBackground(new java.awt.Color(255, 255, 255));
        header_JPanel.setInheritsPopupMenu(true);

        headerimg_JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/goldcoast_esports_v2.jpg"))); // NOI18N

        javax.swing.GroupLayout header_JPanelLayout = new javax.swing.GroupLayout(header_JPanel);
        header_JPanel.setLayout(header_JPanelLayout);
        header_JPanelLayout.setHorizontalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerimg_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        header_JPanelLayout.setVerticalGroup(
            header_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerimg_JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        body_JPanel.setBackground(new java.awt.Color(255, 255, 255));
        body_JPanel.setPreferredSize(new java.awt.Dimension(800, 478));

        javax.swing.GroupLayout body_JPanelLayout = new javax.swing.GroupLayout(body_JPanel);
        body_JPanel.setLayout(body_JPanelLayout);
        body_JPanelLayout.setHorizontalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        body_JPanelLayout.setVerticalGroup(
            body_JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jTabbedPane1.setName(""); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        team_jLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        team_jLabel.setText("Team");

        event_jLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        event_jLabel.setText("Event");

        compresults_jLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        compresults_jLabel.setText("Competition Results");

        eventLeaderboards_JLablel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        eventLeaderboards_JLablel.setText("Event Leaderboards");

        teamCompResults_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        teamCompResults_jComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                teamCompResults_jComboBoxItemStateChanged(evt);
            }
        });

        eventResults_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        eventResults_jComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                eventResults_jComboBoxItemStateChanged(evt);
            }
        });

        allEventsLeaderboard_jTable.setModel(leaderBoardAllEventsTableModel);
        jScrollPane1.setViewportView(allEventsLeaderboard_jTable);

        compResults_JTable.setModel(compResultsTableModel);
        jScrollPane2.setViewportView(compResults_JTable);

        selectedEventLeaderboard_jTable.setModel(leaderBoardSelectedEventTableModel);
        jScrollPane3.setViewportView(selectedEventLeaderboard_jTable);

        nbrRecordsFound_JTextField.setText("jTextField1");
        nbrRecordsFound_JTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nbrRecordsFound_JTextFieldActionPerformed(evt);
            }
        });

        jButton1.setText("Export Leaderboards as .CSV file");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setLabel("Export competition results as .CSV file");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(eventResults_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(team_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(teamCompResults_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(compresults_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(nbrRecordsFound_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61)
                                .addComponent(jButton2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventLeaderboards_JLablel, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addGap(73, 73, 73))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addComponent(event_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(635, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(eventResults_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(team_jLabel)
                            .addComponent(teamCompResults_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(compresults_jLabel)
                    .addComponent(eventLeaderboards_JLablel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(70, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nbrRecordsFound_JTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addGap(23, 23, 23))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(event_jLabel)
                    .addContainerGap(376, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Event competition results", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Event:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Game:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Team 1:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Team 2:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Team 2 Points:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Team 1 Points:");

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField1");

        teamTab2_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        eventTab2_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        gameTab2_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        secondTeamTab2_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(gameTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(eventTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(teamTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(secondTeamTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(54, 54, 54))))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(524, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(129, 129, 129)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(eventTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(gameTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(teamTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(secondTeamTab2_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(225, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(154, 154, 154)
                    .addComponent(jLabel6)
                    .addContainerGap(268, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Add new competition result", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        newTeamName_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("New Team Name:");

        contactName_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Contact Name:");

        phoneNumber__jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Phone Number:");

        emailAddress__jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Email Address:");

        addNewTeam_jButton.setText("Add new team");
        addNewTeam_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewTeam_jButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(499, Short.MAX_VALUE)
                .addComponent(addNewTeam_jButton)
                .addGap(188, 188, 188))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(287, 287, 287)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(contactName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(newTeamName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(phoneNumber__jComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emailAddress__jComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(193, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(161, 161, 161)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(addNewTeam_jButton)
                .addContainerGap(127, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(158, 158, 158)
                    .addComponent(newTeamName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(contactName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(phoneNumber__jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(emailAddress__jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(165, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Add new team", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Team Name:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Contact Name:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Phone Number:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Email Address:");

        teamName_jComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        teamName_jComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                teamName_jComboBoxItemStateChanged(evt);
            }
        });

        updateExistingTeam_jButton1.setText("Update existing team");
        updateExistingTeam_jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateExistingTeam_jButton1ActionPerformed(evt);
            }
        });

        phoneNumberExistingTeam_jTextField.setText("jTextField3");

        contactNameExistingTeam_jTextField.setText("jTextField3");

        emailAddressExistingTeam_jTextField.setText("jTextField3");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateExistingTeam_jButton1)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(34, 34, 34)
                            .addComponent(teamName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(46, 46, 46)
                            .addComponent(emailAddressExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(contactNameExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(phoneNumberExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(255, 255, 255))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teamName_jComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(phoneNumberExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(contactNameExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(emailAddressExistingTeam_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(updateExistingTeam_jButton1)
                .addGap(53, 53, 53))
        );

        jTabbedPane1.addTab("Update existing team", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Location:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Date:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("New event name:");

        newEventName_jTextField.setText("jTextField3");

        newEventDate_jTextField.setText("jTextField3");

        newEventLocation_jTextField.setText("jTextField3");

        addNewEvent_jButton.setText("Update existing team");
        addNewEvent_jButton.setActionCommand("Add new event");
        addNewEvent_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewEvent_jButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 794, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(136, 136, 136)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(addNewEvent_jButton)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46)
                                .addComponent(newEventLocation_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(newEventDate_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(newEventName_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(137, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(101, 101, 101)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(newEventName_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(newEventDate_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(newEventLocation_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                    .addComponent(addNewEvent_jButton)
                    .addGap(101, 101, 101)))
        );

        jTabbedPane1.addTab("Add new event", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1))
                    .addComponent(header_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(body_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header_JPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(body_JPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addContainerGap())))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Event competition results");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nbrRecordsFound_JTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nbrRecordsFound_JTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nbrRecordsFound_JTextFieldActionPerformed

    private void eventResults_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_eventResults_jComboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_eventResults_jComboBoxItemStateChanged

    private void teamCompResults_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_teamCompResults_jComboBoxItemStateChanged

    if (comboBoxStatus)
    {
        chosenTeam = teamCompResults_jComboBox.getSelectedItem().toString();
        displayCompResults();
    }
    }//GEN-LAST:event_teamCompResults_jComboBoxItemStateChanged

    private void teamName_jComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_teamName_jComboBoxItemStateChanged
    
    if (comboBoxStatus)
    {
        displayTeamData();
    }
        
    }//GEN-LAST:event_teamName_jComboBoxItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void addNewTeam_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewTeam_jButtonActionPerformed
        
        String newTeamName = newTeamName_jComboBox.getText();
        String newContactPerson = contactName_jComboBox.getText();
        String newContactPhone = phoneNumber__jComboBox.getText();
        String newContactEmail = emailAddress__jComboBox.getText();
        
        boolean errorStatus = false;
        String errorMessage = "ERROR(S) DETECTED: \n";
        
        if (newTeamName.isEmpty())
        {
            errorStatus = true;
            errorMessage += "Team name is required! \n";
        }
        
        else
        {
            for (int i=0; i < teamsCSVStrArray.length; i++)
            {
                String [] splitTeamsStr = teamsCSVStrArray[i].split(",");
                if (newTeamName.equals(splitTeamsStr[0]))
                {
                    errorStatus = true;
                    errorMessage += " Team name already exists and must be unique. \n";
                    break;
                }
            }
        }
        
        if (newContactPerson.isEmpty())
        {
            errorStatus = true;
            errorMessage += "Contact person is required! \n";
        }
        
        if (newContactPhone.isEmpty())
        {
            errorStatus = true;
            errorMessage += "Contact phone is required! \n";
        }
        
        if (newContactEmail.isEmpty())
        {
            errorStatus = true;
            errorMessage += "Contact email is required! \n";
        }
        
        if (errorStatus == true)
        {
            javax.swing.JOptionPane.showMessageDialog(null, errorMessage, "ERRORS DETECTED!", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int yesOrNo = javax.swing.JOptionPane.showConfirmDialog(null, "You're about to save a new team for: " + newTeamName + "\nDo you wish to continue?", "ADD NEW TEAM");
       
        if (yesOrNo == javax.swingJOptionPane.No_Option)
        {
            System.out.println("ADD NEW TEAM: " + newTeamName + " cancelled.");
        }
        
        else
        {
            System.out.println("ADD NEW TEAM: " + newTeamName + " proceeding!");
            
            sql = "INSERT INTO[team (name, contact, phone, email) VALUES ('" + newTeamName + "', '" + newContactPerson + "', '" + newContactPhone + "', '" + newContactEmail + "')";
            System.out.println(sql);
            dbWrite = new DB_Write(sql);
            
            if (dbWrite.getErrorMessage().equals(""))
            {
                System.out.println("Successful write operation to database");
                ArrayList<String> arraylistTeams = new ArrayList<String>(Arrays.asList(teamsCSVStrArray));
                String newTeamStr = newTeamName + "," + newContactPerson + "," + newContactPhone + "," + newContactEmail);
                arrayListTeams.add(newTeamStr);
                teamsCSVStrArray = arrayListTeams.toArray(new String[arrayListTeams.size()]);
                
                newTeamName_jComboBox.addItem(newTeamName);
                contactName_jComboBox.addItem(newTeamName);
                phoneNumber__jComboBox.addItem(newTeamName);
                emailAddress__jComboBox.addItem(newTeamName);
                
                if (eventResults_jComboBox.getSelectedItem().toString().equals("All events"))
                {
                    displayAllEventsLeaderBoard();
                    
                    if (leaderBoardSelectedEventTableModel.getRowCount() > 0)
                    {
                        for (int i = leaderBoardSelectedEventTableModel.getRowCount() - 1; i > -1; i--)
                        {
                            leaderBoardSelectedEventTableModel.removeRow(i);
                        }
                    }
                }
                
                else
                {
                    displaySelectedEventLeaderBoard();
                    
                    if (leaderBoardAllEventsTableModel.getRowCount() > 0)
                    {
                        for (int i = leaderBoardAllEventsTableModel.getRowCount() - 1; 9 > -11; i--)
                        {
                            leaderBoardAllEventsTableModel.removeRow(i);
                        }
                    }
                }
            }
            
            else
            {
                System.out.println(dbWrite.getErrorMessage());
            }
        }
        
    }//GEN-LAST:event_addNewTeam_jButtonActionPerformed

    private void updateExistingTeam_jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateExistingTeam_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateExistingTeam_jButton1ActionPerformed

    private void addNewEvent_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewEvent_jButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addNewEvent_jButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GC_EGames_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GC_EGames_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GC_EGames_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GC_EGames_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GC_EGames_GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewEvent_jButton;
    private javax.swing.JButton addNewTeam_jButton;
    private javax.swing.JTable allEventsLeaderboard_jTable;
    private javax.swing.JPanel body_JPanel;
    private javax.swing.JTable compResults_JTable;
    private javax.swing.JLabel compresults_jLabel;
    private javax.swing.JTextField contactNameExistingTeam_jTextField;
    private javax.swing.JComboBox<String> contactName_jComboBox;
    private javax.swing.JTextField emailAddressExistingTeam_jTextField;
    private javax.swing.JComboBox<String> emailAddress__jComboBox;
    private javax.swing.JLabel eventLeaderboards_JLablel;
    private javax.swing.JComboBox<String> eventResults_jComboBox;
    private javax.swing.JComboBox<String> eventTab2_jComboBox;
    private javax.swing.JLabel event_jLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> gameTab2_jComboBox;
    private javax.swing.JPanel header_JPanel;
    private javax.swing.JLabel headerimg_JLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField nbrRecordsFound_JTextField;
    private javax.swing.JTextField newEventDate_jTextField;
    private javax.swing.JTextField newEventLocation_jTextField;
    private javax.swing.JTextField newEventName_jTextField;
    private javax.swing.JComboBox<String> newTeamName_jComboBox;
    private javax.swing.JTextField phoneNumberExistingTeam_jTextField;
    private javax.swing.JComboBox<String> phoneNumber__jComboBox;
    private javax.swing.JComboBox<String> secondTeamTab2_jComboBox;
    private javax.swing.JTable selectedEventLeaderboard_jTable;
    private javax.swing.JComboBox<String> teamCompResults_jComboBox;
    private javax.swing.JComboBox<String> teamName_jComboBox;
    private javax.swing.JComboBox<String> teamTab2_jComboBox;
    private javax.swing.JLabel team_jLabel;
    private javax.swing.JButton updateExistingTeam_jButton1;
    // End of variables declaration//GEN-END:variables
}
