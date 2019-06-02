package ferox.bracket.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.CustomClass.StringJsonArrayPair;
import ferox.bracket.CustomViews.BracketConnectorView;
import ferox.bracket.CustomViews.LoadingView;
import ferox.bracket.CustomViews.MatchReportButton;
import ferox.bracket.CustomViews.MatchView;
import ferox.bracket.CustomViews.MyZoomLayout;
import ferox.bracket.CustomViews.RoundLayout;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Match;
import ferox.bracket.Tournament.Participant;
import ferox.bracket.Tournament.Round;
import ferox.bracket.Tournament.Tournament;

public class BracketFragment extends Fragment {


    private final static String SINGLE_ELIM = "single elimination";
    private final static String DOUBLE_ELIM = "double elimination";
    private final static String ROUND_ROBIN = "round robin";
    private final static String SWISS = "swiss";
    /**
     * All the tournaments matches have been completed and is now ready to be ended
     */
    private final static String AWAITING_REVIEW = "awaiting_review";
    /**
     * Tournament has begun but still has pending matches
     */
    private final static String UNDERWAY = "underway";
    /**
     * Tournament has not begun yet
     */
    private final static String PENDING = "pending";
    /**
     * Tournament has been ended
     */
    private final static String COMPLETE = "complete";
    /**
     * When start_at has been set tournament enters check in state
     */
    private final static String CHECKING_IN = "checking_in";
    /**
     * Tournament check-ins have been processed
     */
    private final static String CHECKED_IN = "checked_in";

    private final static String GRANDS_DEFAULT = "";
    private final static String GRANDS_SINGLE_MATCH = "single match";
    private final static String GRANDS_SKIP = "skip";

    private final static String REOPEN = "Reopen";

//    TextView width;
//    TextView height;
//    TextView maxdx;
//    TextView maxdy;
//    TextView dx;
//    TextView dy;
//    TextView scale;
//
//    TextView yposition;
//    TextView bracketHeight;
//    TextView screenHeight;

    private TextView oneParticipantMessage;
    private Button actionButton;
    private ImageButton refreshButton;

    private int numOfLR1;
    private int mHeightUnit;
    private int mWidthUnit;
    private int countedParticipants;
    /**
     * When refreshing a tournament if format and # of participants is the same then only update<br>
     * view else then remake layout
     */
    private int oldParticipantCount;
    /**
     * When refreshing a tournament if format and # of participants is the same then only update<br>
     * view else then remake layout
     */
    private String oldType;

    /**
     * When refreshing a tournament if format and # of participants is the same then only update<br>
     * view else then remake layout
     */
    private boolean oldHoldThirdPlace;

    /**
     * When refreshing a tournament if format and # of participants is the same then only update<br>
     * view else then remake layout
     */
    private String oldGrandsModifier;


    String lastJson;


    ArrayList<Round> winnersRounds;
    ArrayList<Round> losersRounds;
    ArrayList<Round> roundLabelsW;
    ArrayList<Round> roundLabelsL;

    private ArrayList<RoundLayout> roundLayoutsWinners;
    private ArrayList<RoundLayout> roundLayoutsLosers;

    //String url;
    //String subdomain;
    //String tournament.getType();
    String state;
    //int participantCount;


    private MyZoomLayout zoomLayout;
    private LoadingView lv;

    private LinearLayout bracketWinners;
    private LinearLayout bracketLosers;

    private Tournament tournament;

    private Match lastSearchedMatch;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_bracket, container, false);

        lastJson = "";


        zoomLayout = v.findViewById(R.id.bracket_zoom_layout);
//
//        width = v.findViewById(R.id.bracket_width);
//        height = v.findViewById(R.id.bracket_height);
//        maxdx = v.findViewById(R.id.bracket_max_dx);
//        maxdy = v.findViewById(R.id.bracket_max_dy);
//        dx = v.findViewById(R.id.bracket_dx);
//        dy = v.findViewById(R.id.bracket_dy);
//        scale = v.findViewById(R.id.bracket_scale);


        bracketWinners = v.findViewById(R.id.bracket_winners);

        bracketLosers = v.findViewById(R.id.bracket_losers);
        bracketLosers.setOnSystemUiVisibilityChangeListener(visibility -> {
            Toast.makeText(getContext(), "Visibility Changed", Toast.LENGTH_SHORT).show();
        });
        lv = v.findViewById(R.id.loading_view);

        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height);
        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);

        winnersRounds = new ArrayList<>();
        losersRounds = new ArrayList<>();
        roundLabelsW = new ArrayList<>();
        roundLabelsL = new ArrayList<>();
        roundLayoutsWinners = new ArrayList<>();
        roundLayoutsLosers = new ArrayList<>();

        RoundLayout.setFragment(this);


        oneParticipantMessage = v.findViewById(R.id.one_participant_message);
        actionButton = v.findViewById(R.id.bracket_action_button);
        refreshButton = v.findViewById(R.id.bracket_refresh);
        refreshButton.setOnClickListener(v1 -> newRefresh());


        tournament = Objects.requireNonNull(intent.getExtras()).getParcelable("tournament");
        assert tournament != null;
        if (tournament.isSearched()) {
            actionButton.setVisibility(View.GONE);
        }

        lastSearchedMatch = new Match();

        oldType = "default";


        newRefresh();
        //sendTournamentRequest();

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


    private void setOldValues() {
        oldType = tournament.getType();
        oldParticipantCount = tournament.getParticipantCount();
        oldHoldThirdPlace = tournament.isHoldThirdPlaceMatch();
        oldGrandsModifier = tournament.getGrandFinalsModifier();
    }


    private void setSearchTournamentValues() {
        countedParticipants = 0;
        int roundsToCheck = 0;
        if (winnersRounds.size() > 0) {
            roundsToCheck = winnersRounds.size() > 1 ? 2 : 1;
        }
        for (int i = 0; i < roundsToCheck; i++) {
            ArrayList<Match> matchList = winnersRounds.get(i).getMatchList();
            for (int j = 0; j < matchList.size(); j++) {
                if (matchList.get(j).getP1PrereqIdentifier() == 0) {
                    countedParticipants = countedParticipants + 1;
                }
                if (matchList.get(j).getP2PrereqIdentifier() == 0) {
                    countedParticipants = countedParticipants + 1;
                }
            }
        }
        tournament.setParticipantCount(countedParticipants);
    }


    private void refresh() {

        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response);
                String urlTmp = tournament.getUrl();
                String subdomainTmp = tournament.getSubdomain();
                tournament = gson.fromJson(jsonElement.getAsJsonObject().get("tournament"), Tournament.class);
                tournament.undoJsonShenanigans();
                tournament.setUrl(urlTmp);
                tournament.setSubdomain(subdomainTmp);
                tournament.setSearched(true);
                lastJson = response;
                getTournamentRoundInfo(lastJson);
                setSearchTournamentValues();
                startBracketDisplay();
            }

            @Override
            public void onErrorResponse(ArrayList errorResponse) {

            }
        }, ChallongeRequests.jsonAtTheEndOfTheNormalURL(tournament.getUrl(), tournament.getSubdomain()));

    }



    /*
    TODO FOR UNSEARCHED
    function to refresh tournament param
    function to use that to remake rounds if participant count/round count/format changes, else just reset matches
     */

    public void newRefresh() {

        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                tournament = new Gson().fromJson(new JsonParser().parse(response).getAsJsonObject().get("tournament"), Tournament.class);
                tournament.undoJsonShenanigans();
                setActionButton();


                ChallongeRequests.sendRequest(new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {

                        if (tournament.getParticipantCount() < 2) {
                            lv.setVisibility(View.GONE);
                            oneParticipantMessage.setVisibility(View.VISIBLE);
                        } else {
                            oneParticipantMessage.setVisibility(View.GONE);
                            lv.hide();

                            Log.d("grMod", tournament.getGrandFinalsModifier() + "-" + oldGrandsModifier);
                            if (!tournament.getType().equals(oldType) || tournament.getParticipantCount() != oldParticipantCount || tournament.isHoldThirdPlaceMatch() != oldHoldThirdPlace || !tournament.getGrandFinalsModifier().equals(oldGrandsModifier)) {
                                reset();
                                getTournamentRoundInfo(response);
                                makeRoundLayouts();
                                newMakeBrackets(false);
                            } else {
                                resetRoundsInfo();
                                getTournamentRoundInfo(response);
                                refreshRoundLayouts();
                                newMakeBrackets(true);
                                //setMatchInfo();
                            }
                            if (tournament.getType().equals(Tournament.SINGLE_ELIM) || tournament.getType().equals(Tournament.DOUBLE_ELIM)) {
                                //setRoundColor();
                            }
                            setOldValues();
                        }
                    }

                    @Override
                    public void onErrorResponse(ArrayList errorResponse) {

                    }
                }, ChallongeRequests.jsonAtTheEndOfTheNormalURL(tournament.getUrl(), tournament.getSubdomain()));

            }

            @Override
            public void onErrorResponse(ArrayList errorList) {

            }
        }, ChallongeRequests.tournamentShow(tournament.getId()));

    }


    private void newMakeBrackets(boolean refresh) {
        RoundLayout.setTournament(tournament);
        makeWinners(refresh);
        newMakeLosers(refresh);

    }


    private void makeRoundLayouts() {


        for (int i = 0; i < winnersRounds.size(); i++) {
            RoundLayout roundLayout = new RoundLayout(getContext());
            roundLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            roundLayoutsWinners.add(roundLayout);
        }
        for (int i = 0; i < losersRounds.size(); i++) {
            RoundLayout roundLayout = new RoundLayout(getContext());
            roundLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            roundLayoutsLosers.add(roundLayout);
        }


        for (int i = 0; i < roundLayoutsWinners.size(); i++) {
            if (i > 0) {
                roundLayoutsWinners.get(i).setPrevRoundLayout(roundLayoutsWinners.get(i - 1));
            }
            if (i < roundLayoutsWinners.size() - 1) {
                roundLayoutsWinners.get(i).setNextRoundLayout(roundLayoutsWinners.get(i + 1));
            }
        }

        for (int i = 0; i < roundLayoutsLosers.size(); i++) {
            if (i > 0) {
                roundLayoutsLosers.get(i).setPrevRoundLayout(roundLayoutsLosers.get(i - 1));
            }
            if (i < roundLayoutsLosers.size() - 1) {
                roundLayoutsLosers.get(i).setNextRoundLayout(roundLayoutsLosers.get(i + 1));
            }
        }

        refreshRoundLayouts();
    }


    private void refreshRoundLayouts() {
        for (int i = 0; i < winnersRounds.size(); i++) {
            roundLayoutsWinners.get(i).setRound(winnersRounds.get(i));
        }
        for (int i = 0; i < losersRounds.size(); i++) {
            roundLayoutsLosers.get(i).setRound(losersRounds.get(i));
        }

        //sets grandfinals boolean
        if (tournament.getType().equals(DOUBLE_ELIM) && !tournament.getGrandFinalsModifier().equals(Tournament.SKIP)) {
            winnersRounds.get(winnersRounds.size() - 1).setGrandFinals(true);
        }

        //sets prev/next rounds of roundLayouts
        for (int i = 0; i < roundLayoutsWinners.size(); i++) {
            if (i > 0) {
                roundLayoutsWinners.get(i).setPrevRound(roundLayoutsWinners.get(i - 1).getRound());
            }
            if (i < roundLayoutsWinners.size() - 1) {
                roundLayoutsWinners.get(i).setNextRound(roundLayoutsWinners.get(i + 1).getRound());
            }
        }
        for (int i = 0; i < roundLayoutsLosers.size(); i++) {
            if (i > 0) {
                roundLayoutsLosers.get(i).setPrevRound(roundLayoutsLosers.get(i - 1).getRound());
            }
            if (i < roundLayoutsLosers.size() - 1) {
                roundLayoutsLosers.get(i).setNextRound(roundLayoutsLosers.get(i + 1).getRound());
            }
        }
    }


    private void resetViewsViewGroups() {
        bracketWinners.removeAllViews();
        bracketLosers.removeAllViews();
    }

    private void reset() {

        resetViewsViewGroups();
        resetRoundLayouts();
        resetRoundsInfo();
    }

    private void resetRoundsInfo() {
        roundLabelsW.clear();
        roundLabelsL.clear();
        losersRounds.clear();
        winnersRounds.clear();
    }

    private void resetRoundLayouts() {
        roundLayoutsWinners.clear();
        roundLayoutsLosers.clear();
    }

    private void getTournamentRoundInfo(String jsonString) {
        Gson gson = new Gson();
        //separate gson to deal with matchs expose tags
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gsonMatch = builder.create();
        JsonParser jsonParser = new JsonParser();
        JsonElement tournamentField = jsonParser.parse(jsonString);

        JsonObject matchesByRound = tournamentField.getAsJsonObject().get("matches_by_round").getAsJsonObject();
        JsonArray rounds = tournamentField.getAsJsonObject().get("rounds").getAsJsonArray();

        for (JsonElement round : rounds) {
            JsonObject roundField = round.getAsJsonObject();
            Round roundTmp = gson.fromJson(roundField, Round.class);
            if (roundTmp.getNumber() > 0) {
                roundLabelsW.add(roundTmp);
            } else roundLabelsL.add(roundTmp);

        }
        Collections.sort(roundLabelsW, (n1, n2) -> n1.getNumber() - n2.getNumber());
        Collections.sort(roundLabelsL, (n1, n2) -> n1.getNumber() - n2.getNumber());
        //losers rounds are denoted with a negative number, Round -x is Loser's Round X
        //Since the rounds have been sorted by number value they need to be reversed to be order by actual round
        Collections.reverse(roundLabelsL);


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
            } else {
                roundTmp.setIsWinners(true);
                winnersRounds.add(roundTmp);
            }

            for (JsonElement match : round) {
                Participant player1 = new Participant();
                Participant player2 = new Participant();
                Match matchTmp = gsonMatch.fromJson(match.getAsJsonObject(), Match.class);

                setMatchScore(matchTmp, match);

                matchTmp.undoJsonShenanigans();

                //sets players of match
                if (!match.getAsJsonObject().get("player1").isJsonNull()) {
                    player1 = gson.fromJson(match.getAsJsonObject().get("player1").getAsJsonObject(), Participant.class);
                    matchTmp.setP1Seed(player1.getSeed());
                    player1.undoJsonShenanigans();

                } else {
                    matchTmp.setP1Decided(false);
                    if (matchTmp.getP1PrereqText() != null) {
                        player1.setName(matchTmp.getP1PrereqText());
                    }

                }
                if (!match.getAsJsonObject().get("player2").isJsonNull()) {
                    player2 = gson.fromJson(match.getAsJsonObject().get("player2").getAsJsonObject(), Participant.class);
                    matchTmp.setP2Seed(player2.getSeed());
                    player2.undoJsonShenanigans();

                } else {
                    matchTmp.setP2Decided(false);
                    if (matchTmp.getP2PrereqText() != null) {
                        player2.setName(matchTmp.getP2PrereqText());
                    }
                }

                matchTmp.setP1(player1);
                matchTmp.setP2(player2);

                if (roundTmp.isWinners()) {
                    matchTmp.setIsWinners(true);
                }
                roundTmp.getMatchList().add(matchTmp);
            }
        }
        //The third place match is not listed with the other rounds in the json so it has to be manually extracted

        if (tournament.getType().equals(SINGLE_ELIM) && !tournamentField.getAsJsonObject().get("third_place_match").isJsonNull()) {
            tournament.setHoldThirdPlaceMatch(true);
            JsonObject thirdPlaceMatch = tournamentField.getAsJsonObject().get("third_place_match").getAsJsonObject();
            Match thirdPlace = gsonMatch.fromJson(thirdPlaceMatch, Match.class);
            thirdPlace.undoJsonShenanigans();
            winnersRounds.get(winnersRounds.size() - 1).getMatchList().add(thirdPlace);
            Participant player1 = new Participant();
            Participant player2 = new Participant();

            if (!thirdPlaceMatch.getAsJsonObject().get("player1").isJsonNull()) {
                player1 = gson.fromJson(thirdPlaceMatch.getAsJsonObject().get("player1").getAsJsonObject(), Participant.class);
                thirdPlace.setP1Seed(player1.getSeed());
                player1.undoJsonShenanigans();

            } else {
                thirdPlace.setP1Decided(false);
                if (thirdPlace.getP1PrereqText() != null) {
                    player1.setName(thirdPlace.getP1PrereqText());
                }

            }
            if (!thirdPlaceMatch.getAsJsonObject().get("player2").isJsonNull()) {
                player2 = gson.fromJson(thirdPlaceMatch.getAsJsonObject().get("player2").getAsJsonObject(), Participant.class);
                thirdPlace.setP2Seed(player2.getSeed());
                player2.undoJsonShenanigans();

            } else {
                thirdPlace.setP2Decided(false);
                if (thirdPlace.getP2PrereqText() != null) {
                    player2.setName(thirdPlace.getP2PrereqText());
                }
            }

            thirdPlace.setP1(player1);
            thirdPlace.setP2(player2);


        } else if (tournamentField.getAsJsonObject().get("third_place_match").isJsonNull()) {
            tournament.setHoldThirdPlaceMatch(false);
        }


        Collections.sort(winnersRounds, (n1, n2) -> n1.getNumber() - n2.getNumber());
        Collections.sort(losersRounds, (n1, n2) -> n1.getNumber() - n2.getNumber());
        //losers rounds are denoted with a negative number, Round -x is Loser's Round X
        //Since the rounds have been sorted by number value they need to be reversed to be order by actual round
        Collections.reverse(losersRounds);

        //set round titles and populate roundLayoutList
        for (int i = 0; i < roundLabelsW.size(); i++) {
            winnersRounds.get(i).setTitle(roundLabelsW.get(i).getTitle());
        }
        for (int i = 0; i < roundLabelsL.size(); i++) {
            losersRounds.get(i).setTitle(roundLabelsL.get(i).getTitle());
        }


    }


    private void setMatchScore(Match match, JsonElement matchJson) {

        JsonArray score = matchJson.getAsJsonObject().get("scores").getAsJsonArray();
        if (score.size() == 2) {
            match.setP1Score(score.get(0).getAsString());
            match.setP2Score(score.get(1).getAsString());
        }


    }

    /*
    startBracketDisplay - if tournament has less than 2 members method will show a message stating
    bracket will be shown when there are enough members
     */
    private void startBracketDisplay() {
        if (tournament.getParticipantCount() < 2) {
            lv.setVisibility(View.GONE);
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


    private void makeWinners(boolean refresh) {


        //total number of rounds minus grand finals which is constructed separately
        int totalRounds;
        if (tournament.getType().equals(DOUBLE_ELIM) && !tournament.getGrandFinalsModifier().equals(GRANDS_SKIP)) {
            totalRounds = winnersRounds.size() - 1;
        } else {
            totalRounds = winnersRounds.size();
        }


        if (tournament.getType().equals(SINGLE_ELIM) || tournament.getType().equals(DOUBLE_ELIM)) {
            bracketWinners.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0, i1 = 0; i < totalRounds; i++, i1++) {

                //make a Linear Layout to hold each round
//                LinearLayout roundLayout = new LinearLayout(getContext());
//                LinearLayout bcvLayout = new LinearLayout(getContext());
//                roundLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                bcvLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                roundLayout.setOrientation(LinearLayout.VERTICAL);
//                bcvLayout.setOrientation(LinearLayout.VERTICAL);
//                //make space that goes before each round(2^(n-1)-1, n = round)
//                roundLayout.addView(makeSpaceComponent(((Math.pow(2, winnersRounds.get(i1).getNumber() - 1)) - 1)));

                //number of matches in "full" round, not all match slots will be used in which case they will be set invisible
                int numMatches = (int) Math.pow(2, totalRounds - (i + 1));

                if (!refresh) {
                    RoundLayout roundLayout = roundLayoutsWinners.get(i);
                    bracketWinners.addView(roundLayout);
                    roundLayout.setFullRoundSize(numMatches);
                    roundLayout.setSizeMultiplier(i);
                    roundLayout.initializeLayouts(numMatches, i);
                }

                if (i == 0) {
                    roundLayoutsWinners.get(i).setUnusedMatchesInvisible();
                }

                roundLayoutsWinners.get(i).updateMatchViews();


            }

            if (tournament.getType().equals(Tournament.DOUBLE_ELIM) && !tournament.getGrandFinalsModifier().equals(Tournament.SKIP)) {

                newMakeGrandFinals(refresh);

            }

            //double elimination grand finals
//            if (tournament.getType().equals(DOUBLE_ELIM) && !tournament.getGrandFinalsModifier().equals(GRANDS_SKIP)) {
//                makeGrandFinals();
//            }
//            if (tournament.getType().equals(SINGLE_ELIM) && tournament.isHoldThirdPlaceMatch() && tournament.getParticipantCount() > 3) {
//                makeThirdPlaceMatch();
//            }

        }
        //rr swiss
        else if (tournament.getType().equals(ROUND_ROBIN) || tournament.getType().equals(SWISS)) {
            for (int i = 0; i < winnersRounds.size(); i++) {
                bracketWinners.setOrientation(LinearLayout.VERTICAL);
                LayoutInflater inflater = getLayoutInflater();
                TextView round = (TextView) inflater.inflate(R.layout.menu_list_item, null);
                round.setText("Round" + (i + 1));
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

    private void newMakeGrandFinals(boolean refresh) {
        RoundLayout roundLayout = roundLayoutsWinners.get(roundLayoutsWinners.size() - 1);
        if (!refresh) {
            bracketWinners.addView(roundLayout);
        }
        roundLayout.setSizeMultiplier(roundLayout.getPrevRoundLayout().getSizeMultiplier());
        roundLayout.setFullRoundSize(1);
        roundLayout.initializeLayouts(roundLayout.getFullRoundSize(), roundLayout.getSizeMultiplier());
    }

    private void newMakeLosers(boolean refresh) {

        double shift = (losersRounds.size() % 2) == 0 ? 0.5 : 0;
        int start = (int) Math.ceil(roundLayoutsLosers.size() / 2.0) * 2;


        for (int i = losersRounds.size(), j = 0; i > 0; i--, j++) {
            RoundLayout roundLayout = roundLayoutsLosers.get(i - 1);
            int multiplier = (int) Math.ceil((start - j) / 2.0);
            int fullSizeRound = (int) Math.pow(2, Math.floor((roundLayoutsLosers.size() - i) / 2.0));
            int preRoundMultiplier = (int) Math.floor((i / 2.0) + 1 + shift);
            roundLayout.setSizeMultiplier(multiplier - 1);
            roundLayout.setFullRoundSize(fullSizeRound);
            roundLayout.setPreRoundMultiplier(preRoundMultiplier - 1);
            if (!refresh) {
                bracketLosers.addView(roundLayout, 0);
            }


        }

        for (int i = 0; i < roundLayoutsLosers.size(); i++) {

            RoundLayout roundLayout = roundLayoutsLosers.get(i);
            roundLayout.initializeLayouts(roundLayout.getFullRoundSize(), roundLayout.getSizeMultiplier());
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
                    bcvLayout.addView(makeBCVComponent(i / 2, BracketConnectorView.MODE_TOP));
                    bcvLayout.addView(makeBCVComponent(i / 2, BracketConnectorView.MODE_BOTTOM));
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, i / 2) - 0.5));
                    bcvLayout.addView(makeSpaceComponent(1));
                }
            } else {
                //preround space for BCV
                bcvLayout.addView(makeSpaceComponent(Math.pow(2, multiplier - 1) - 1));
                for (int j = 0; j < numMatches; j++) {
                    bcvLayout.addView(makeBCVComponent(0, BracketConnectorView.MODE_MIDDLE));
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

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private void makeBracketDisplay() {


        if (!tournament.getType().equals(ROUND_ROBIN) && !tournament.getType().equals(SWISS)) {
            //setRoundHeaders();
        }
        RoundLayout.setTournament(tournament);
        //makeWinners();
        makeLosers();
        //setUnusedMatchesInvisible();
        //setMatchInfo();

    }


    public void sendTournamentRequest() {


        if (!tournament.isSearched()) {
            ChallongeRequests.sendRequest(new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    tournament = new Gson().fromJson(new JsonParser().parse(response).getAsJsonObject().get("tournament"), Tournament.class);
                    tournament.undoJsonShenanigans();
                    System.out.println(tournament.toString());
                    setActionButton();


                    ChallongeRequests.sendRequest(new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {

                            if (tournament.getParticipantCount() < 2) {
                                lv.setVisibility(View.GONE);
                                oneParticipantMessage.setVisibility(View.VISIBLE);
                            } else {
                                oneParticipantMessage.setVisibility(View.GONE);
                                lv.hide();

                                Log.d("grMod", tournament.getGrandFinalsModifier() + "-" + oldGrandsModifier);
                                if (!tournament.getType().equals(oldType) || tournament.getParticipantCount() != oldParticipantCount || tournament.isHoldThirdPlaceMatch() != oldHoldThirdPlace || !tournament.getGrandFinalsModifier().equals(oldGrandsModifier)) {
                                    reset();
                                    lastJson = response;
                                    getTournamentRoundInfo(lastJson);
                                    makeBracketDisplay();
                                } else {
                                    resetRoundsInfo();
                                    lastJson = response;
                                    getTournamentRoundInfo(lastJson);
                                    //setMatchInfo();
                                }
                                if (tournament.getType().equals(Tournament.SINGLE_ELIM) || tournament.getType().equals(Tournament.DOUBLE_ELIM)) {
                                    //setRoundColor();
                                }
                                setOldValues();
                            }
                        }

                        @Override
                        public void onErrorResponse(ArrayList errorResponse) {

                        }
                    }, ChallongeRequests.jsonAtTheEndOfTheNormalURL(tournament.getUrl(), tournament.getSubdomain()));
                }

                @Override
                public void onErrorResponse(ArrayList errorList) {

                }
            }, ChallongeRequests.tournamentShow(tournament.getId()));
        } else {
//            ChallongeRequests.sendRequest(new VolleyCallback() {
//                @Override
//                public void onSuccess(String response) {
            reset();
            refresh();
//                }
//
//                @Override
//                public void onErrorResponse(ArrayList errorResponse) {
//
//                }
//            }, ChallongeRequests.jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(tournament.getUrl(), tournament.getSubdomain()));
        }
    }

    private void makeGrandFinals() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        int size = 0;
        for (int i = 0; i < winnersRounds.size(); i++) {
            if (!winnersRounds.get(i).isHidden()) {
                size++;
            }
        }
        size = Math.min(size, winnersRounds.size() - 1);

        //init GF match 1
        LinearLayout BCV1 = new LinearLayout(getContext());
        BCV1.setLayoutParams(layoutParams);
        BCV1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout grandFinals = new LinearLayout(getContext());
        grandFinals.setLayoutParams(layoutParams);
        grandFinals.setOrientation(LinearLayout.VERTICAL);
        BCV1.addView(makeSpaceComponent(Math.pow(2, size - 2) - 0.5));
        grandFinals.addView(makeSpaceComponent(Math.pow(2, size - 2)));
        BCV1.addView(makeBCVComponent(0, BracketConnectorView.MODE_TOP));
        grandFinals.addView(makeMatchComponent());
        bracketWinners.addView(BCV1);
        bracketWinners.addView(grandFinals);

        if (tournament.getGrandFinalsModifier().equals(GRANDS_DEFAULT)) {
            //init GF reset
            LinearLayout BCV2 = new LinearLayout(getContext());
            BCV2.setLayoutParams(layoutParams);
            BCV2.setOrientation(LinearLayout.VERTICAL);
            LinearLayout grandFinalsReset = new LinearLayout(getContext());
            grandFinalsReset.setLayoutParams(layoutParams);
            grandFinalsReset.setOrientation(LinearLayout.VERTICAL);
            BCV2.addView(makeSpaceComponent(Math.pow(2, size - 2)));
            grandFinalsReset.addView(makeSpaceComponent(Math.pow(2, size - 2)));
            BCV2.addView(makeBCVComponent(0, BracketConnectorView.MODE_MIDDLE));
            grandFinalsReset.addView(makeMatchComponent());
            bracketWinners.addView(BCV2);
            bracketWinners.addView(grandFinalsReset);
        }
    }

    private void makeThirdPlaceMatch() {
        LinearLayout finalRound = (LinearLayout) bracketWinners.getChildAt(bracketWinners.getChildCount() - 1);
        Space space = makeSpaceComponent(2);
        MatchView thirdPlaceMatch = makeMatchComponent();
        finalRound.addView(space);
        finalRound.addView(thirdPlaceMatch);
    }


    private void setMatchInfo() {
        Log.d("setMatchInfo", "ran");

        //double/single elim matches
        if (tournament.getType().equals(DOUBLE_ELIM) || tournament.getType().equals(SINGLE_ELIM)) {
            //get number of rounds minus double elim grands
            int numberOfRoundsWinners = tournament.getType().equals(DOUBLE_ELIM) ? winnersRounds.size() - 1 : winnersRounds.size();

            Log.d("sss", String.valueOf(winnersRounds.size()));
            Log.d("ssst", String.valueOf(winnersRounds.get(0).getMatchList().size()));
            //set winners match info
            for (int i = 0; i < numberOfRoundsWinners; i++) {
                if (!winnersRounds.get(i).isHidden()) {
                    ArrayList<Match> matches = winnersRounds.get(i).getMatchList();
                    LinearLayout round = (LinearLayout) bracketWinners.getChildAt(2 * i);
                    int iterator = 0;
                    for (int j = 0; j < round.getChildCount(); j++) {
                        //must only set info for the visible constraintlayout/ignore unused matches
                        if (round.getChildAt(j) instanceof ConstraintLayout && round.getChildAt(j).getVisibility() == View.VISIBLE) {
                            setMatchView((MatchView) round.getChildAt(j), matches.get(iterator));
                            iterator++;
                        }
                    }
                }
            }


            //sets grand finals in double elim
            // an iterating method could be used just to eliminate use of magic numbers but these locations
            //should be constant across any double elim bracket constructed with this program
            int grandFinalsIndex = (winnersRounds.size() - 1) * 2;

            if (tournament.getType().equals(DOUBLE_ELIM) && !tournament.getGrandFinalsModifier().equals(GRANDS_SKIP)) {


                LinearLayout GF1 = (LinearLayout) bracketWinners.getChildAt(grandFinalsIndex);
                MatchView GF1Match = (MatchView) GF1.getChildAt(1);
                setMatchView(GF1Match, winnersRounds.get(winnersRounds.size() - 1).getMatchList().get(0));


                if (tournament.getGrandFinalsModifier().equals(GRANDS_DEFAULT) && winnersRounds.get(winnersRounds.size() - 1).getMatchList().size() > 1) {
                    LinearLayout GF2 = (LinearLayout) bracketWinners.getChildAt(grandFinalsIndex + 2);
                    MatchView GF2Match = (MatchView) GF2.getChildAt(1);
                    setMatchView(GF2Match, winnersRounds.get(winnersRounds.size() - 1).getMatchList().get(1));
                } else if (!tournament.getGrandFinalsModifier().equals(GRANDS_SINGLE_MATCH)) {

                    bracketWinners.getChildAt(grandFinalsIndex + 1).setVisibility(View.GONE);
                    bracketWinners.getChildAt(grandFinalsIndex + 2).setVisibility(View.GONE);
                }
            }


            //set losers match info
            for (int i = 0; i < losersRounds.size(); i++) {
                ArrayList<Match> matches = losersRounds.get(i).getMatchList();
                LinearLayout round = (LinearLayout) bracketLosers.getChildAt(2 * i);
                int iterator = 0;
                for (int j = 0; j < round.getChildCount(); j++) {
                    //must only set info for the visible constraintlayout/ignore unused matches
                    if (round.getChildAt(j) instanceof ConstraintLayout && round.getChildAt(j).getVisibility() == View.VISIBLE) {

                        setMatchView((MatchView) round.getChildAt(j), matches.get(iterator));
                        iterator++;
                    }
                }
            }

        } else if (tournament.getType().equals(ROUND_ROBIN) || tournament.getType().equals(SWISS)) {
            for (int i = 0; i < winnersRounds.size(); i++) {
                ArrayList<Match> matches = winnersRounds.get(i).getMatchList();
                LinearLayout round = (LinearLayout) bracketWinners.getChildAt(2 * i + 1);
                int iterator = 0;
                for (int j = 0; j < round.getChildCount(); j++) {
                    //must only set info for the visible constraintlayout/ignore unused matches
                    if (round.getChildAt(j) instanceof ConstraintLayout) {
                        setMatchView((MatchView) round.getChildAt(j), matches.get(iterator));
                        iterator++;
                    }
                }
            }
        }


    }

    private void setMatchView(MatchView matchView, Match match) {

        matchView.setmMatchId(match.getId());
        matchView.setMatch(match);

        TextView matchNumber = matchView.findViewById(R.id.matchNumber);
        matchNumber.setText(String.valueOf(match.getIdentifier()));
        TextView P1Seed = matchView.findViewById(R.id.player1_seed);
        P1Seed.setText(String.valueOf(match.getP1Seed()));
        TextView P2Seed = matchView.findViewById(R.id.player2_seed);
        P2Seed.setText(String.valueOf(match.getP2Seed()));
        TextView P1Name = matchView.findViewById(R.id.player1_name);
        P1Name.setText(match.getP1().getName());
        TextView P2Name = matchView.findViewById(R.id.player2_name);
        P2Name.setText(match.getP2().getName());
        TextView P1Score = matchView.findViewById(R.id.player1_score);
        P1Score.setText(match.getP1Score());
        TextView P2Score = matchView.findViewById(R.id.player2_score);
        P2Score.setText(match.getP2Score());

        P1Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_background_light));
        P2Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_background_light));

        if (match.getState().equals(Match.COMPLETE)) {
            if (match.getWinnerID().equals(match.getP1().getId())) {
                P1Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_title));
                P2Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_background_light));
            } else if (match.getWinnerID().equals(match.getP2().getId())) {
                P2Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_title));
                P1Score.setBackgroundTintList(getResources().getColorStateList(R.color.menu_background_light));
            }
        }

        if (tournament.getState().equals(COMPLETE)) {
            matchView.setOnLongClickListener(v -> {
                Toast.makeText(getContext(), "Tournament is over", Toast.LENGTH_LONG).show();
                return true;
            });
        } else {
            if (match.getState().equals(Match.OPEN) || match.getState().equals(Match.COMPLETE)) {
                matchView.setOnLongClickListener(v -> {
                    buildMatchReportDialog(matchView.getMatch());
                    return true;
                });
            }
        }
        //match.setOnClickListener(v -> Toast.makeText(getContext(), match.getmMatchId(), Toast.LENGTH_SHORT).show());


    }

    private void sendStartRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                newRefresh();
                Toast.makeText(getContext(), "Tournament Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {

            }
        }, ChallongeRequests.tournamentStart(tournament.getId()));
    }

    private void sendFinalizeRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                newRefresh();
                Toast.makeText(getContext(), "Tournament Finalized", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                for (int i = 0; i < errorList.size(); i++) {
                    Toast.makeText(getContext(), String.valueOf(errorList.get(i)), Toast.LENGTH_SHORT).show();
                }
            }
        }, ChallongeRequests.tournamentFinalize(tournament.getId()));
    }

    private void setActionButton() {
        switch (tournament.getState()) {
            case CHECKED_IN:
            case PENDING:
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Start");
                actionButton.setOnClickListener(v -> sendStartRequest());
                break;
            case CHECKING_IN:
                //TODO process checkins on click
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Check In");
                break;
            case UNDERWAY:
            case COMPLETE:
                actionButton.setVisibility(View.GONE);
                break;
            case AWAITING_REVIEW:
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Finalize");
                actionButton.setOnClickListener(v -> sendFinalizeRequest());
                break;
            default:
                actionButton.setText("-----");
                break;
        }
    }

    // get match layout
    private MatchView makeMatchComponent() {
//        LayoutInflater inflater = getLayoutInflater();
//        assert inflater != null;
        return new MatchView(getContext());
    }

    //get space
    private Space makeSpaceComponent(double heightMultiplier) {
        Space space = new Space(getContext());
        space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (mHeightUnit * heightMultiplier)));
        return space;
    }


    //get bracketConnectorView
    private BracketConnectorView makeBCVComponent(int heightMultiplier, int mode) {
        BracketConnectorView bcv = new BracketConnectorView(getContext(), null, mHeightUnit * (int) Math.pow(2, heightMultiplier), mode, null);
        return bcv;
    }


    private void hideRound(int roundNumber, boolean winners) {
        if (winners) {
            Toast.makeText(getContext(), String.valueOf(roundLabelsW.get(roundNumber).getTitle()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), String.valueOf(roundLabelsL.get(roundNumber).getTitle()), Toast.LENGTH_SHORT).show();
        }
    }




    private void setUnusedMatchesInvisible() {


        //winners
        if ((tournament.getType().equals(DOUBLE_ELIM) || tournament.getType().equals(SINGLE_ELIM)) && winnersRounds.size() > 1) {
            LinearLayout firstRoundLayout = (LinearLayout) bracketWinners.getChildAt(0);
            LinearLayout firstRoundBC = (LinearLayout) bracketWinners.getChildAt(1);
            Round secondRound = winnersRounds.get(1);
            ArrayList<Match> secondRoundMatches = secondRound.getMatchList();
            for (int i = 0; i < secondRoundMatches.size(); i++) {


                //Or just refactor these numbers to static final ints
                if (secondRoundMatches.get(i).getP1PrereqIdentifier() == 0) {
                    //index of of matchLayout and BCV corresponding to current second round index
                    int matchLayoutIndex = (4 * i) + 1;
                    int bcvIndex = (5 * i) + 1;
                    firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                    firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                }
                if (secondRoundMatches.get(i).getP2PrereqIdentifier() == 0) {
                    int matchLayoutIndex = (4 * i) + 3;
                    int bcvIndex = (5 * i) + 2;
                    firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                    firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                }
            }
        }

        //losers
        if (tournament.getType().equals(DOUBLE_ELIM) && losersRounds.size() > 1) {
            LinearLayout firstRoundLayout = (LinearLayout) bracketLosers.getChildAt(0);
            LinearLayout firstRoundBC = (LinearLayout) bracketLosers.getChildAt(1);
            LinearLayout secondRoundLayout = (LinearLayout) bracketLosers.getChildAt(2);
            Round secondRound = losersRounds.get(1);
            ArrayList<Match> secondRoundMatches = secondRound.getMatchList();

            //handles two scenarios where if first round match capacity is larger than second round
            if (firstRoundLayout.getChildCount() > secondRoundLayout.getChildCount()) {
                for (int i = 0; i < secondRoundMatches.size(); i++) {


                    if (secondRoundMatches.get(i).isP1IsPrereqLoser()) {
                        //index of of matchLayout and BCV corresponding to current second round index
                        int matchLayoutIndex = (4 * i) + 1;
                        int bcvIndex = (5 * i) + 1;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                    if (secondRoundMatches.get(i).isP2IsPrereqLoser()) {
                        int matchLayoutIndex = (4 * i) + 3;
                        int bcvIndex = (5 * i) + 2;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }

            } else {
                for (int i = 0; i < secondRoundMatches.size(); i++) {

                    if ((secondRoundMatches.get(i).isP2IsPrereqLoser())) {
                        int matchLayoutIndex = (2 * i) + 1;
                        int bcvIndex = (2 * i) + 1;
                        firstRoundLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                        firstRoundBC.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }
            }

        }


    }


    private void buildMatchReportDialog(Match match) {

        if (match.getState().equals(Match.OPEN)) {
            AlertDialog dialog = makeMatchReportDialogBuilder(match).create();
            setPositiveButton(dialog, match);
            dialog.show();
        }
        if (match.getState().equals(Match.COMPLETE)) {
            AlertDialog dialog = buildReopenDialog(match).create();
            setReopenDialog(dialog, match);
            dialog.show();
        }
    }

    private void setReopenDialog(AlertDialog dialog, Match match) {
        View dialogueLayout = getLayoutInflater().inflate(R.layout.match_reopen_dialog, null);
        EditText reopenPrompt = dialogueLayout.findViewById(R.id.match_reopen_prompt);
        LinearLayout errorsLayout = dialogueLayout.findViewById(R.id.match_reopen_error_layout);
        dialog.setView(dialogueLayout);
        dialog.setOnShowListener(dialog1 -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                if (reopenPrompt.getText().toString().equals(REOPEN)) {
                    sendMatchReopenRequest(match, errorsLayout, dialog);
                } else {
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText("Type 'Reopen' with no spaces or other punctuation on either side");
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            });
        });
    }

    private AlertDialog.Builder buildReopenDialog(Match match) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Would you like to reopen the match");
        builder.setNegativeButton("Cancel", (dialog1, which) -> {
        });
        builder.setPositiveButton("Reopen", (dialog, which) -> {
        });

        return builder;
    }

    private AlertDialog.Builder makeMatchReportDialogBuilder(Match match) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Report Score");
        builder.setNegativeButton("Cancel", (dialog1, which) -> {
        });
        builder.setPositiveButton("Submit", (dialog, which) -> {
        });

        return builder;
    }

    private void setPositiveButton(AlertDialog dialog, Match match) {
        View dialogueLayout = getLayoutInflater().inflate(R.layout.match_report_dialog, null);
        TextView p1Name = dialogueLayout.findViewById(R.id.match_report_player1_name);
        p1Name.setText(match.getP1().getName());
        TextView p2Name = dialogueLayout.findViewById(R.id.match_report_player2_name);
        p2Name.setText(match.getP2().getName());
        EditText p1Score = dialogueLayout.findViewById(R.id.match_report_player1_score);
        EditText p2Score = dialogueLayout.findViewById(R.id.match_report_player2_score);
        MatchReportButton p1Win = dialogueLayout.findViewById(R.id.match_report_P1_winner);
        MatchReportButton p2Win = dialogueLayout.findViewById(R.id.match_report_P2_winner);
        LinearLayout errorsLayout = dialogueLayout.findViewById(R.id.match_report_dialog_error_layout);
        setUpMatchReportButtons(p1Win, p2Win);
        dialog.setView(dialogueLayout);
        if (match.getWinnerID().equals(match.getP1().getId())) {
            p1Win.setWon(true);
            p2Win.setWon(false);
        } else if (match.getWinnerID().equals(match.getP2().getId())) {
            p2Win.setWon(true);
            p1Win.setWon(false);
        } else {
            p1Win.setWon(false);
        }

        dialog.setOnShowListener(dialog1 -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                match.setP1Score(p1Score.getText().toString());
                match.setP2Score(p2Score.getText().toString());
                if (p1Win.isWon()) {
                    match.setWinnerID(match.getP1().getId());
                } else if (p2Win.isWon()) {
                    match.setWinnerID(match.getP2().getId());
                }
                sendMatchUpdateRequest(match, errorsLayout, dialog);
            });

        });


    }

    private void sendMatchReopenRequest(Match match, LinearLayout errorsLayout, AlertDialog dialog) {
        dialog.dismiss();
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                newRefresh();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText(String.valueOf(errorList.get(i)));
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            }
        }, ChallongeRequests.matchReopen(match));
    }

    private void sendMatchUpdateRequest(Match match, LinearLayout errorsLayout, AlertDialog dialog) {
        dialog.dismiss();
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                newRefresh();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                    error.setText(String.valueOf(errorList.get(i)));
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            }
        }, ChallongeRequests.matchUpdate(match));
    }


    private void setUpMatchReportButtons(MatchReportButton p1, MatchReportButton p2) {
        p1.setWon(true);
        p2.setWon(false);
        p1.setOnClickListener(v -> {
            if (p2.isWon()) {
                p2.setWon(false);
                p1.setWon(true);
            } else if (!p2.isWon() && p1.isWon()) {
                p1.setWon(false);
            } else {
                p1.setWon(true);
            }
        });
        p2.setOnClickListener(v -> {
            if (p1.isWon()) {
                p1.setWon(false);
                p2.setWon(true);
            } else if (!p1.isWon() && p2.isWon()) {
                p2.setWon(false);
            } else {
                p2.setWon(true);
            }
        });

    }




}
