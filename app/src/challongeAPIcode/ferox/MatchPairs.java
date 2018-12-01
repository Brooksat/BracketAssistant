package ferox;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
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
		int numberOfPreround = (playerList.size() - numberOfByes)/2;
		int nonPreround = nextOrEqualPowerOfTwo / 4;
		int numberOfMatches = nonPreround + numberOfPreround;

		ArrayList<match> matchList = new ArrayList<match>();
		//make matches and adds to list
		for (int i = 0; i < numberOfMatches; i++) {
			match aMatch = new match();
			matchList.add(aMatch);
		}
		//set  first participant for non preround matches
		for (int i = 0; i < numberOfByes; i++) {
			if (i < nonPreround) {
				matchList.get(i).setP1(playerList.get(i));
			} else {
				matchList.get(nonPreround - (i+1 - nonPreround)).setP2(playerList.get(i));
			}
		}
		//set second participant for non preround matches
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
		//set participants for preround matches
		for(int i = nonPreround; i < matchList.size();i++) {
			matchList.get(i).setP1(playerList.get(numberOfByes+(i-nonPreround)));
			matchList.get(i).setP2(playerList.get((playerList.size()-(i-nonPreround))-1));
		}

		for (int i = 0; i < matchList.size(); i++) {
			System.out.println(matchList.get(i).getP1().getName() + " vs " + matchList.get(i).getP2().getName());
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