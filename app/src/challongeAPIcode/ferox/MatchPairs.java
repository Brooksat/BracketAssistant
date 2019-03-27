package ferox;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

	//take whats in main and makes bracket
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
		int[] arr = new int[nextOrEqualPowerOfTwo/2];
		//sets variables in case of an even bracket
		if(isPowerOfTwo(playerList.size())) {
			numberOfByes = nextOrEqualPowerOfTwo;
			qualifyRound = 0;
			postQualRound = nextOrEqualPowerOfTwo/2;
			numberOfMatches = playerList.size()/2;
			arr = new int[nextOrEqualPowerOfTwo];
		}

		arr = seedArray(arr, arr.length);

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

		//set match numbers
		if(isPowerOfTwo(playerList.size())) {
			for (int i = 0; i < matchList.size(); i++) {
				matchList.get(i).setNumber(i+1);
			}
		}
		else {
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

		}
		//sorts matches based in match number
		Collections.sort(matchList, new Comparator<match>() {
			@Override public int compare(match p1, match p2) {
				return p1.number - p2.number; // Ascending
			}

		});
		//prints results TO BE REMOVED
		for (int i = 0; i < matchList.size(); i++) {
			if(i==qualifyRound) {
				System.out.println();
			}
			System.out.println(matchList.get(i).getNumber() + "-  " + matchList.get(i).getP1().getName() + " vs. " + matchList.get(i).getP2().getName());
		}
	}

	//makes an array if ints in order of the seeding of an even tournament bracket
	public int[] seedArray(int[] arr, int partition) {

		if(partition != 1) {
			//calls itself until the array = {1} is return
			int[] split = seedArray(arr,partition/2);
			int[] tmp = new int[split.length*2];
			int[] doublesplit = new int[split.length];

			//creates an array that is equal to split except there is a space between each element
			// if split is equal to [1,2] then tmp is [1,0,2,0]
			for(int i=0;i<split.length;i++) {
				tmp[i*2] = split[i];
			}
			//makes array equal to next split.length integers after split.length
			//if array is [1,2], doublesplit is [3,4]
			for (int i = 0; i < doublesplit.length; i++) {
				doublesplit[i] = i+split.length+1;
			}


			int[] result = matchLowHigh(tmp, doublesplit);
			return result;



		}
		else if(partition == 1) {
			int[] i = {1};
			return i;
		}

		else {
			int[] i = {1};
			return i;
		}

	}

	//method for seedArray. takes the expanded bracket from next recursive layer and adds the remaining numbers
	//[1,0,2,0] and [3,4] return [1,4,2,3]
	public int[] matchLowHigh(int[] low, int[] high) {


		int lower = 1;
		int upper = high[high.length-1];
		while(lower < upper) {
			for (int i = 0; i < low.length; i++) {
				if(low[i]==lower) {
					low[i+1] = upper;
				}
			}
			++lower;
			--upper;
		}
		return low;
	}

	public int nextOrEqualPowerOfTwo(int i) {
		int result = (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(i - 1));
		if (result / 2 == i) {
			return i;
		}
		return result;
	}

	public boolean isPowerOfTwo(int num){
		return (int)(Math.ceil((Math.log(num) / Math.log(2)))) == (int)(Math.floor(((Math.log(num) / Math.log(2))))); 
	}
}