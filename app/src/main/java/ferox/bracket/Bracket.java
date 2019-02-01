package ferox.bracket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Bracket extends AppCompatActivity {

    int numRoundW;
    int numRoundL;
    int matchesInRound1;
    Canvas mCanvas;
    Paint mPaint;
    BracketView bv;
    TextView yposition;
    TextView bracketHeight;
    TextView screenHeight;
    TextView oneParticipantMessage;
    MatchList mp;
    ArrayList<Match> mMatchList;
    int postQualRound;
    int qualifyRound;
    int numOfLR1;
    int mHeightUnit;
    int mWidthUnit;
    int numRoundsWinners;
    int numRoundsLosers;

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    ChallongeRequests CR;

    ArrayList<Match> matchList;
    ArrayList<Round> roundList;
    String url;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracket);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();

        numRoundW = 3;
        numRoundL = 6;
        matchesInRound1 = 10;

        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height) / 2;
        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);


        matchList = new ArrayList<>();
        roundList = new ArrayList<>();
        oneParticipantMessage = findViewById(R.id.one_participant_message);
        bv = findViewById(R.id.bracket_root);
        yposition = findViewById(R.id.YPosition);
        bracketHeight = findViewById(R.id.height);
        screenHeight = findViewById(R.id.screen_height);

        url = intent.getStringExtra("tournamentURL");
        type = intent.getStringExtra("tournamentType");
        mp = new MatchList(this);
        mp.sendGetParticipants(url);

        CR = new ChallongeRequests(api_key);

        getMatches(url);


//        final Handler handler = new Handler();
//        final int delay = 10; //milliseconds
//
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                //do something
//                bv.montitor(yposition, bracketHeight, screenHeight);
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

    }

    /*
    Get list of all matches and set the bracket accordingly
     */

    public void setMatchInfo() {
        Log.d("setMatchInfo", "This is being called");
        LinearLayout bw = findViewById(R.id.bracket_winners);

        LinearLayout round = (LinearLayout) bw.getChildAt(2);

        for (int i = 0; i < roundList.get(6).getMatchList().size(); i++) {
            ConstraintLayout match = (ConstraintLayout) round.getChildAt((2 * i) + 1);
            setMatchView(match, roundList.get(6).getMatchList().get(i));
        }


    }

    public void setMatchView(ConstraintLayout match, Match matchInfo) {


        TextView matchNumber = match.findViewById(R.id.matchNumber);
        matchNumber.setText(String.valueOf(matchInfo.getNumber()));
        TextView P1Seed = match.findViewById(R.id.seed1);
        P1Seed.setText(String.valueOf(matchInfo.getP1Seed()));
        TextView P2Seed = match.findViewById(R.id.seed2);
        P2Seed.setText(String.valueOf(matchInfo.getP2Seed()));
        TextView P1Name = match.findViewById(R.id.participant1);
        P1Name.setText(matchInfo.getP1().getName());
        TextView P2Name = match.findViewById(R.id.participant2);
        P2Name.setText(matchInfo.getP2().getName());

    }

    public void getMatchInfo(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        JsonElement tournament = jsonParser.parse(jsonString);
        JsonObject matchesByRound = tournament.getAsJsonObject().get("matches_by_round").getAsJsonObject();

        //The entryset is not in order so this is to make a list of jsonobjects in some kind legitimate order

        ArrayList<StringJsonArrayPair> holder = new ArrayList<>();


        for (Map.Entry<String, JsonElement> round : matchesByRound.entrySet()) {
            StringJsonArrayPair pair = new StringJsonArrayPair(round.getKey(), round.getValue().getAsJsonArray());

            holder.add(pair);
        }
        Collections.sort(holder, (p1, p2) -> p1.getName().compareTo(p2.getName()));

        for (int i = 0; i < holder.size(); i++) {
            System.out.println(holder.get(i).getName());
        }


        for (int i = 0; i < holder.size(); i++) {
            String name = holder.get(i).getName();
            JsonArray round = holder.get(i).getJsonArr();
            Round roundTmp = new Round();
            roundTmp.setName(name);
            roundTmp.setNumber(Integer.parseInt(name));
            System.out.println(name);
            if (roundTmp.getNumber() < 0) {
                roundTmp.isInWinners = false;
            }
            JsonArray currentRound = round;
            for (JsonElement match : currentRound) {
                Participant player1 = new Participant();
                Participant player2 = new Participant();
                Match matchTmp = new Match();
                if (!match.getAsJsonObject().get("player1").isJsonNull()) {
                    JsonObject p1 = match.getAsJsonObject().get("player1").getAsJsonObject();
                    player1.setName(p1.get("display_name").getAsString());
                    player1.setSeed(p1.get("seed").getAsInt());
                    matchTmp.setP1Seed(player1.getSeed());

                } else {
                    matchTmp.setP1Decided(false);
                    if (!match.getAsJsonObject().get("player1_placeholder_text").isJsonNull()) {
                        player1.setName(match.getAsJsonObject().get("player1_placeholder_text").getAsString());
                    }
//                    if(match.getAsJsonObject().get("player1_prereq_identifier")!=null){
//                        //sets corresponding match as previous match
//                    }
                }
                if (!match.getAsJsonObject().get("player2").isJsonNull()) {
                    JsonObject p2 = match.getAsJsonObject().get("player2").getAsJsonObject();
                    player2.setName(p2.get("display_name").getAsString());
                    player2.setSeed(p2.get("seed").getAsInt());
                    matchTmp.setP2Seed(player2.getSeed());
                } else {
                    matchTmp.setP2Decided(false);
                    if (!match.getAsJsonObject().get("player2_placeholder_text").isJsonNull()) {
                        player2.setName(match.getAsJsonObject().get("player2_placeholder_text").getAsString());
                    }
                }
                matchTmp.setP1(player1);
                matchTmp.setP2(player2);
                matchTmp.setNumber(match.getAsJsonObject().get("identifier").getAsInt());

                roundTmp.getMatchList().add(matchTmp);

            }
            roundList.add(roundTmp);
            // Log.d("number", String.valueOf(roundTmp.getNumber()));
        }
        if (type.equals("single elimination")) {
            numRoundsWinners = roundList.get(roundList.size() - 1).getNumber();
        } else if (type.equals("double elimination")) {
            //set number of losers rounds to the number of the last round in the round list which
            //is the last losers round, then set the number of winners round by getting the index
            //of the last winners round that was added whos index is equal to
            // index of last round - number of losers rounds
            Log.d("roundlistsize", String.valueOf(roundList.size() - 1));

            numRoundsWinners = roundList.get(roundList.size() - 1).getNumber();
            Log.d("roundlistsize", String.valueOf(numRoundsLosers));
            numRoundsLosers = roundList.get((roundList.size() - 1) - numRoundsWinners).getNumber();
        }
        setMatchInfo();
    }


    public void startBracketDisplay(int numRoundW, int numRoundL, Context context) {
        if (matchList.size() == 0) {
            oneParticipantMessage.setVisibility(View.VISIBLE);
        } else {
            oneParticipantMessage.setVisibility(View.GONE);
            makeBracketDisplay(numRoundW, numRoundL, context);
        }
    }


    public void makeBracketDisplay(int numRoundW, int numRoundL,
                                   Context context) {

        ConstraintLayout bracketRoot = findViewById(R.id.bracket_root);
        LinearLayout roundWinners = findViewById(R.id.round_winners);
        LinearLayout bracketWinners = findViewById(R.id.bracket_winners);
        LinearLayout roundLosers = findViewById(R.id.round_losers);
        LinearLayout bracketLosers = findViewById(R.id.bracket_losers);

        //empties the linear layouts. May not be needed
        for (int i = 0; i < bracketRoot.getChildCount(); i++) {
            ViewGroup child = (ViewGroup) bracketRoot.getChildAt(i);
            child.removeAllViews();
        }


        //bracket winners
        //adds winners rounds
        int numberRound = qualifyRound == 0 ? postQualRound : postQualRound * 2;
        setRoundHeaders(numberRound);
        for (int i = 0; numberRound != 0; i++) {

            makeRound(numberRound, i, bracketWinners, 0);
            makeBracketConnectors(numberRound, i, bracketWinners, 0);

//            for (int j=0;j<2;j++){
//                makeRound(numberRound/2, i, bracketLosers, 0);
//                makeBracketConnectors(numberRound/2,i,bracketLosers,0);
//            }


            if (numberRound == 1 && type.equals("double elimination")) {

                makeGrandFinals(i, bracketWinners);
            }
            numberRound = numberRound / 2;


        }


        //setPostQualRoundInfo();
        if (qualifyRound != 0) {
            setUnusedMatchesInvisible();
        }


    }

    public void makeGrandFinals(int multiplier, ViewGroup vg) {

        makeBracketConnectors(1, multiplier, vg, 1);
        makeRound(1, multiplier, vg, 1);
        makeBracketConnectors(1, multiplier, vg, 2);
        makeRound(1, multiplier, vg, 1);


    }

    public void makeRound(int numMatches, int multiplier, ViewGroup vg, int modifier) {

        LinearLayout matches = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        matches.setLayoutParams(layoutParams);

        matches.setOrientation(LinearLayout.VERTICAL);

        matches.setGravity(Gravity.CENTER);


        //for every round after round 1 there is a space added before the matches for alignment
        if (multiplier != 0 || modifier == 1) {
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + (modifier == 0 ? 0 : 1))));
            matches.addView(space);
        }

        //creates the matches
        for (int j = 0; j < numMatches; j++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConstraintLayout match = (ConstraintLayout) inflater.inflate(R.layout.match, null);
            TextView textView1 = match.findViewById(R.id.matchNumber);
            textView1.setText(Integer.toString(j));

            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (mHeightUnit * ((int) Math.pow(2, multiplier + 2) - 2))));
            if (j == numMatches - 1) {
                space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit));
            }


            matches.addView(match);

            matches.addView(space);

        }


        vg.addView(matches);


    }

    public void makeBracketConnectors(int numMatches, int multiplier, ViewGroup vg, int modifier) {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout bracketConnector = new LinearLayout(this);
        bracketConnector.setLayoutParams(layoutParams);
        bracketConnector.setOrientation(LinearLayout.VERTICAL);

        //for non grand finals rounds
        if (modifier == 0) {
            for (int i = 0; i < numMatches / 2; i++) {
                Space space = new Space(this);
                Space space2 = new Space(this);
                Space space3 = new Space(this);
                space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
                space2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
                space3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * 2));

                bracketConnectorView bcv = new bracketConnectorView(this, null
                        , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                        , bracketConnectorView.MODE_TOP
                        , "");
                bracketConnectorView bcv2 = new bracketConnectorView(this, null
                        , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                        , bracketConnectorView.MODE_BOTTOM
                        , "");


                bracketConnector.addView(space);
                bracketConnector.addView(bcv);
                bracketConnector.addView(bcv2);
                bracketConnector.addView(space2);
                bracketConnector.addView(space3);

            }
        } else if (modifier == 1) {
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
            bracketConnectorView bcv = new bracketConnectorView(this, null, mHeightUnit, bracketConnectorView.MODE_TOP, "");
            bracketConnector.addView(space);
            bracketConnector.addView(bcv);
        } else if (modifier == 2) {
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + 1)));
            bracketConnectorView bcv = new bracketConnectorView(this, null, mHeightUnit * 2, bracketConnectorView.MODE_MIDDLE, "");
            bracketConnector.addView(space);
            bracketConnector.addView(bcv);
        }
        vg.addView(bracketConnector);
    }


    public void setRoundHeaders(int numRounds) {
        LinearLayout roundWinners = findViewById(R.id.round_winners);
        LinearLayout roundLosers = findViewById(R.id.round_losers);

        ViewGroup.MarginLayoutParams roundHeaderLayoutParams = new ViewGroup.MarginLayoutParams(
                mWidthUnit * 2, getResources().getDimensionPixelSize(R.dimen.round_header_height));
        roundHeaderLayoutParams.setMargins(0, 0, 0, 0);


        int tmp = numRounds;
        //adds round headers
        for (int i = 1; tmp != 0; i++) {
            TextView roundNumber = new TextView(this);
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            if (tmp != 1) {
                roundNumber.setText(getString(R.string.Round_Number, i));
                roundWinners.addView(roundNumber);
            } else {
                roundNumber.setText(getString(R.string.Semifinals));
                TextView finals = new TextView(this);
                finals.setGravity(Gravity.CENTER);
                finals.setText(getString(R.string.Finals));
                roundWinners.addView(roundNumber);
                roundWinners.addView(finals);
            }

            tmp = tmp / 2;
        }
    }

    private void setPostQualRoundInfo() {
        if (matchList.size() > 2) {
            ViewGroup bracketWinners = findViewById(R.id.bracket_winners);
            ViewGroup PQR = (LinearLayout) bracketWinners.getChildAt(qualifyRound == 0 ? 0 : 2);
            for (int i = 0; i < postQualRound; i++) {
                //plus 1 caused by conditional space in get round
                //Because of the if statement in getRound the first round rendered does not have a
                //space at the beginning of it. There for when the PQR is the first round rendered
                //then the index for the Contraint layout is 2*i else is it 2*i+1
                ConstraintLayout match = (ConstraintLayout) PQR.getChildAt(qualifyRound == 0 ? 2 * i : 2 * i + 1);
                TextView matchNumber = match.findViewById(R.id.matchNumber);
                matchNumber.setText(String.valueOf(matchList.get(i).getNumber()));
                TextView P1Seed = match.findViewById(R.id.seed1);
                P1Seed.setText(String.valueOf(matchList.get(i).getP1Seed()));
                TextView P2Seed = match.findViewById(R.id.seed2);
                P2Seed.setText(String.valueOf(matchList.get(i).getP2Seed()));
                TextView P1Name = match.findViewById(R.id.participant1);
                P1Name.setText(matchList.get(i).getP1().getName());
                TextView P2Name = match.findViewById(R.id.participant2);
                P2Name.setText(matchList.get(i).getP2().getName());
            }
        }

    }

    private void setUnusedMatchesInvisible() {
        ViewGroup bracketWinners = findViewById(R.id.bracket_winners);
        ViewGroup QR = (LinearLayout) bracketWinners.getChildAt(0);
        ViewGroup QRBC = (LinearLayout) bracketWinners.getChildAt(1);

        for (int i = 0; i < postQualRound; i++) {
            if (matchList.get(i).isP1Decided() == true) {
                QR.getChildAt(4 * i).setVisibility(View.INVISIBLE);
                QRBC.getChildAt(5 * i + 1).setVisibility(View.INVISIBLE);
            }
            if (matchList.get(i).isP2Decided() == true) {
                QR.getChildAt(4 * i + 2).setVisibility(View.INVISIBLE);
                QRBC.getChildAt(5 * i + 2).setVisibility(View.INVISIBLE);
            }
        }
    }


    public void getMatches(String URL) {


        RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).
                getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CR.jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(URL),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        getMatchInfo(response);
                        Log.d("Request", " Request Received");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", " Error");
            }
        });


        RequestQueueSingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


    public ArrayList<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(ArrayList<Match> matchList) {
        this.matchList = matchList;
    }

    public int getPostQualRound() {
        return postQualRound;
    }

    public void setPostQualRound(int postQualRound) {
        this.postQualRound = postQualRound;
    }

    public void setQualifyRound(int qualifyRound) {
        this.qualifyRound = qualifyRound;
    }

    public void setNumOfLR1(int numOfLR1) {
        this.numOfLR1 = numOfLR1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

