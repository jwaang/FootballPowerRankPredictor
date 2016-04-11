package app;

import AI.BayesianCollection;
import AI.Categorizer;
import app.Model.GuessScoresModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PowerRankAI extends GuessScoresModel implements ServiceProvider {
    @Override
    public Object[][] GetPowerRankData(int Season, int Week) {
        HashMap<String, String> hm = new HashMap<>();
        String[] abbrvName = {"buf", "mia", "ne", "nyj", "bal", "cin", "cle", "pit", "hou", "ind", "jax", 
            "ten", "den", "kc", "oak", "sd", "dal", "nyg", "phi", "wsh", "chi", "det", "gb", "min", "atl", "car", 
            "no", "tb", "ari", "la", "sf", "sea"};
        String[] fullName = {"BuffaloBills", "MiamiDolphins", "NewEnglandPatriots", "NewYorkJets", 
            "BaltimoreRavens", "CincinnatiBengals", "ClevelandBrowns", "PittsburghSteelers", "HoustonTexans", 
            "IndianapolisColts", "JacksonvilleJaguars", "TennesseeTitans", "DenverBroncos", "KansasCityChiefs", 
            "OaklandRaiders", "SanDiegoChargers", "DallasCowboys", "NewYorkGiants", "PhiladelphiaEagles", 
            "WashingtonRedskins", "ChicagoBears", "DetroitLions", "GreenBayPackers", "MinnesotaVikings", 
            "AtlantaFalcons", "CarolinaPanthers", "NewOrleansSaints", "TampaBayBuccaneers", "ArizonaCardinals", 
            "LosAngelesRams", "SanFrancisco49ers", "SeattleSeahawks"};
        for(int i=0; i<fullName.length; ++i){
            hm.put(fullName[i], abbrvName[i]);
        }
        
        Object[][] objRankData = new Object[16][2];

        // Variables definition.
        String strLine;         // Auxiliary variable to read line by line from input file.
        String[] Values;        // Array to save each individual value of a single line read from input file.
        int intRowCount = -1;	// Counter/pointer for input file records.
        final Categorizer<String, String> bayes = new BayesianCollection<>();
        //InputStream objFile = this.getClass().getClassLoader().getResourceAsStream("app/2010-2015NFLGames.txt");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Scanner objScanner = new Scanner(classLoader.getResourceAsStream("resources/2010-2015NFLGames.txt"));
        while (objScanner.hasNextLine()) {
            String line = objScanner.nextLine();
            String[] splitLine = line.split("\\s+");
            String[] arrLine = {splitLine[1], splitLine[2], splitLine[4], splitLine[7]};
            bayes.learn(splitLine[5], Arrays.asList(arrLine));
        }
        //InputStream o = this.getClass().getClassLoader().getResourceAsStream("app/" + "NFLStats" + Season + ".db");
        ClassLoader classLoader2 = Thread.currentThread().getContextClassLoader();
        Scanner os = new Scanner(classLoader2.getResourceAsStream("resources/NFLStats2015.db"));
        while(os.hasNextLine()){
            strLine = os.nextLine();
            String[] splitLine = strLine.split(",");
            // Find week lines to start reading
            if(Integer.parseInt(splitLine[0]) == Week){
                String[] arrLine = {hm.get(splitLine[1]), "reg", hm.get(splitLine[2])};
                String s = bayes.classify(Arrays.asList(arrLine)).getCategory();
                
                
                if(intRowCount==15) break;
                else intRowCount++;
                
                objRankData[intRowCount][0] = splitLine[1];
                
                //System.out.println(splitLine[1] + " " + s + " " + bayes.classify(Arrays.asList(arrLine)).getProbability());
                if(bayes.classify(Arrays.asList(arrLine)).getProbability() < .002){
                    if(s.equals("W")) objRankData[intRowCount][1] = "L";
                    else if (s.equals("L")) objRankData[intRowCount][1] = "L";
                } else objRankData[intRowCount][1] = s;
            }
        }

        return objRankData;
    }
    
    @Override
    public boolean AI(Object[][] PowerRank, String[] Values)
    {
        for (int i = 0; i < 16; i++)
        {
            if (PowerRank[i][0].equals(Values[1]) && PowerRank[i][1].equals("W"))
            {
                return false;
            }

            if (PowerRank[i][0].equals(Values[1]) && PowerRank[i][1].equals("L"))
            {
                return true;
            }
        }
        return true;     
    }
}
