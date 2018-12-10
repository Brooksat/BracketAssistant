package ferox;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;


public class MatchPairs {

	public static void main(String[] args) {

		MatchPairs bracket = new MatchPairs();

		// NEED TO GET SPECIFIC FIELDS FOR JSON LIST

		ChallongeRequests CR = new ChallongeRequests();
		String request = "";
		try {
			request = CR.getTournament("ka35zhyo");


		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonParser jp = new JsonParser();
		JsonElement jsonTree = jp.parse(request);
		JsonArray ja = jsonTree.getAsJsonArray();


		ArrayList<participant> playerList = new ArrayList<participant>();


		//makes participants and gets name and seed from challonge
		for (int i = 0; i < ja.size(); i++) {

			participant player = new participant(
					ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("name").getAsString(),
					ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("seed").getAsInt());
			playerList.add(player);
		}


		bracket.makeBracket(playerList);

	}

	//take whats in main and make methods out of them
	public void makeBracket(ArrayList<participant> playerList) {
		// gets next power of two (needs to be put in method) needs to get next or equal
		// power of two
		int nextOrEqualPowerOfTwo = nextOrEqualPowerOfTwo(playerList.size());
		int numberOfByes = nextOrEqualPowerOfTwo - playerList.size();
		int numNonByes = playerList.size() - numberOfByes;
		int qualifyRound = (playerList.size() - numberOfByes)/2;
		int postQualRound = nextOrEqualPowerOfTwo / 4;
		int numberOfMatches = postQualRound + qualifyRound;
		int numOfLR1 = playerList.size() % postQualRound;

		bracketBasis basis = new bracketBasis();
		int[] arr = new int[nextOrEqualPowerOfTwo/2];
		arr = basis.populateArray(arr, arr.length);
		System.out.println(Arrays.toString(arr));

		ArrayList<match> matchList = new ArrayList<match>();
		//make matches and adds to list
		for (int i = 0; i < numberOfMatches; i++) {
			match aMatch = new match();
			if(i<postQualRound) {
				aMatch.setP1Seed(arr[2*i]);
				aMatch.setP2Seed(arr[(2*i)+1]);
			}
			matchList.add(aMatch);
		}

		// set participants for post qualifying round

		for(int i = 0;i<numberOfByes;i++) {
			for (int j = 0; j < postQualRound; j++) {

				if (playerList.get(i).getSeed() == matchList.get(j).getP1Seed()) {
					matchList.get(j).setP1(playerList.get(i));
					break;
				}
				if (playerList.get(i).getSeed() == matchList.get(j).getP2Seed()) {
					matchList.get(j).setP2(playerList.get(i));
					break;
				} 

			}
		}
		

		
		//set participants for preround matches
		for (int i = 0; (i+postQualRound) < matchList.size(); i++) {
		matchList.get(i+postQualRound).setP1(playerList.get(i+numberOfByes));
		matchList.get(i+postQualRound).setP2(playerList.get(playerList.size()-(i+1)));
		}

		//fills empty spots
		for (int i = 0; i < matchList.size(); i++) {
			if(matchList.get(i).getP1()==null) {
				participant p = new participant();
				matchList.get(i).setP1(p);
			}
			if(matchList.get(i).getP2()==null)
			{
				participant p = new participant();
				matchList.get(i).setP2(p);
			}
		}

		//sets match numbering for preround(i++/i-- can be removed if break is removed)

		int numAssigned = 0;
		for (int i = 0; i < postQualRound; i++) {
			for (int j = postQualRound; j < matchList.size(); j++) {
				if(matchList.get(j).getP1().getSeed() == matchList.get(i).getP1Seed()) {

					
					matchList.get(j).setNumber(numAssigned+1);
					numAssigned++;

				}
				else if(matchList.get(j).getP1().getSeed()==matchList.get(i).getP2Seed()) {
					
					matchList.get(j).setNumber(numAssigned+1);
					numAssigned++;

				}
			}
		}

		if(numberOfByes>=postQualRound) {
			for (int i = 0; i < postQualRound; i++) {
				if(matchList.get(i).getP2().getSeed() != 0) {
					matchList.get(i).setNumber(numAssigned+1);
					numAssigned++;
				}
			}
			for (int i = 0; i < postQualRound; i++) {
				if(matchList.get(i).getP2().getSeed() == 0) {
					matchList.get(i).setNumber(numAssigned+1);
					numAssigned++;
				}
			}
		}
		else if(numberOfByes<postQualRound) {

			numAssigned = numAssigned + numOfLR1;
			for(int i = 0;i<postQualRound;i++)
			{
				matchList.get(i).setNumber(i+numAssigned+1);
			}
		}
		else {
			 for (int i = 0; i < matchList.size(); i++) {
				 matchList.get(i).setNumber(i+1);
			}
		}



		for (int i = 0; i < matchList.size(); i++) {
			if(i==postQualRound) {
				System.out.println();
			}
			System.out.println(matchList.get(i).getNumber() + "-  " + matchList.get(i).getP1().getName() + " vs. " + matchList.get(i).getP2().getName());
		}
		


		


	}

	
	
	public int nextOrEqualPowerOfTwo(int i) {
		int result = (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(i - 1));
		if (result / 2 == i) {
			return i;
		}
		return result;
	}
}