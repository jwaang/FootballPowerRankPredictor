import org.jsoup.Jsoup;
import org.jsoup.helper.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;

import java.io.BufferedReader;
import java.io.*;
import java.util.*;
import java.net.*;

// Scrapes from http://www.cbssports.com/nfl/teams/schedule/BUF/buffalo-bills
// Compare data from http://espn.go.com/nfl/team/schedule/_/name/buf/buffalo-bills

public class Main {
	public static void main(String[] args) {
		try {
		// Create DB files for regseason and preseason
		PrintWriter regseason = new PrintWriter(new FileWriter("regseason.db"));
		String regLine = "CREATE TABLE regseason (weekNumber INTEGER, team VARCHAR(5), opponent VARCHAR(5), score VARCHAR(10)) PRIMARY KEY (weekNumber);";
 		regseason.write(regLine + "\n\n");
 		
 		PrintWriter preseason = new PrintWriter(new FileWriter("preseason.db"));
		String preLine = "CREATE TABLE preseason (weekNumber INTEGER, team VARCHAR(5), opponent VARCHAR(5), score VARCHAR(10)) PRIMARY KEY (weekNumber);";
 		preseason.write(preLine + "\n\n");
 		
		// Hashmap for getting abbreviation at the end
		HashMap<String, String> hm = new HashMap<String, String>();
		String[] teamProperNameStrArr = {"Buffalo", "Miami", "New England", "N.Y. Jets", "Baltimore", "Cincinnati", "Cleveland", "Pittsburgh", "Houston", "Indianapolis", "Jacksonville", "Tennessee", "Denver", "Kansas City", "Oakland", "San Diego", "Dallas", "N.Y. Giants", "Philadelphia", "Washington", "Chicago", "Detroit", "Green Bay", "Minnesota", "Atlanta", "Carolina", "New Orleans", "Tampa Bay", "Arizona", "St. Louis", "San Francisco", "Seattle", "BYE"};
		String[] teamAbbrvStrArr = {"BUF", "MIA", "NE", "NYJ", "BAL", "CIN", "CLE", "PIT", "HOU", "IND", "JAC", "TEN", "DEN", "KC", "OAK", "SD", "DAL", "NYG", "PHI", "WAS", "CHI", "DET", "GB", "MIN", "ATL", "CAR", "NO", "TB", "ARI", "STL", "SF", "SEA", "BYE"};
		String[] teamNamesStrArr = {"buffalo-bills", "miami-dolphins", "new-england-patriots", "new-york-jets", "baltimore-ravens", "cincinnati-bengals", "cleveland-browns", "pittsburgh-steelers", "houston-texans", "indianapolis-colts", "jacksonville-jaguars", "tennessee-titans", "denver-broncos", "kansas-city-chiefs", "oakland-raiders", "san-diego-chargers", "dallas-cowboys", "new-york-giants", "philadelphia-eagles", "washington-redskins", "chicago-bears", "detroit-lions", "green-bay-packers", "minnesota-vikings", "atlanta-falcons", "carolina-panthers", "new-orleans-saints", "tampa-bay-buccaneers", "arizona-cardinals", "st-louis-rams", "san-francisco-49ers", "seattle-seahawks"};
		for(int i=0; i<teamAbbrvStrArr.length; ++i){
			hm.put(teamProperNameStrArr[i], teamAbbrvStrArr[i]);
		}
		
		// Get Year
		//int year = 2015;
		int year = Integer.parseInt(args[0]);	
		if(year > 2015 || year < 2007) {
			System.out.println("Invalid year: " + year);
			System.out.println("Pick an year between 2007 - 2015");
			return;
		}
		
		System.out.println("Now scraping preseason and regular season data for year: " + year + ".\nPlease wait.");
		
		// Iterate through teams to collect preseason data & get schedule of team
		for(int i=0; i<teamNamesStrArr.length; ++i){
		//for(int i=0; i<1; ++i){	
			int preScoringMargin = 0;
			String reg = "[-\\s]";
			String url = "http://www.cbssports.com/nfl/teams/schedule/" + teamAbbrvStrArr[i] + "/" + teamNamesStrArr[i] + "/" + year;
			Document doc = Jsoup.connect(url).get();
			
			// *******************
			// Regular Season Data
			// *******************
			String[] schedule = new String[17];
			String[] score = new String[17];
			for(int j=0; j<17; ++j){
				Elements elemTeam = doc.select("#layoutTeamsRight > div.column1 > table:nth-child(29) > tbody > tr:nth-child("+(j+3)+") > td:nth-child(2) > a");
				Elements elemScore = doc.select("#layoutTeamsRight > div.column1 > table:nth-child(29) > tbody > tr:nth-child("+(j+3)+") > td:nth-child(3) > a");
				// Team
				if(!elemTeam.isEmpty()){
					String strTeam = elemTeam.first().text();
					if(strTeam.charAt(0)=='@'){
						schedule[j] = strTeam.substring(1);
					}
					else{
						schedule[j] = strTeam;
					}
				}else{
					schedule[j] = "BYE";
				}
				
				// Score
				if(!elemScore.isEmpty()){
					String strTeam = elemScore.first().text();
					String[] newStrTeam = strTeam.split(" ");
					if(newStrTeam[0].equals("Lost")){
						score[j] = newStrTeam[1];
					}else if(newStrTeam[0].equals("Won")){
						score[j] = newStrTeam[1];
					}else if(newStrTeam[0].equals("Tied")){
						score[j] = newStrTeam[1];
					}
				}else{
					score[j] = "BYE";
				}
				regLine = "INSERT INTO regseason VALUES FROM (" + (j+1) + ", \"" + teamAbbrvStrArr[i] + "\", \"" + hm.get(schedule[j]) + "\", \"" + score[j] + "\");";
				regseason.write(regLine + "\n");
			}
			
			// ***************
			// Pre Season Data
			// ***************
			// Preseason may have 5 matches, keep scraping, but if no elements found, then exit loop
			boolean exit = true;
			int z = 3;
			while(exit){
				Elements preSeasonTeam = doc.select("#layoutTeamsRight > div.column1 > table:nth-child(27) > tbody > tr:nth-child(" + z + ") > td:nth-child(2) > a");
				Elements preSeasonScore = doc.select("#layoutTeamsRight > div.column1 > table:nth-child(27) > tbody > tr:nth-child(" + z + ") > td:nth-child(3) > a");
				if(!preSeasonScore.isEmpty()){
					if(preSeasonTeam.first().text().charAt(0)=='@'){
						String stringTeam = preSeasonTeam.first().text().substring(1);
						String stringScore = preSeasonScore.first().text();
						String[] splitScore = stringScore.split(" ");
						if(splitScore[0].equals("Lost")){
							stringScore = splitScore[1];
						}else if(splitScore[0].equals("Won")){
							stringScore = splitScore[1];
						}else if(splitScore[0].equals("Tied")){
							stringScore = splitScore[1];
						}
						preLine = "INSERT INTO preseason VALUES FROM (" + (z-2) + ", \"" + teamAbbrvStrArr[i] + "\", \"" + hm.get(stringTeam) + "\", \"" + stringScore + "\");";
						preseason.write(preLine + "\n");
					} else {
						String stringTeam = preSeasonTeam.first().text();
						String stringScore = preSeasonScore.first().text();
						String[] splitScore = stringScore.split(" ");
						if(splitScore[0].equals("Lost")){
							stringScore = splitScore[1];
						}else if(splitScore[0].equals("Won")){
							stringScore = splitScore[1];
						}else if(splitScore[0].equals("Tied")){
							stringScore = splitScore[1];
						}
						preLine = "INSERT INTO preseason VALUES FROM (" + (z-2) + ", \"" + teamAbbrvStrArr[i] + "\", \"" + hm.get(stringTeam) + "\", \"" + stringScore + "\");";
						preseason.write(preLine + "\n");
					}
					z += 1;
				} else exit = false;
			}
			
			// New Line
			regseason.write("\n");
			preseason.write("\n");
		}
		
		// Save and close
		preseason.write("SAVE preseason\n\n");
		preseason.close();
		regseason.write("SAVE regseason\n\n");
		regseason.close();
			
		System.out.println("Finished scraping");
		
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}