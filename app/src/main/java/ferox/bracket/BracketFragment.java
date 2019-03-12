package ferox.bracket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class BracketFragment extends Fragment {

    final static int UNUSED_WINNERS = 0;
    final static int UNUSED_LOSERS_ODD = 1;
    final static int UNUSED_LOSERS_EVEN = 2;
    final static String SINGLE_ELIM = "single elimination";
    final static String DOUBLE_ELIM = "double elimination";
    final static String ROUND_ROBIN = "round robin";
    final static String AWAITING_REVIEW = "awaiting_review";
    final static String UNDERWAY = "underway";
    final static String PENDING = "pending";
    final static String COMPLETE = "complete";


    TextView yposition;
    TextView bracketHeight;
    TextView screenHeight;
    TextView oneParticipantMessage;
    int postQualRound;
    int qualifyRound;
    int numOfLR1;
    int mHeightUnit;
    int mWidthUnit;
    int numRoundsWinners;
    int numRoundsLosers;

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    ChallongeRequests CR;

    ArrayList<Round> winnersRounds;
    ArrayList<Round> losersRounds;

    String url;
    String type;
    String state;
    int numberOfParticipants;

    private BracketListener listener;

    BracketView bv;
    LoadingView lv;
    LinearLayout roundWinners;
    LinearLayout roundLosers;
    LinearLayout bracketWinners;
    LinearLayout bracketLosers;

    public interface BracketListener {
        LinearLayout getStuff();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_bracket, container, false);
        bv = v.findViewById(R.id.bracket_root);
        roundWinners = v.findViewById(R.id.round_winners);
        bracketWinners = v.findViewById(R.id.bracket_winners);
        roundLosers = v.findViewById(R.id.round_losers);
        bracketLosers = v.findViewById(R.id.bracket_losers);
        lv = v.findViewById(R.id.loading_view);

        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height) / 2;
        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);


        winnersRounds = new ArrayList<>();
        losersRounds = new ArrayList<>();

        oneParticipantMessage = v.findViewById(R.id.one_participant_message);

        url = intent.getStringExtra("tournamentURL");
        type = intent.getStringExtra("tournamentType");
        numberOfParticipants = intent.getIntExtra("tournamentSize", 0);


        CR = new ChallongeRequests(api_key);

        getMatches(url);

        return v;
    }

    public void getMatches(String URL) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, CR.jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(URL),
                response -> {
                    Log.d("Response", response);
                    getMatchInfo(response);
                    Log.d("Request", " Request Received");
                }, error -> Log.d("Response", String.valueOf(error)));


        RequestQueueSingleton.getInstance(this.getContext()).addToRequestQueue(stringRequest);

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
            String name = holder.get(i).getName();
            JsonArray round = holder.get(i).getJsonArr();
            Round roundTmp = new Round();
            roundTmp.setName(name);
            roundTmp.setNumber(Integer.parseInt(name));
            if (roundTmp.getNumber() < 0) {
                losersRounds.add(roundTmp);
                roundTmp.isInWinners = false;
            } else {
                winnersRounds.add(roundTmp);
            }

            for (JsonElement match : round) {
                Participant player1 = new Participant();
                Participant player2 = new Participant();
                Match matchTmp = new Match();
                if (!match.getAsJsonObject().get("player1").isJsonNull()) {
                    JsonObject p1 = match.getAsJsonObject().get("player1").getAsJsonObject();
                    player1.setId(p1.get("id").getAsInt());
                    player1.setName(p1.get("display_name").getAsString());
                    player1.setSeed(p1.get("seed").getAsInt());
                    matchTmp.setP1Decided(true);
                    matchTmp.setP1Seed(player1.getSeed());


                } else {
                    matchTmp.setP1Decided(false);
                    if (!match.getAsJsonObject().get("player1_placeholder_text").isJsonNull()) {
                        player1.setName(match.getAsJsonObject().get("player1_placeholder_text").getAsString());
                        matchTmp.setP1PrereqText(match.getAsJsonObject().get("player1_placeholder_text").getAsString());
                    }
//                    if(match.getAsJsonObject().get("player1_prereq_identifier")!=null){
//                        //sets corresponding match as previous match
//                    }
                }
                if (!match.getAsJsonObject().get("player2").isJsonNull()) {
                    JsonObject p2 = match.getAsJsonObject().get("player2").getAsJsonObject();
                    player2.setId(p2.get("id").getAsInt());
                    player2.setName(p2.get("display_name").getAsString());
                    player2.setSeed(p2.get("seed").getAsInt());
                    matchTmp.setP2Decided(true);
                    matchTmp.setP2Seed(player2.getSeed());

                } else {
                    matchTmp.setP2Decided(false);
                    if (!match.getAsJsonObject().get("player2_placeholder_text").isJsonNull()) {
                        player2.setName(match.getAsJsonObject().get("player2_placeholder_text").getAsString());
                        matchTmp.setP2PrereqText(match.getAsJsonObject().get("player2_placeholder_text").getAsString());
                    }
                }
                if (!match.getAsJsonObject().get("player1_prereq_identifier").isJsonNull()) {
                    matchTmp.setP1PreviousIdentifier(match.getAsJsonObject().get("player1_prereq_identifier").getAsInt());
                }
                if (!match.getAsJsonObject().get("player2_prereq_identifier").isJsonNull()) {
                    matchTmp.setP2PreviousIdentifier(match.getAsJsonObject().get("player2_prereq_identifier").getAsInt());
                }
                matchTmp.setP1(player1);
                matchTmp.setP2(player2);
                matchTmp.setNumber(match.getAsJsonObject().get("identifier").getAsInt());

                roundTmp.getMatchList().add(matchTmp);

            }


        }
        Collections.sort(winnersRounds, (n1, n2) -> n1.getNumber() - n2.getNumber());
        Collections.sort(losersRounds, (n1, n2) -> n1.getNumber() - n2.getNumber());
        Collections.reverse(losersRounds);
        init();

        startBracketDisplay(1, 1, this.getContext());

    }

    public void startBracketDisplay(int numRoundW, int numRoundL, Context context) {
        if (numberOfParticipants < 2) {
            oneParticipantMessage.setVisibility(View.VISIBLE);
        } else {
            oneParticipantMessage.setVisibility(View.GONE);
            makeBracketDisplay(numRoundW, numRoundL, context);
            lv.hide();
            //Log.d("sBDbracketSize", String.valueOf(bracketWinners.getChildCount()));
            setMatchInfo(bracketWinners, winnersRounds);
            setMatchInfo(bracketLosers, losersRounds);
        }

    }

    public void makeBracketDisplay(int numRoundW, int numRoundL,
                                   Context context) {

        Log.d("number rounds", String.valueOf(winnersRounds.size() + " " + losersRounds.size()));

        //ConstraintLayout bracketRoot = findViewById(R.id.bracket_root);
        LinearLayout roundWinners = getView().findViewById(R.id.round_winners);
        //LinearLayout bracketWinners = findViewById(R.id.bracket_winners);
        // LinearLayout roundLosers = findViewById(R.id.round_losers);
        LinearLayout bracketLosers = getView().findViewById(R.id.bracket_losers);

        //empties the linear layouts. May not be needed
//        for (int i = 0; i < bracketRoot.getChildCount(); i++) {
//            ViewGroup child = (ViewGroup) bracketRoot.getChildAt(i);
//            child.removeAllViews();
//        }


        //bracket winners
        //adds winners rounds
        Log.d("qualifyRound", String.valueOf(qualifyRound));
        Log.d("postQual", String.valueOf(postQualRound));
        Log.d("winnersRounds", String.valueOf(winnersRounds.size()));


        setRoundHeaders(8);

        //int numberRound = qualifyRound == 0 ? postQualRound : postQualRound * 2;
        if (type.equals("single elimination")) {
            for (int i = 0; i < winnersRounds.size(); i++) {
                int numberRound = (i == 0 && qualifyRound != 0) ? postQualRound * 2 : winnersRounds.get(i).getMatchList().size();

                makeRound(numberRound, i, bracketWinners, 0);
                //the winners finals match doesnt have a connects after it, unless double elim
                //which is handled in makeGrandFinals
                if (i < winnersRounds.size() - 1) {
                    makeBracketConnectors(numberRound, i, bracketWinners, 0);
                }


            }
        } else if (type.equals("double elimination")) {
            for (int i = 0; i < winnersRounds.size() - 1; i++) {
                int numberRound = (i == 0 && qualifyRound != 0) ? postQualRound * 2 : winnersRounds.get(i).getMatchList().size();

                makeRound(numberRound, i, bracketWinners, 0);

                if (i < winnersRounds.size() - 2) {
                    makeBracketConnectors(numberRound, i, bracketWinners, 0);
                }

                if (i == winnersRounds.size() - 2) {

                    makeGrandFinals(i, bracketWinners);
                }
                //numberRound = numberRound / 2;

            }
        }
        //losers round
        //shift corrects space on losers bracket based on even or odd rounds
        int shift = losersRounds.size() % 2;
        for (int i = 0; i < losersRounds.size(); i++) {


            //int numberRound =(i==0) ? postQualRound : losersRounds.get(i).getMatchList().size();
            Log.d("int match result", String.valueOf((losersRounds.size() - i) / 2));
            int numberRound = (int) Math.pow(2, ((losersRounds.size() - 1) - i) / 2);

            makeRound(numberRound, (i + shift) / 2, bracketLosers, 0);
            if (i < losersRounds.size() - 1) {
                if (i == 0 && losersRounds.size() % 2 == 1) {
                    makeBracketConnectors(numberRound, (i + shift) / 2, bracketLosers, 0);
                } else if (losersRounds.get(i + 1).getMatchList().size() < losersRounds.get(i).getMatchList().size()) {
                    makeBracketConnectors(numberRound, (i + shift) / 2, bracketLosers, 0);
                } else {
                    makeBracketConnectors(numberRound, (i + shift) / 2, bracketLosers, 4);
                }
            }

        }


        if (qualifyRound != 0) {
            setUnusedMatchesInvisible(bracketWinners, winnersRounds, UNUSED_WINNERS);
        }
        Log.d("numPart", String.valueOf(numberOfParticipants));
        if (losersRounds.size() > 2) {
            if (losersRounds.size() % 2 == 1) {
                setUnusedMatchesInvisible(bracketLosers, losersRounds, UNUSED_LOSERS_ODD);
            } else {
                setUnusedMatchesInvisible(bracketLosers, losersRounds, UNUSED_LOSERS_EVEN);
            }
        }


    }

    /*
    Get list of all matches and set the bracket accordingly
     */

    public void setMatchInfo(LinearLayout bracket, ArrayList<Round> roundList) {
        Log.d("setMatchInfo", "This is being called");

        Log.d("bracketSize", String.valueOf(bracket.getChildCount()));

        for (int i = 0; i < roundList.size(); i++) {
            LinearLayout round = (LinearLayout) bracket.getChildAt(2 * i);
            Round roundInfo = roundList.get(i);
            //this case deals with the qualifying round, Player 1 of the first match of the first
            //round in an uneven bracket will never have the first seed
            if (i < roundList.size() - 1 || i == 0) {
                int tracker = 0;
                for (int j = 0; tracker < roundInfo.getMatchList().size() && j < round.getChildCount(); j++) {
                    if (round.getChildAt(j) instanceof ConstraintLayout && round.getChildAt(j).getVisibility() == View.VISIBLE) {
                        setMatchView((ConstraintLayout) round.getChildAt(j), roundInfo.getMatchList().get(tracker));
                        tracker++;
                    }

                }
            } else {
                //grands and grands reset
                if (roundInfo.getMatchList().size() == 1) {
                    setMatchView((ConstraintLayout) round.getChildAt(1), roundInfo.getMatchList().get(0));
                } else {
                    LinearLayout reset = (LinearLayout) bracket.getChildAt(bracket.getChildCount() - 1);
                    setMatchView((ConstraintLayout) round.getChildAt(1), roundInfo.getMatchList().get(0));
                    setMatchView((ConstraintLayout) reset.getChildAt(1), roundInfo.getMatchList().get(1));
                }
            }
            //even bracket
//            else if (roundInfo.getNumber() == 1 && roundInfo.getMatchList().get(0).getP1Seed() == 1) {
//                for (int j = 0; j < roundInfo.getMatchList().size(); j++) {
//
//                    setMatchView((ConstraintLayout) round.getChildAt(j * 2), roundInfo.getMatchList().get(j));
//                }
//            }
//              else {
//                for (int j = 0; j < roundInfo.getMatchList().size(); j++) {
//                    if (roundInfo.getNumber() != numRoundsWinners) {
//                        setMatchView((ConstraintLayout) round.getChildAt((j * 2) + 1), roundInfo.getMatchList().get(j));
//                    } else {
//                        //if single elim then you are at the final round
//                        if (type.equals("single elimination")) {
//                            setMatchView((ConstraintLayout) round.getChildAt(1), roundInfo.getMatchList().get(0));
//                        }
//                        //if double elim then you are at grand finals, according to the API grand
//                        //finals and grand finals reset are considered the same round
//                        else if (type.equals("double elimination")) {
//                            LinearLayout reset = (LinearLayout) bracket.getChildAt(bracket.getChildCount() - 1);
//
//                            setMatchView((ConstraintLayout) round.getChildAt(1), roundInfo.getMatchList().get(0));
//                            setMatchView((ConstraintLayout) reset.getChildAt(1), roundInfo.getMatchList().get(1));
//                        }
//                    }
//                }
//            }
        }

        bv.invalidate();
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


    //initializes values needed to make bracket dislay
    public void init() {

        numRoundsWinners = winnersRounds.size();
        numRoundsLosers = losersRounds.size();

        if (type.equals("single elimination")) {
            if (winnersRounds.size() == 1) {
                postQualRound = 1;
            } else {
                if (winnersRounds.get(0).getMatchList().get(0).getP1Seed() != 1) {
                    qualifyRound = winnersRounds.get(0).getMatchList().size();
                    postQualRound = winnersRounds.get(1).getMatchList().size();

                } else {
                    postQualRound = winnersRounds.get(0).getMatchList().size();
                }
            }
        } else if (type.equals("double elimination")) {
            if (winnersRounds.get(0).getMatchList().get(0).getP1Seed() != 1) {
                qualifyRound = winnersRounds.get(0).getMatchList().size();
                postQualRound = winnersRounds.get(1).getMatchList().size();
            } else {
                postQualRound = winnersRounds.get(0).getMatchList().size();
            }
        }


    }


    public void makeGrandFinals(int multiplier, ViewGroup vg) {

        makeBracketConnectors(1, multiplier, vg, 1);
        makeRound(1, multiplier, vg, 1);
        makeBracketConnectors(1, multiplier, vg, 2);
        makeRound(1, multiplier, vg, 1);


    }

    public void makeRound(int numMatches, int multiplier, ViewGroup vg, int modifier) {

        Log.d("makeRoundCalled", String.valueOf(modifier));
        LinearLayout matches = new LinearLayout(this.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        matches.setLayoutParams(layoutParams);
        matches.setOrientation(LinearLayout.VERTICAL);
        matches.setGravity(Gravity.CENTER);


        //for every round after round 1 there is a space added before the matches for alignment
        if (multiplier != 0 || modifier == 1) {
            Space space = new Space(this.getContext());
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + (modifier == 0 ? 0 : 1))));
            matches.addView(space);
        }

        //creates the matches
        for (int j = 0; j < numMatches; j++) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            ConstraintLayout match = (ConstraintLayout) inflater.inflate(R.layout.match, null);

            Space space = new Space(this.getContext());
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

        Log.d("makeBCCalled", String.valueOf(modifier));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout bracketConnector = new LinearLayout(this.getContext());
        bracketConnector.setLayoutParams(layoutParams);
        bracketConnector.setOrientation(LinearLayout.VERTICAL);

        //for non grand finals rounds
        if (modifier == 0) {
            for (int i = 0; i < numMatches / 2; i++) {
                Space space = new Space(this.getContext());
                Space space2 = new Space(this.getContext());
                Space space3 = new Space(this.getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
                space2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
                space3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * 2));

                bracketConnectorView bcv = new bracketConnectorView(this.getContext(), null
                        , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                        , bracketConnectorView.MODE_TOP
                        , "");
                bracketConnectorView bcv2 = new bracketConnectorView(this.getContext(), null
                        , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                        , bracketConnectorView.MODE_BOTTOM
                        , "");


                bracketConnector.addView(space);
                bracketConnector.addView(bcv);
                bracketConnector.addView(bcv2);
                bracketConnector.addView(space2);
                bracketConnector.addView(space3);

            }
        }
        //grandfinals
        else if (modifier == 1) {
            Space space = new Space(this.getContext());
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
            bracketConnectorView bcv = new bracketConnectorView(this.getContext(), null, mHeightUnit, bracketConnectorView.MODE_TOP, "");
            bracketConnector.addView(space);
            bracketConnector.addView(bcv);
        }
        //grandfinals reset
        else if (modifier == 2) {
            Space space = new Space(this.getContext());
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + 1)));
            bracketConnectorView bcv = new bracketConnectorView(this.getContext(), null, mHeightUnit * 2, bracketConnectorView.MODE_MIDDLE, "");
            bracketConnector.addView(space);
            bracketConnector.addView(bcv);
        }
        //losers rounds
        else if (modifier == 4) {

            if (multiplier != 0) {
                Space space = new Space(this.getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2)));
                bracketConnector.addView(space);
            }
            for (int i = 0; i < numMatches; i++) {
                bracketConnectorView bcv = new bracketConnectorView(this.getContext(), null, mHeightUnit * 2, bracketConnectorView.MODE_MIDDLE, "");
                Space space = new Space(this.getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        (mHeightUnit * ((int) Math.pow(2, multiplier + 2) - 2))));
                bracketConnector.addView(bcv);
                bracketConnector.addView(space);


            }
        }
        vg.addView(bracketConnector);
    }


    public void setRoundHeaders(int numRounds) {
        LinearLayout roundWinners = getView().findViewById(R.id.round_winners);
        LinearLayout roundLosers = getView().findViewById(R.id.round_losers);

        ViewGroup.MarginLayoutParams roundHeaderLayoutParams = new ViewGroup.MarginLayoutParams(
                getResources().getDimensionPixelSize(R.dimen.match_width) + getResources().getDimensionPixelSize(R.dimen.bcv_width), getResources().getDimensionPixelSize(R.dimen.round_header_height));
        roundHeaderLayoutParams.setMargins(0, 0, 0, 0);


        //adds round headers
        for (int i = 0; i < winnersRounds.size(); i++) {
            TextView roundNumber = new TextView(this.getContext(), null, 0, R.style.menu_round);
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            roundNumber.setText(winnersRounds.get(i).getName());
            roundWinners.addView(roundNumber);
        }
        for (int i = 0; i < losersRounds.size(); i++) {
            TextView roundNumber = new TextView(this.getContext());
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            roundNumber.setText(losersRounds.get(i).getName());
            roundLosers.addView(roundNumber);
        }

    }


    private void setUnusedMatchesInvisible(LinearLayout linLayout, ArrayList<Round> roundList, int mode) {

        ViewGroup QR = (LinearLayout) linLayout.getChildAt(0);
        ViewGroup QRBC = (LinearLayout) linLayout.getChildAt(1);
        ArrayList<Match> byeRound = roundList.get(1).getMatchList();
        //winners round
        if (mode == 0) {

            for (int i = 0; i < byeRound.size(); i++) {
                Log.d("unusedIndex", String.valueOf(i));
                Log.d("p1prev", String.valueOf(byeRound.get(i).getP1PreviousIdentifier()));
                Log.d("p2prev", String.valueOf(byeRound.get(i).getP2PreviousIdentifier()));
                if (byeRound.get(i).getP1PreviousIdentifier() == 0) {
                    QR.getChildAt(4 * i).setVisibility(View.INVISIBLE);
                    QRBC.getChildAt(5 * i + 1).setVisibility(View.INVISIBLE);
                }
                if (byeRound.get(i).getP2PreviousIdentifier() == 0) {
                    QR.getChildAt(4 * i + 2).setVisibility(View.INVISIBLE);
                    QRBC.getChildAt(5 * i + 2).setVisibility(View.INVISIBLE);
                }
            }
        } else if (mode == 1) {
            for (int i = 0; i < byeRound.size(); i++) {

                if (!(byeRound.get(i).getP1PrereqText().equals(""))) {
                    QR.getChildAt(4 * i).setVisibility(View.INVISIBLE);
                    QRBC.getChildAt(5 * i + 1).setVisibility(View.INVISIBLE);
                }
                if (!(byeRound.get(i).getP2PrereqText().equals(""))) {
                    QR.getChildAt(4 * i + 2).setVisibility(View.INVISIBLE);
                    QRBC.getChildAt(5 * i + 2).setVisibility(View.INVISIBLE);
                }
            }
        } else if (mode == 2) {
            for (int i = 0; i < byeRound.size(); i++) {

                if (!(byeRound.get(i).getP2PrereqText().equals(""))) {
                    QR.getChildAt(2 * i).setVisibility(View.INVISIBLE);
                    QRBC.getChildAt(2 * i).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void setGrandsResetInvis() {
        if (type.equals(DOUBLE_ELIM)) {
            bracketWinners.getChildAt(bracketWinners.getChildCount() - 1).setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BracketListener) {
            listener = (BracketListener) context;
            listener.getStuff();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BracketListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public LinearLayout getRoundWinners() {
        return roundWinners;
    }

    public LinearLayout getRoundLosers() {
        return roundLosers;
    }

    public LinearLayout getBracketWinners() {
        return bracketWinners;
    }

    public LinearLayout getBracketLosers() {
        return bracketLosers;
    }
}
