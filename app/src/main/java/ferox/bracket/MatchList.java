package ferox.bracket;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MatchList {
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    static final int MODE_SINGLE_ELIM = 0;
    static final int MODE_DOUBLE_ELIM = 1;
    static final int MODE_ROUND_ROBIN = 2;
    static final int MODE_SWISS = 3;



    Context mContext;
    String url = "https://api.challonge.com/v1/tournaments/ka35zhyo/participants.json?api_key=hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    String mGetParticipantJson;
    int mNumOfLR1;
    int mNumberOfMatches;
    int mQualifyRound;
    int mPostQualRound;
    ArrayList<Match> mMatchList;


    public MatchList(Context context) {
        mContext = context;


    }


    private ArrayList<Participant> parsePlayerList(String playerListJson) {
        JsonParser jp = new JsonParser();
        JsonElement jsonTree = jp.parse(playerListJson);
        JsonArray ja = jsonTree.getAsJsonArray();

        ArrayList<Participant> playerList = new ArrayList<Participant>();


        //makes participants and gets name and seed from challonge
        for (int i = 0; i < ja.size(); i++) {

            Participant player = new Participant(
                    ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("name").getAsString(),
                    ja.get(i).getAsJsonObject().get("participant").getAsJsonObject().get("seed").getAsInt());
            playerList.add(player);
        }
        return playerList;

    }

    public void sendGetParticipants(String name) {


        RequestQueue queue = RequestQueueSingleton.getInstance(mContext.getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, ChallongeRequests.participantIndex(name),
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
                Log.d("Response", String.valueOf(error));
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
    public ArrayList<Match> makeMatchList(ArrayList<Participant> playerList) {
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

        Log.d("nPO2", String.valueOf(playerList.size()));
        Log.d("nPO2", String.valueOf(nextOrEqualPowerOfTwo));
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

        ArrayList<Match> matchList = new ArrayList<Match>();
        //make matches and sets seed for the postQualRound matches
        for (int i = 0; i < numberOfMatches; i++) {
            Match aMatch = new Match();
            if (i < postQualRound) {
                aMatch.setP1Seed(arr[2 * i]);
                aMatch.setP2Seed(arr[(2 * i) + 1]);
            }
            matchList.add(aMatch);
        }

        // set participants for post qualifying round to matches by going through the playerlist and checking
        //its seed against the seeded part of the matchlist
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

        //set participants for preround matches, after the participants in the postQualRound are
        //matched up the remaining participants are matched by putting next highest seed against the
        //next lowest. Also sets seed for these matches. These matches will also be in order of
        //P1Seed at this point in time
        for (int i = 0; (i + postQualRound) < matchList.size(); i++) {
            matchList.get(i + postQualRound).setP1(playerList.get(i + numberOfByes));
            matchList.get(i + postQualRound).setP1Seed(i + numberOfByes);
            matchList.get(i + postQualRound).setP2(playerList.get(playerList.size() - (i + 1)));
            matchList.get(i + postQualRound).setP2Seed(playerList.size() - (i + 1));
        }

        //Any undecided participants will be set to default participant
        for (int i = 0; i < matchList.size(); i++) {
            if (matchList.get(i).getP1() == null) {
                Participant p = new Participant();
                matchList.get(i).setP1(p);
            }
            if (matchList.get(i).getP2() == null) {
                Participant p = new Participant();
                matchList.get(i).setP2(p);
            }
        }

        //set match numbers
        //The order of matches in the matchList is currently (postQualRound matches with
        //participants in correct seed order) -> (remaining matches in order of P1 seed)


        //if bracket is even then match number is in order of matchlist
        if (isPowerOfTwo(playerList.size())) {
            for (int i = 0; i < matchList.size(); i++) {
                matchList.get(i).setNumber(i + 1);
            }
        }
        //if bracket is uneven then match number order depends on number range
        else {
            //numassigned is used to tell how many matches have been numbered so far
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


            //Matches are number in a different order if number of byes is greater or less than the number
            //of matches in the post qualifying round

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
        Collections.sort(matchList.subList(postQualRound, matchList.size()), new Comparator<Match>() {
            @Override
            public int compare(Match p1, Match p2) {
                return p1.getNumber() - p2.getNumber(); // Ascending
            }

        });


        //prints results TO BE REMOVED
        for (int i = 0; i < matchList.size(); i++) {
            if (i == qualifyRound) {
                System.out.println();
            }
            System.out.println(matchList.get(i).getNumber() + "-  " + matchList.get(i).getP1().getName() + " vs. " + matchList.get(i).getP2().getName());
        }

        Bracket bracket = (Bracket) mContext;
        //bracket.setPostQualRound(postQualRound);
        //bracket.setQualifyRound(qualifyRound);
        //bracket.setNumOfLR1(numOfLR1);
        //need to change function to void
        //bracket.startBracketDisplay(5, 6, mContext);
        bracket.bv.requestLayout();
        Log.d("Match List size", Integer.toString(matchList.size()));


        return matchList;
    }

    //makes an array of ints in order of the seeding of an even tournament bracket
    public int[] seedArray(int[] arr, int partition) {

        if (partition > 1) {
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
        //zero causes the method to return max int value which causes an out of memeory error
        if (i == 0) {
            return 0;
        }
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

    public ArrayList<Match> getmMatchList() {
        return mMatchList;
    }

    public void setmMatchList(ArrayList<Match> mMatchList) {
        this.mMatchList = mMatchList;
    }
}