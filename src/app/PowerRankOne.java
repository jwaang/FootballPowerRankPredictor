package app;

import app.Model.GuessScoresModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class PowerRankOne extends GuessScoresModel implements ServiceProvider {
    public Object[][] GetPowerRankData(int Season, int Week){
        Object[][] objRankData = new Object[32][2];

        // Variables definition.
        String strLine;         // Auxiliary variable to read line by line from input file.
        String[] Values;        // Array to save each individual value of a single line read from input file.
        int intRowCount = -1;	// Counter/pointer for input file records.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream objFile = this.getClass().getClassLoader().getResourceAsStream("app/" + "PowerRank" + Season + ".db");
        Scanner objScanner = new Scanner(classLoader.getResourceAsStream("resources/" + "PowerRank" + Season + ".db"));
        while (objScanner.hasNextLine()) {
            // Read a single line and fill array with each individual value.
            strLine = objScanner.nextLine();
            Values = strLine.split("\t");
            
            // Fill array with values from input file.
            intRowCount++;
            objRankData[intRowCount][0] = Values[0].replace('"', ' ').trim();  // Team name.
            objRankData[intRowCount][1] = Double.parseDouble(Values[1]) + Double.parseDouble(Values[2]);  // Power Rank.
        }

        // No power rank data found for specified season.
        if (intRowCount == -1) {
            JOptionPane.showMessageDialog(this, "No power rank data found for the specified season.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return objRankData;
    }
    
    // ************************************************************************
    // Determine which team has higher power rank.
    // ************************************************************************
    public boolean AI(Object[][] PowerRank, String[] Values)
    {
        double dblPowerRankOpp = 0;
        double dblPowerRankHome = 0;

        for (int i = 0; i <= 31; i++)
        {
            if (PowerRank[i][0].equals(Values[1]))
            {
                dblPowerRankOpp = Double.parseDouble(PowerRank[i][1].toString());
            }

            if (PowerRank[i][0].equals(Values[2]))
            {
                dblPowerRankHome = Double.parseDouble(PowerRank[i][1].toString());
            }
        }

        return (dblPowerRankOpp > dblPowerRankHome);
    }
}
