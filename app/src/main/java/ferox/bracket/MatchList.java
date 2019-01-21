package ferox.bracket;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MatchList {
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";


    ChallongeRequests CR;
    Context mContext;
    String url = "https://api.challonge.com/v1/tournaments/ka35zhyo/participants.json?api_key=hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    String mGetParticipantJson;
    int mNumOfLR1;
    int mNumberOfMatches;
    int mQualifyRound;
    int mPostQualRound;
    ArrayList<match> mMatchList;


    public MatchList(Context context) {
        mContext = context;
        CR = new ChallongeRequests(api_key);


    }


    private ArrayList<participant> parsePlayerList(String playerListJson) {
        JsonParser jp = new JsonParser();
        JsonElement jsonTree = jp.parse(playerListJson);
        JsonArray ja = jsonTree.getAsJsonArray();

        ArrayList<participant> playerList = new ArrayList<participant>();


        //makes participants and gets name and seed from challonge
        for (int i = 0; i < ja.size(); i++) {

            participant player = new participant(
                    ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("name").getAsString(),
                    ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("seed").getAsInt());
            playerList.add(player);
        }
        return playerList;

    }

    public void sendGetParticipants() {

        VolleyLog.DEBUG = true;


        RequestQueue queue = RequestQueueSingleton.getInstance(mContext.getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        mGetParticipantJson = response;
                        makeMatchList(parsePlayerList(mGetParticipantJson));
                        Log.d("Request", " Request Received");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", " Error");
            }
        });


        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(stringRequest);

    }

    /*take whats in main and makes bracket
        In the case of an uneven bracket the first round is called the qualifying round. The
        qualifying round is made in a way so that the second round of the tournament will be
        even(the number of matches and participants is a power of 2. As for the code the second
        round is referred to the post qualifying round regardless of if whether or not there was a
        qualifying round.



     */
    public ArrayList<match> makeMatchList(ArrayList<participant> playerList) {
        // gets next power of two (needs to be put in method) needs to get next or equal
        // power of two
        int nextOrEqualPowerOfTwo = nextOrEqualPowerOfTwo(playerList.size());
        int numberOfByes = nextOrEqualPowerOfTwo - playerList.size();
        int numNonByes = playerList.size() - numberOfByes;
        int qualifyRound = (playerList.size() - numberOfByes) / 2;
        int postQualRound = nextOrEqualPowerOfTwo / 4;
        int numberOfMatches;
        int numOfLR1;
        if (postQualRound < 2) {
            numberOfMatches = 2;
            numOfLR1 = 0;
        } else {
            numberOfMatches = postQualRound + qualifyRound;
            numOfLR1 = playerList.size() % postQualRound;
        }

        int[] arr = new int[nextOrEqualPowerOfTwo / 2];
        //sets variables in case of an even bracket
        if (isPowerOfTwo(playerList.size())) {
            numberOfByes = nextOrEqualPowerOfTwo;
            qualifyRound = 0;
            postQualRound = nextOrEqualPowerOfTwo / 2;
            numberOfMatches = playerList.size() / 2;
            arr = new int[nextOrEqualPowerOfTwo];
        }


        //set class parameters
        mNumberOfMatches = numberOfMatches;
        mNumOfLR1 = numOfLR1;
        mPostQualRound = postQualRound;
        mQualifyRound = qualifyRound;


        arr = seedArray(arr, arr.length);

        ArrayList<match> matchList = new ArrayList<match>();
        //make matches and adds to list
        for (int i = 0; i < numberOfMatches; i++) {
            match aMatch = new match();
            if (i < postQualRound) {
                aMatch.setP1Seed(arr[2 * i]);
                aMatch.setP2Seed(arr[(2 * i) + 1]);
            }
            matchList.add(aMatch);
        }

        // set participants for post qualifying round
        for (int i = 0; i < numberOfByes; i++) {

            for (int j = 0; j < postQualRound; j++) {

                if (playerList.get(i).getSeed() == matchList.get(j).getP1Seed()) {
                    matchList.get(j).setP1(playerList.get(i));
                    matchList.get(j).setP1Decided(true);
                    break;
                }
                if (playerList.get(i).getSeed() == matchList.get(j).getP2Seed()) {
                    matchList.get(j).setP2(playerList.get(i));
                    matchList.get(j).setP2Decided(true);
                    break;
                }

            }
        }

        //set participants for preround matches
        for (int i = 0; (i + postQualRound) < matchList.size(); i++) {
            matchList.get(i + postQualRound).setP1(playerList.get(i + numberOfByes));
            matchList.get(i + postQualRound).setP2(playerList.get(playerList.size() - (i + 1)));
        }

        //fills empty spots
        for (int i = 0; i < matchList.size(); i++) {
            if (matchList.get(i).getP1() == null) {
                participant p = new participant();
                matchList.get(i).setP1(p);
            }
            if (matchList.get(i).getP2() == null) {
                participant p = new participant();
                matchList.get(i).setP2(p);
            }
        }

        //set match numbers
        if (isPowerOfTwo(playerList.size())) {
            for (int i = 0; i < matchList.size(); i++) {
                matchList.get(i).setNumber(i + 1);
            }
        } else {
            int numAssigned = 0;
            for (int i = 0; i < postQualRound; i++) {
                for (int j = postQualRound; j < matchList.size(); j++) {
                    if (matchList.get(j).getP1().getSeed() == matchList.get(i).getP1Seed()) {


                        matchList.get(j).setNumber(numAssigned + 1);
                        numAssigned++;

                    } else if (matchList.get(j).getP1().getSeed() == matchList.get(i).getP2Seed()) {

                        matchList.get(j).setNumber(numAssigned + 1);
                        numAssigned++;

                    }
                }
            }

            if (numberOfByes >= postQualRound) {
                for (int i = 0; i < postQualRound; i++) {
                    if (matchList.get(i).getP2().getSeed() != 0) {
                        matchList.get(i).setNumber(numAssigned + 1);
                        numAssigned++;
                    }
                }
                for (int i = 0; i < postQualRound; i++) {
                    if (matchList.get(i).getP2().getSeed() == 0) {
                        matchList.get(i).setNumber(numAssigned + 1);
                        numAssigned++;
                    }
                }
            } else if (numberOfByes < postQualRound) {

                numAssigned = numAssigned + numOfLR1;
                for (int i = 0; i < postQualRound; i++) {
                    matchList.get(i).setNumber(i + numAssigned + 1);
                }
            }

        }
        //sorts matches based on match number
        Collections.sort(matchList.subList(postQualRound, matchList.size()), new Comparator<match>() {
            @Override
            public int compare(match p1, match p2) {
                return p1.number - p2.number; // Ascending
            }

        });


        //prints results TO BE REMOVED
        for (int i = 0; i < matchList.size(); i++) {
            if (i == qualifyRound) {
                System.out.println();
            }
            System.out.println(matchList.get(i).getNumber() + "-  " + matchList.get(i).getP1().getName() + " vs. " + matchList.get(i).getP2().getName());
        }

        bracket bracket = (bracket) mContext;
        bracket.setMatchList(matchList);
        bracket.setPostQualRound(postQualRound);
        bracket.setQualifyRound(qualifyRound);
        bracket.setNumOfLR1(numOfLR1);
        //need to change function to void
        bracket.makeBracketDisplay(5, 6, mContext);
        bracket.bv.requestLayout();
        Log.d("Match List size", Integer.toString(matchList.size()));


        return matchList;
    }

    //makes an array if ints in order of the seeding of an even tournament bracket
    public int[] seedArray(int[] arr, int partition) {

        if (partition != 1) {
            //calls itself until the array = {1} is return
            int[] split = seedArray(arr, partition / 2);
            int[] tmp = new int[split.length * 2];
            int[] doublesplit = new int[split.length];

            //creates an array that is equal to split except there is a space between each element
            // if split is equal to [1,2] then tmp is [1,0,2,0]
            for (int i = 0; i < split.length; i++) {
                tmp[i * 2] = split[i];
            }
            //makes array equal to next split.length integers after split.length
            //if array is [1,2], doublesplit is [3,4]
            for (int i = 0; i < doublesplit.length; i++) {
                doublesplit[i] = i + split.length + 1;
            }


            int[] result = matchLowHigh(tmp, doublesplit);
            return result;


        } else if (partition == 1) {
            int[] i = {1};
            return i;
        } else {
            int[] i = {1};
            return i;
        }

    }

    //method for seedArray. takes the expanded bracket from next recursive layer and adds the remaining numbers
    //[1,0,2,0] and [3,4] return [1,4,2,3]
    public int[] matchLowHigh(int[] low, int[] high) {


        int lower = 1;
        int upper = high[high.length - 1];
        while (lower < upper) {
            for (int i = 0; i < low.length; i++) {
                if (low[i] == lower) {
                    low[i + 1] = upper;
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

    public boolean isPowerOfTwo(int num) {
        return (int) (Math.ceil((Math.log(num) / Math.log(2)))) == (int) (Math.floor(((Math.log(num) / Math.log(2)))));
    }

    public String getmGetParticipantJson() {
        return mGetParticipantJson;
    }

    public void setmGetParticipantJson(String mGetParticipantJson) {
        this.mGetParticipantJson = mGetParticipantJson;
    }

    public int getmNumOfLR1() {
        return mNumOfLR1;
    }

    public void setmNumOfLR1(int mNumOfLR1) {
        this.mNumOfLR1 = mNumOfLR1;
    }

    public int getmNumberOfMatches() {
        return mNumberOfMatches;
    }

    public void setmNumberOfMatches(int mNumberOfMatches) {
        this.mNumberOfMatches = mNumberOfMatches;
    }

    public int getmQualifyRound() {
        return mQualifyRound;
    }

    public void setmQualifyRound(int mQualifyRound) {
        this.mQualifyRound = mQualifyRound;
    }

    public int getmPostQualRound() {
        return mPostQualRound;
    }

    public void setmPostQualRound(int mPostQualRound) {
        this.mPostQualRound = mPostQualRound;
    }

    public ArrayList<match> getmMatchList() {
        return mMatchList;
    }

    public void setmMatchList(ArrayList<match> mMatchList) {
        this.mMatchList = mMatchList;
    }
}