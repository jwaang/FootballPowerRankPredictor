package app.Model;

import app.PowerRankAI;
import app.View.GuessScoresView;
import app.ServiceProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.ServiceLoader;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



public class GuessScoresModel extends javax.swing.JFrame{
    public static <T> T loadService(Class<T> c){
        T result = null;
	ServiceLoader<T> impl = ServiceLoader.load(c);
	for(T loadedImpl : impl){
            result = loadedImpl;
            if(result != null) break;
        }
	if(result == null) throw new RuntimeException("Cannot find Implementation");
	return result;
    }
    
    public static final ServiceProvider sp = loadService(ServiceProvider.class);
    
    public javax.swing.JButton btnExit;
    public javax.swing.JButton btnGo;
    public javax.swing.JButton btnSubmit;
    public javax.swing.JComboBox<String> cboSeason;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lblSeason;
    public javax.swing.JLabel lblUserPercentage;
    public javax.swing.JLabel lblWeek;
    public javax.swing.JSpinner spnWeek;
    public javax.swing.JTable tblGames;
    
    // ************************************************************************
    // Go button click.
    // ************************************************************************
    public void initialize(){
        // Set column headers.
        Object[] objHeaders = {"Week", "Away Team", "Home Team", "Away Score", "Home Score", "User Guess", "Result"};

        // Get data for selected season and week, and bind table.
        int intWeek = (int) spnWeek.getValue();
        int intSeason = Integer.parseInt(cboSeason.getSelectedItem().toString());
        StringBuilder sbMessage = new StringBuilder();
        Object[][] objModel = LoadData(intSeason, intWeek);
        
        DefaultTableModel objTableModel = new DefaultTableModel(objModel, objHeaders);
        tblGames.setModel(objTableModel);

        // Set columns width.
        tblGames.getColumnModel().getColumn(0).setMinWidth(50);             // Week.
        tblGames.getColumnModel().getColumn(0).setMaxWidth(50);
        tblGames.getColumnModel().getColumn(0).setPreferredWidth(50);

        tblGames.getColumnModel().getColumn(1).setMinWidth(180);            // Opponent team.
        tblGames.getColumnModel().getColumn(1).setMaxWidth(180);
        tblGames.getColumnModel().getColumn(1).setPreferredWidth(180);

        tblGames.getColumnModel().getColumn(2).setMinWidth(180);            // Home team.
        tblGames.getColumnModel().getColumn(2).setMaxWidth(180);
        tblGames.getColumnModel().getColumn(2).setPreferredWidth(180);

        tblGames.getColumnModel().getColumn(3).setMinWidth(0);              // Opponent score.
        tblGames.getColumnModel().getColumn(3).setMaxWidth(0);
        tblGames.getColumnModel().getColumn(3).setPreferredWidth(0);

        tblGames.getColumnModel().getColumn(4).setMinWidth(0);              // Home score.
        tblGames.getColumnModel().getColumn(4).setMaxWidth(0);
        tblGames.getColumnModel().getColumn(4).setPreferredWidth(0);

        tblGames.getColumnModel().getColumn(5).setMinWidth(180);            // User guess.
        tblGames.getColumnModel().getColumn(5).setMaxWidth(180);
        tblGames.getColumnModel().getColumn(5).setPreferredWidth(180);
        
        // Generate combobox controls for user guess column.
        generateCombobox();
        
        tblGames.getColumnModel().getColumn(6).setMinWidth(0);              // Result (Right/Wrong).
        tblGames.getColumnModel().getColumn(6).setMaxWidth(0);
        tblGames.getColumnModel().getColumn(6).setPreferredWidth(0);

        lblUserPercentage.setText("User %: ");
    }
    
    
    // ************************************************************************
    // Submit user guesses and calculate percentage.
    // ************************************************************************
    public void calculatePercentage(){
                // Validate that user selected a winner for all of the games.
        int intLength = tblGames.getRowCount();
        for (int i = 0; i <= intLength - 1; i++)
        {
            if (tblGames.getValueAt(i, 0) != null && tblGames.getValueAt(i, 5) == null)
            {
                JOptionPane.showMessageDialog(this, "You have not selected a winner for all of the games.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Make home team score column visible.
        tblGames.getColumnModel().getColumn(3).setMinWidth(80);          // Opponent score.
        tblGames.getColumnModel().getColumn(3).setMaxWidth(80);
        tblGames.getColumnModel().getColumn(3).setPreferredWidth(80);

        // Make opponent team score column visible.
        tblGames.getColumnModel().getColumn(4).setMinWidth(80);          // Home score.
        tblGames.getColumnModel().getColumn(4).setMaxWidth(80);
        tblGames.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Make result column visible.
        tblGames.getColumnModel().getColumn(6).setMinWidth(120);          // Result (Right guess/Wrong guess).
        tblGames.getColumnModel().getColumn(6).setMaxWidth(120);
        tblGames.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Set Result column according to user answers.
        String strHomeTeam = "";
        String strOpponentTeam = "";
        int intHomeScore = 0;
        int intOppScore = 0;
        int intTotalAnswers = 0;
        int intRightAnswers = 0;
        String strUserAnswer = "";
        String strAux = "";

        for (int i = 0; i <= 16; i++)
        {
            if (tblGames.getValueAt(i, 0) != null)
            {
                intTotalAnswers++;
                strOpponentTeam = (String) tblGames.getValueAt(i,1);
                strHomeTeam = (String) tblGames.getValueAt(i,2);

                strAux = ((String) tblGames.getValueAt(i, 3)).trim();
                intOppScore = Integer.parseInt(strAux);

                strAux = ((String) tblGames.getValueAt(i, 4)).trim();
                intHomeScore = Integer.parseInt(strAux);

                strUserAnswer = (String) tblGames.getValueAt(i,5);

                if (intHomeScore > intOppScore && strUserAnswer == strHomeTeam)
                {
                    tblGames.setValueAt("Right", i, 6);
                    intRightAnswers++;
                }
                else if (intHomeScore < intOppScore && strUserAnswer == strOpponentTeam)
                {
                    tblGames.setValueAt("Right", i, 6);
                    intRightAnswers++;
                }
                else
                    tblGames.setValueAt("Wrong", i, 6);
            }
        }

        double dblUserPercentage = (intRightAnswers * 100) / intTotalAnswers;
        lblUserPercentage.setText("User %: " + Double.toString(dblUserPercentage));
    }
    
    // ************************************************************************
    // Set JComboBox control for column where user will make his/her guess.
    // ************************************************************************
    public void generateCombobox() {
       EachRowEditorModel editor = new EachRowEditorModel(tblGames);
        for(int i = 0; i < tblGames.getRowCount(); i++){
            JComboBox cbb = new JComboBox();
            cbb.addItem(tblGames.getValueAt(i, 1));
            cbb.addItem(tblGames.getValueAt(i, 2));
            editor.setEditorAt(i, new DefaultCellEditor(cbb));
        }
        tblGames.getColumnModel().getColumn(5).setCellEditor(editor);
    }
    
    // ************************************************************************
    // Load table with data of some week.
    // ************************************************************************
    private Object[][] LoadData(int Season, int Week) {

        // Get power rank data for specified season.
        Object[][] objPowerRankData = sp.GetPowerRankData(Season, Week);

        // Array used as model for games in JTable.
        Object[][] objModel = new Object[17][7];

        // Variables definition.
        String strLine;         // Auxiliary variable to read line by line from input file.
        String[] Values;        // Array to save each individual value of a single line read from input file.
        int intRowCount = -1;	// Counter/pointer for input file records.
        //InputStream objFile = this.getClass().getClassLoader().getResourceAsStream("app/" + "NFLStats" + Season + ".db");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Scanner objScanner = new Scanner(classLoader.getResourceAsStream("resources/NFLStats2015.db"));
        while (objScanner.hasNextLine()) {
            // Read a single line and fill array with each individual value.
            strLine = objScanner.nextLine();
            Values = strLine.split(",");
            
            // Fill array with values from input file.
            // Load values of selected week only.
            int intWeek = Integer.parseInt(Values[0]);
            if (intWeek == Week) {
                intRowCount++;
                objModel[intRowCount][0] = Values[0];  // Week number.
                
                if (sp.AI(objPowerRankData, Values))
                {
                    objModel[intRowCount][1] = "<html><u>" + Values[1] + "</u></html>";  // Opponent team.
                    objModel[intRowCount][2] = Values[2];  // Home team.
                }
                else
                {
                    objModel[intRowCount][1] = Values[1];  // Opponent team.
                    objModel[intRowCount][2] = "<html><u>" + Values[2] + "</u></html>"; // Home team.
                }
                
                
                objModel[intRowCount][3] = Values[3];  // Opponent score.
                objModel[intRowCount][4] = Values[4];  // Home score.
            }
        }
        objScanner.close();

        if (intRowCount == -1) {
            JOptionPane.showMessageDialog(this, "No data found for the specified week.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return objModel;
    } 
                                              
    // ************************************************************************
    // Main method.
    // ************************************************************************
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuessScoresView().setVisible(true);
            }
        });
    }
   
}
