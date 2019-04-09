package ferox.bracket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
    private TextView oneParticipantMessage;
    private int postQualRound;
    private int qualifyRound;
    private int numOfLR1;
    private int mHeightUnit;
    private int mWidthUnit;
    private int numRoundsWinners;
    private int numRoundsLosers;

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    ArrayList<Round> winnersRounds;
    ArrayList<Round> losersRounds;

    String url;
    String type;
    String state;
    int numberOfParticipants;


    private BracketView bv;
    private LoadingView lv;
    private LinearLayout roundWinners;
    private LinearLayout roundLosers;
    private LinearLayout bracketWinners;
    private LinearLayout bracketLosers;


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

        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height);
        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);


        winnersRounds = new ArrayList<>();
        losersRounds = new ArrayList<>();

        oneParticipantMessage = v.findViewById(R.id.one_participant_message);

        url = intent.getStringExtra("tournamentURL");
        type = intent.getStringExtra("tournamentType");
        numberOfParticipants = intent.getIntExtra("tournamentSize", 0);


        ChallongeRequests.sendRequest(response -> getTournamentInfo(response), ChallongeRequests.jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(url));

        return v;
    }


    private void getTournamentInfo(String jsonString) {
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
        //losers rounds are denoted with a negative number, Round -x is Loser's Round X
        //Since the rounds have been sorted by number value they need to be reversed to be order by actual round
        Collections.reverse(losersRounds);
        init();

        startBracketDisplay();

    }

    /*
    startBracketDisplay - if tournament has less than 2 members method will show a message stating
    bracket will be shown when there are enough members
     */
    private void startBracketDisplay() {
        if (numberOfParticipants < 2) {
            oneParticipantMessage.setVisibility(View.VISIBLE);
        } else {
            oneParticipantMessage.setVisibility(View.GONE);
            makeBracketDisplay();
            lv.hide();
            //Log.d("sBDbracketSize", String.valueOf(bracketWinners.getChildCount()));
            //setMatchInfo(bracketWinners, winnersRounds);
//            setMatchInfo(bracketLosers, losersRounds);
        }

    }

    /*
    Once there are enough members in the tournament to make a bracket, this method will construct
    the round headers and matches
     */
    private void makeBracketDisplay() {


        if (!type.equals(ROUND_ROBIN)) {
            setRoundHeaders(8);
        }

        makeWinners();
        makeLosers();


        //The round of the bracket is initially constructed as if it is a "full" round, and then unused matches are set inivisible
//        if (qualifyRound != 0) {
////            setUnusedMatchesInvisible(bracketWinners, winnersRounds, UNUSED_WINNERS);
//        }
//        Log.d("numPart", String.valueOf(numberOfParticipants));
//        if (losersRounds.size() > 2) {
//            if (losersRounds.size() % 2 == 1) {
//                setUnusedMatchesInvisible(bracketLosers, losersRounds, UNUSED_LOSERS_ODD);
//            } else {
//                setUnusedMatchesInvisible(bracketLosers, losersRounds, UNUSED_LOSERS_EVEN);
//            }
//        }
//
//
    }

    /*
    Get list of all matches and set the bracket accordingly
     */

    private void setMatchInfo(LinearLayout bracket, ArrayList<Round> roundList) {

        //TODO this shit gotta be remade
        Log.d("setMatchInfo", "This is being called");

        Log.d("bracketSize", String.valueOf(bracket.getChildCount()));
        if (type.equals("round robin")) {
            return;
        }

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

    private void setMatchView(ConstraintLayout match, Match matchInfo) {


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


    //initializes values needed to make bracket display
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


    private void makeGrandFinals(int multiplier, ViewGroup vg) {

        makeBracketConnectors(1, multiplier, vg, 1);
        makeRound(1, multiplier, vg, 1);
        makeBracketConnectors(1, multiplier, vg, 2);
        makeRound(1, multiplier, vg, 1);


    }
/*
Method makeRound
modifier -method to deal with grand finals
TODO method could probably be refactored into two different methods to make it more elegant
 */

    private void makeRound(int numMatches, int multiplier, ViewGroup vg, int modifier) {

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

    private void makeWinners() {

        //total number of rounds minus grand finals which is constructed separately
        int totalRounds = type.equals(DOUBLE_ELIM) ? winnersRounds.size() - 1 : winnersRounds.size();

        if (type.equals(SINGLE_ELIM) || type.equals(DOUBLE_ELIM)) {
            for (int i = 0; i < totalRounds; i++) {
                //make a Linear Layout to hold each round
                LinearLayout roundLayout = new LinearLayout(getContext());
                LinearLayout bcvLayout = new LinearLayout(getContext());
                roundLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                bcvLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                roundLayout.setOrientation(LinearLayout.VERTICAL);
                bcvLayout.setOrientation(LinearLayout.VERTICAL);
                //make space that goes before each round(2^(n-1)-1, n = round)
                roundLayout.addView(makeSpaceComponent(((Math.pow(2, winnersRounds.get(i).getNumber() - 1)) - 1)));

                //number of matches in "full" round, not all match slots will be used in which case they will be set invisible
                int numMatches = (int) Math.pow(2, totalRounds - (i + 1));

                for (int j = 0; j < numMatches; j++) {
                    //make  match
                    roundLayout.addView(makeMatchComponent());
                    //then space((2^n)-1)
                    if (j < numMatches - 1) {
                        roundLayout.addView(makeSpaceComponent(Math.pow(2, i + 1) - 1));
                    }
                }


                for (int j = 0; j < numMatches / 2; j++) {
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i) - 0.5));
                    bcvLayout.addView(makeBCVComponent(i, bracketConnectorView.MODE_TOP));
                    bcvLayout.addView(makeBCVComponent(i, bracketConnectorView.MODE_BOTTOM));
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i) - 0.5));
                    bcvLayout.addView(makeSpaceComponent(1));
                }


                bracketWinners.addView(roundLayout);

                if (bcvLayout.getChildCount() != 0) {
                    bracketWinners.addView(bcvLayout);
                }
            }
        } else if (type.equals(ROUND_ROBIN)) {
            for (int i = 0; i < winnersRounds.size(); i++) {
                bracketWinners.setOrientation(LinearLayout.VERTICAL);
                LayoutInflater inflater = getLayoutInflater();
                TextView round = (TextView) inflater.inflate(R.layout.menu_list_item, null);
                round.setText("Round" + String.valueOf(i + 1));
                LinearLayout roundLayout = new LinearLayout(getContext());
                roundLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < winnersRounds.get(i).getMatchList().size(); j++) {
                    roundLayout.addView(makeMatchComponent());
                    Space space = makeSpaceComponent(1);
                    space.getLayoutParams().width = mWidthUnit / 2;
                    roundLayout.addView(space);
                }
                bracketWinners.addView(round);
                bracketWinners.addView(roundLayout);
            }
        }


    }

    private void makeLosers() {
        int totalRounds = losersRounds.size();

        //This double is for helping keep track of the number of matches to add to each losers round
        int shift = (totalRounds % 2);

        for (int i = totalRounds; i > 0; i--) {
            LinearLayout roundLayout = new LinearLayout(getContext());
            roundLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            roundLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout bcvLayout = new LinearLayout(getContext());
            bcvLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            bcvLayout.setOrientation(LinearLayout.VERTICAL);
            int preRoundSpaceSize = (i + shift - 1) / 2;

            //makes space that offsets the height of subsequent rounds
            roundLayout.addView(makeSpaceComponent(Math.pow(2, preRoundSpaceSize) - 1));


            //number of matches in "full" round, not all match slots will be used in which case they will be set invisible
            int numMatches = (int) Math.pow(2, (totalRounds - i) / 2);

            for (int j = 0; j < numMatches; j++) {
                roundLayout.addView(makeMatchComponent());
                if (j < numMatches - 1) {
                    roundLayout.addView(makeSpaceComponent(Math.pow(2, (i / 2) + shift) - 1));
                }
            }


            if (i % 2 == 1) {
                for (int j = 0; j < numMatches / 2; j++) {
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i / 2) - 0.5));
                    bcvLayout.addView(makeBCVComponent(i / 2, bracketConnectorView.MODE_TOP));
                    bcvLayout.addView(makeBCVComponent(i / 2, bracketConnectorView.MODE_BOTTOM));
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i / 2) - 0.5));
                    bcvLayout.addView(makeSpaceComponent(1));
                }
            } else {
                bcvLayout.addView(makeSpaceComponent(Math.pow(2, preRoundSpaceSize) - 1));
                for (int j = 0; j < numMatches; j++) {
                    bcvLayout.addView(makeBCVComponent(0, bracketConnectorView.MODE_MIDDLE));
                    if (j < numMatches - 1) {
                        bcvLayout.addView(makeSpaceComponent(Math.pow(2, (i / 2) + shift) - 1));
                    }
                }
            }


            if (bracketLosers.getChildCount() != 0) {
                bracketLosers.addView(bcvLayout, 0);
            }
            bracketLosers.addView(roundLayout, 0);

        }
    }

    // get match layout
    private ConstraintLayout makeMatchComponent() {
        LayoutInflater inflater = getLayoutInflater();
        assert inflater != null;
        ConstraintLayout match = (ConstraintLayout) inflater.inflate(R.layout.match, null);
        return match;
    }

    //get space
    private Space makeSpaceComponent(double heightMultiplier) {
        Space space = new Space(getContext());
        space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (mHeightUnit * heightMultiplier)));
        return space;
    }

    //get bracketConnectorView
    private bracketConnectorView makeBCVComponent(int heightMultiplier, int mode) {
        bracketConnectorView bcv = new bracketConnectorView(getContext(), null, mHeightUnit * (int) Math.pow(2, heightMultiplier), mode, null);
        return bcv;
    }

    private void makeBracketConnectors(int numMatches, int multiplier, ViewGroup vg, int modifier) {

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

    //Constructs round headers
    private void setRoundHeaders(int numRounds) {
        //TODO needs to set names from the round names themselves(Semifinals, Grands Etc)
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
            TextView roundNumber = new TextView(this.getContext(), null, 0, R.style.menu_round);
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

    private void setUnusedMatchesInvisible2() {
        
    }

    public void setGrandsResetInvis() {
        if (type.equals(DOUBLE_ELIM)) {
            bracketWinners.getChildAt(bracketWinners.getChildCount() - 1).setVisibility(View.INVISIBLE);
        }
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
