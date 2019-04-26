package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
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
    final static String SWISS = "swiss";
    final static String AWAITING_REVIEW = "awaiting_review";
    final static String UNDERWAY = "underway";
    final static String PENDING = "pending";
    final static String COMPLETE = "complete";

    TextView width;
    TextView height;
    TextView maxdx;
    TextView maxdy;
    TextView dx;
    TextView dy;
    TextView scale;

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
    Tournament currentTournament;

    ArrayList<Round> winnersRounds;
    ArrayList<Round> losersRounds;

    String url;
    String type;
    String state;
    int numberOfParticipants;


    private MyZoomLayout zoomLayout;
    private LoadingView lv;
    private LinearLayout roundWinners;
    private LinearLayout roundLosers;
    private LinearLayout bracketWinners;
    private LinearLayout bracketLosers;

    private NestedScrollView nestedScrollView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_bracket, container, false);
        currentTournament = new Tournament();

        zoomLayout = v.findViewById(R.id.bracket_zoom_layout);
//
//        width = v.findViewById(R.id.bracket_width);
//        height = v.findViewById(R.id.bracket_height);
//        maxdx = v.findViewById(R.id.bracket_max_dx);
//        maxdy = v.findViewById(R.id.bracket_max_dy);
//        dx = v.findViewById(R.id.bracket_dx);
//        dy = v.findViewById(R.id.bracket_dy);
//        scale = v.findViewById(R.id.bracket_scale);


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


        ChallongeRequests.sendRequest(response -> getTournamentRoundInfo(response), ChallongeRequests.jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(url));


//        int delay = 100; //milliseconds
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                //do something
//                width.setText(String.valueOf(zoomLayout.child().getWidth()));
//                height.setText(String.valueOf(zoomLayout.child().getHeight()));
//                maxdx.setText(String.valueOf(zoomLayout.minDx));
//                maxdy.setText(String.valueOf(zoomLayout.minDy));
//                dx.setText(String.valueOf(zoomLayout.dx));
//                dy.setText(String.valueOf(zoomLayout.dy));
//                scale.setText(String.valueOf(zoomLayout.scale));
//
//                handler.postDelayed(this, delay);
//            }
//        }, delay);


        return v;
    }


    private void getTournamentInfo2(String jsonString) {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement tournamentInfo = jsonParser.parse(jsonString);

    }

    private void getTournamentRoundInfo(String jsonString) {
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
            roundTmp.setTitle(name);
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
        setUnusedMatchesInvisible();
        setMatchInfo();

    }

    private void setMatchInfo() {
        //double/single elim matches
        if (type.equals(DOUBLE_ELIM) || type.equals(SINGLE_ELIM)) {
            //get number of rounds minus double elim grands
            int numberOfRoundsWinners = type.equals(DOUBLE_ELIM) ? winnersRounds.size() - 1 : winnersRounds.size();

            //set winners match info
            for (int i = 0; i < numberOfRoundsWinners; i++) {
                ArrayList<Match> matches = winnersRounds.get(i).getMatchList();
                LinearLayout round = (LinearLayout) bracketWinners.getChildAt(2 * i);
                int iterator = 0;
                for (int j = 0; j < round.getChildCount(); j++) {
                    //must only set info for the visible constraintlayout/ignore unused matches
                    if (round.getChildAt(j) instanceof ConstraintLayout && round.getChildAt(j).getVisibility() == View.VISIBLE) {
                        setMatchView((ConstraintLayout) round.getChildAt(j), matches.get(iterator));
                        iterator++;
                    }
                }
            }

            //sets grand finals in double elim
            //magic numbers are to get the specific location of the grand finals matches in the layout
            // an iterating method could be used just to eliminate use of magic numbers but these locations
            //should be constant across any double elim bracket constructed with this program
            if (type.equals(DOUBLE_ELIM)) {
                LinearLayout GF1 = (LinearLayout) bracketWinners.getChildAt(bracketWinners.getChildCount() - 3);
                LinearLayout GF2 = (LinearLayout) bracketWinners.getChildAt(bracketWinners.getChildCount() - 1);
                ConstraintLayout GF1Match = (ConstraintLayout) GF1.getChildAt(1);
                ConstraintLayout GF2Match = (ConstraintLayout) GF2.getChildAt(1);
                setMatchView(GF1Match, winnersRounds.get(winnersRounds.size() - 1).getMatchList().get(0));
                setMatchView(GF2Match, winnersRounds.get(winnersRounds.size() - 1).getMatchList().get(1));
            }

            //set losers match info
            for (int i = 0; i < losersRounds.size(); i++) {
                ArrayList<Match> matches = losersRounds.get(i).getMatchList();
                LinearLayout round = (LinearLayout) bracketLosers.getChildAt(2 * i);
                int iterator = 0;
                for (int j = 0; j < round.getChildCount(); j++) {
                    //must only set info for the visible constraintlayout/ignore unused matches
                    if (round.getChildAt(j) instanceof ConstraintLayout && round.getChildAt(j).getVisibility() == View.VISIBLE) {
                        setMatchView((ConstraintLayout) round.getChildAt(j), matches.get(iterator));
                        iterator++;
                    }
                }
            }

        } else if (type.equals(ROUND_ROBIN) || type.equals(SWISS)) {
            for (int i = 0; i < winnersRounds.size(); i++) {
                ArrayList<Match> matches = winnersRounds.get(i).getMatchList();
                LinearLayout round = (LinearLayout) bracketWinners.getChildAt(2 * i + 1);
                int iterator = 0;
                for (int j = 0; j < round.getChildCount(); j++) {
                    //must only set info for the visible constraintlayout/ignore unused matches
                    if (round.getChildAt(j) instanceof ConstraintLayout) {
                        setMatchView((ConstraintLayout) round.getChildAt(j), matches.get(iterator));
                        iterator++;
                    }
                }
            }
        }


    }

    private void setMatchView(ConstraintLayout match, Match matchInfo) {


        TextView matchNumber = match.findViewById(R.id.matchNumber);

        matchNumber.setOnClickListener(v -> Toast.makeText(getContext(), matchNumber.getText(), Toast.LENGTH_SHORT).show());

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


    private void makeGrandFinals() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout BCV1 = new LinearLayout(getContext());
        BCV1.setLayoutParams(layoutParams);
        BCV1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout grandFinals = new LinearLayout(getContext());
        grandFinals.setLayoutParams(layoutParams);
        grandFinals.setOrientation(LinearLayout.VERTICAL);
        LinearLayout BCV2 = new LinearLayout(getContext());
        BCV2.setLayoutParams(layoutParams);
        BCV2.setOrientation(LinearLayout.VERTICAL);
        LinearLayout grandFinalsReset = new LinearLayout(getContext());
        grandFinalsReset.setLayoutParams(layoutParams);
        grandFinalsReset.setOrientation(LinearLayout.VERTICAL);

        BCV1.addView(makeSpaceComponent(Math.pow(2, winnersRounds.size() - 2) - 0.5));
        grandFinals.addView(makeSpaceComponent(Math.pow(2, winnersRounds.size() - 2)));
        BCV2.addView(makeSpaceComponent(Math.pow(2, winnersRounds.size() - 2)));
        grandFinalsReset.addView(makeSpaceComponent(Math.pow(2, winnersRounds.size() - 2)));

        BCV1.addView(makeBCVComponent(0, bracketConnectorView.MODE_TOP));
        grandFinals.addView(makeMatchComponent());
        BCV2.addView(makeBCVComponent(0, bracketConnectorView.MODE_MIDDLE));
        grandFinalsReset.addView(makeMatchComponent());

        bracketWinners.addView(BCV1);
        bracketWinners.addView(grandFinals);
        bracketWinners.addView(BCV2);
        bracketWinners.addView(grandFinalsReset);
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
            //double elimination grand finals

            if (type.equals(DOUBLE_ELIM)) {

                makeGrandFinals();
            }

        } else if (type.equals(ROUND_ROBIN) || type.equals(SWISS)) {
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
        double shift = (totalRounds % 2) == 0 ? 0.5 : 0;


        for (int i = totalRounds; i > 0; i--) {
            double multiplier = Math.floor((i / 2.0) + 1 + shift);
            LinearLayout roundLayout = new LinearLayout(getContext());
            roundLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            roundLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout bcvLayout = new LinearLayout(getContext());
            bcvLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            bcvLayout.setOrientation(LinearLayout.VERTICAL);


            //makes space that offsets the height of subsequent rounds
            roundLayout.addView(makeSpaceComponent(Math.pow(2, multiplier - 1) - 1));


            //number of matches in "full" round, not all match slots will be used in which case they will be set invisible
            int numMatches = (int) Math.pow(2, (totalRounds - i) / 2);

            for (int j = 0; j < numMatches; j++) {
                roundLayout.addView(makeMatchComponent());
                if (j < numMatches - 1) {
                    roundLayout.addView(makeSpaceComponent((Math.pow(2, multiplier)) - 1));
                }
            }

            //Bracket connector views
            if ((totalRounds - i) % 2 == 0) {
                for (int j = 0; j < numMatches / 2; j++) {
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i / 2) - 0.5));
                    bcvLayout.addView(makeBCVComponent(i / 2, bracketConnectorView.MODE_TOP));
                    bcvLayout.addView(makeBCVComponent(i / 2, bracketConnectorView.MODE_BOTTOM));
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i / 2) - 0.5));
                    bcvLayout.addView(makeSpaceComponent(1));
                }
            } else {
                //preround space for BCV
                bcvLayout.addView(makeSpaceComponent(Math.pow(2, multiplier - 1) - 1));
                for (int j = 0; j < numMatches; j++) {
                    bcvLayout.addView(makeBCVComponent(0, bracketConnectorView.MODE_MIDDLE));
                    if (j < numMatches - 1) {
                        bcvLayout.addView(makeSpaceComponent(Math.pow(2, multiplier) - 1));
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

    //TODO this takes in an int multiplier whereas the other two take a double of absolute height
    //get bracketConnectorView
    private bracketConnectorView makeBCVComponent(int heightMultiplier, int mode) {
        bracketConnectorView bcv = new bracketConnectorView(getContext(), null, mHeightUnit * (int) Math.pow(2, heightMultiplier), mode, null);
        return bcv;
    }

    private void makeBracketConnectors(int numMatches, int multiplier, ViewGroup vg, int modifier) {


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
            roundNumber.setText(winnersRounds.get(i).getTitle());
            roundWinners.addView(roundNumber);
        }
        for (int i = 0; i < losersRounds.size(); i++) {
            TextView roundNumber = new TextView(this.getContext(), null, 0, R.style.menu_round);
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            roundNumber.setText(losersRounds.get(i).getTitle());
            roundLosers.addView(roundNumber);
        }

    }


    private void setUnusedMatchesInvisible() {


        //winners
        if ((type.equals(DOUBLE_ELIM) || type.equals(SINGLE_ELIM)) && winnersRounds.size() > 1) {
            LinearLayout firstRoundLayout = (LinearLayout) bracketWinners.getChildAt(0);
            LinearLayout firstRoundBC = (LinearLayout) bracketWinners.getChildAt(1);
            Round secondRound = winnersRounds.get(1);
            ArrayList<Match> secondRoundMatches = secondRound.getMatchList();
            for (int i = 0; i < secondRoundMatches.size(); i++) {

                //TODO Possible change so that the match will go through the layout until its gotten to the i matchLayout, gets rid of magic number but loses out on random access
                //Or just refactor these numbers to static final ints
                if (secondRoundMatches.get(i).getP1PreviousIdentifier() == 0) {
                    //index of of matchLayout and BCV corresponding to current second round index
                    int matchLayoutIndex = (4 * i) + 1;
                    int bcvIndex = (5 * i) + 1;
                    firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                    firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                }
                if (secondRoundMatches.get(i).getP2PreviousIdentifier() == 0) {
                    int matchLayoutIndex = (4 * i) + 3;
                    int bcvIndex = (5 * i) + 2;
                    firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                    firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                }
            }
        }

        //losers
        if ((!type.equals(ROUND_ROBIN)) && losersRounds.size() > 1) {
            LinearLayout firstRoundLayout = (LinearLayout) bracketLosers.getChildAt(0);
            LinearLayout firstRoundBC = (LinearLayout) bracketLosers.getChildAt(1);
            LinearLayout secondRoundLayout = (LinearLayout) bracketLosers.getChildAt(2);
            Round secondRound = losersRounds.get(1);
            ArrayList<Match> secondRoundMatches = secondRound.getMatchList();


            //handles two scenarios where if first round match capacity is larger than second round
            if (firstRoundLayout.getChildCount() > secondRoundLayout.getChildCount()) {
                for (int i = 0; i < secondRoundMatches.size(); i++) {

                    //if a round 2 losers match has prereq text then it means that the the players comes froms winners
                    //therefore the unused match slot in LR1 should bet set invisible
                    if (!secondRoundMatches.get(i).getP1PrereqText().equals("")) {
                        //index of of matchLayout and BCV corresponding to current second round index
                        int matchLayoutIndex = (4 * i) + 1;
                        int bcvIndex = (5 * i) + 1;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                    if (!secondRoundMatches.get(i).getP2PrereqText().equals("")) {
                        int matchLayoutIndex = (4 * i) + 3;
                        int bcvIndex = (5 * i) + 2;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }

            } else {
                for (int i = 0; i < secondRoundMatches.size(); i++) {
                    //if a round 2 losers match has prereq text then it means that the the players comes froms winners
                    //therefore the unused match slot in LR1 should bet set invisible


                    if (!secondRoundMatches.get(i).getP2PrereqText().equals("")) {
                        int matchLayoutIndex = (2 * i) + 1;
                        int bcvIndex = (2 * i) + 1;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }
            }

        }


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
