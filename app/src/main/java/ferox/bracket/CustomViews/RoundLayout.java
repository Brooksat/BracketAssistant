package ferox.bracket.CustomViews;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Match;
import ferox.bracket.Tournament.Round;
import ferox.bracket.Tournament.Tournament;
import ferox.bracket.fragments.BracketFragment;

public class RoundLayout extends LinearLayout {


    Context mContext;
    LayoutInflater mInflater;

    static private Tournament tournament;
    static private BracketFragment fragment;

    private int mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height);
    private int mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);

    private int sizeMultiplier;
    private int fullRoundSize;
    private int preRoundMultiplier;


    private boolean hidden;

    /**
     * Part of layout including match_layout and bcv_layout only
     */
    private LinearLayout minusButtonLayout;
    private LinearLayout matchLayout;
    private TextView matchLayoutCover;
    private LinearLayout bcvLayout;
    private Round prevRound;
    private RoundLayout prevRoundLayout;
    private Round round;
    private Round nextRound;
    private RoundLayout nextRoundLayout;
    private Button roundHeaderButton;


    public RoundLayout(Context context) {
        super(context);
        mContext = context;
        setWillNotDraw(false);
        init();
    }


    public RoundLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);
        init();
    }

//    public RoundLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context,attrs);
//    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int childSumWidth = 0;
//        int childSumHeight = 0;
//        int childHspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        int childWspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(childWspec, childHspec);
//            int childWidth = child.getMeasuredWidth(),
//                    childHeight = child.getMeasuredHeight();
//            childSumWidth += childWidth;
//            childSumHeight += childHeight;
//        }
//
//        int desiredWSpec = MeasureSpec.makeMeasureSpec(childSumWidth, MeasureSpec.UNSPECIFIED);
//        int desiredHSpec = MeasureSpec.makeMeasureSpec(childSumHeight, MeasureSpec.EXACTLY);
//        setMeasuredDimension(desiredWSpec, desiredHSpec);
//        super.onMeasure(desiredWSpec, desiredHSpec);
//    }


    private void init() {


        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.round_layout, this, true);

        roundHeaderButton = findViewById(R.id.round_layout_button);
        minusButtonLayout = findViewById(R.id.round_layout_minus_button);
        matchLayout = findViewById(R.id.round_layout_match_layout);

        matchLayoutCover = findViewById(R.id.round_layout_view_cover);
        matchLayoutCover.setLayoutParams(params(mWidthUnit / 3, LayoutParams.MATCH_PARENT));


        bcvLayout = findViewById(R.id.round_layout_bcv_layout);

        roundHeaderButton.setOnClickListener(v -> hide());

    }

    private void initLayoutWinneres(int fullRoundSize, int sizeMultiplier) {
        matchLayout.removeAllViews();
        bcvLayout.removeAllViews();
        matchLayout.setVisibility(VISIBLE);
        bcvLayout.setVisibility(VISIBLE);
        matchLayoutCover.setVisibility(GONE);
        //make space that goes before each round(2^(n-1)-1, n = round)
        matchLayout.addView(makeSpaceComponent(((Math.pow(2, sizeMultiplier)) - 1)));

        //matches
        for (int j = 0; j < fullRoundSize; j++) {
            //make  match
            matchLayout.addView(makeMatchComponent());
            //then space((2^n)-1)
            if (j < fullRoundSize - 1) {
                matchLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier + 1) - 1));
            }
        }

        //bracket connectors
        for (int j = 0; j < fullRoundSize / 2; j++) {
            bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 0.5));
            bcvLayout.addView(makeBCVComponent(sizeMultiplier, BracketConnectorView.MODE_TOP));
            bcvLayout.addView(makeBCVComponent(sizeMultiplier, BracketConnectorView.MODE_BOTTOM));
            if (j < (fullRoundSize / 2 - 1)) {
                bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 0.5));
                bcvLayout.addView(makeSpaceComponent(1));
            }
        }
        if (nextRoundLayout != null && nextRoundLayout.getRound().isGrandFinals()) {
            bcvLayout.addView(makeSpaceComponent(((Math.pow(2, sizeMultiplier)) - 1) + 0.5));
            bcvLayout.addView(makeBCVComponent(0, BracketConnectorView.MODE_TOP));
        }

        if (round.getNumber() == 1) {
            setUnusedMatchesInvisible();
        }
        updateMatchViews();
    }


    private void initGrandFinals(int fullRoundSize, int sizeMultiplier) {

        minusButtonLayout.setOrientation(VERTICAL);
        matchLayout.setOrientation(HORIZONTAL);

        matchLayout.removeAllViews();
        bcvLayout.removeAllViews();
        if (minusButtonLayout.getChildAt(0) instanceof Space) {
            minusButtonLayout.removeViewAt(0);
        }
        matchLayout.setVisibility(VISIBLE);

        bcvLayout.setVisibility(VISIBLE);
        matchLayoutCover.setVisibility(GONE);
        //make space that goes before each round(2^(n-1)-1, n = round)
        minusButtonLayout.addView(makeSpaceComponent(((Math.pow(2, sizeMultiplier)) - 1) + 1), 0);


        for (int j = 0; j < round.getMatchList().size(); j++) {
            //make  match
            matchLayout.addView(makeMatchComponent());
            //then space((2^n)-1)
            if (j < round.getMatchList().size() - 1) {
                matchLayout.addView(makeBCVComponent(0.5, BracketConnectorView.MODE_MIDDLE));
            }
        }
    }

    private void initLayoutLosers(int fullRoundSize, int sizeMultiplier) {
        matchLayout.removeAllViews();
        bcvLayout.removeAllViews();
        matchLayout.setVisibility(VISIBLE);
        bcvLayout.setVisibility(VISIBLE);
        matchLayoutCover.setVisibility(GONE);

        int roundNumber = round.getNumber() * -1;
        //double multiplier = Math.floor((roundNumber / 2.0) + 1 + shift);
        // setSizeMultiplier((int) multiplier);


        //makes space that offsets the height of subsequent rounds
        matchLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 1));


        for (int j = 0; j < fullRoundSize; j++) {
            matchLayout.addView(makeMatchComponent());
            if (j < fullRoundSize - 1) {
                matchLayout.addView(makeSpaceComponent((Math.pow(2, sizeMultiplier + 1)) - 1));


            }
        }


        if (nextRoundLayout != null) {
            Log.d("fullRoundSize", fullRoundSize + String.valueOf(nextRoundLayout.getFullRoundSize()));
        }
        //Bracket connector views
        if (nextRoundLayout != null && nextRoundLayout.getFullRoundSize() < fullRoundSize) {
            for (int j = 0; j < fullRoundSize / 2; j++) {
                //offset space
                bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 0.5));
                bcvLayout.addView(makeBCVComponent(sizeMultiplier, BracketConnectorView.MODE_TOP));
                bcvLayout.addView(makeBCVComponent(sizeMultiplier, BracketConnectorView.MODE_BOTTOM));
                if (j < (fullRoundSize / 2 - 1)) {
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 0.5));
                    bcvLayout.addView(makeSpaceComponent(1));
                }
            }
        } else if (nextRoundLayout != null && nextRoundLayout.getFullRoundSize() == fullRoundSize) {

            //preround space for BCV
            bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier) - 1));
            for (int j = 0; j < fullRoundSize; j++) {
                bcvLayout.addView(makeBCVComponent(0, BracketConnectorView.MODE_MIDDLE));
                if (j < fullRoundSize - 1) {
                    bcvLayout.addView(makeSpaceComponent(Math.pow(2, sizeMultiplier + 1) - 1));
                }
            }
        }

        if (round.getNumber() == -1) {
            setUnusedMatchesInvisible();
        }
        updateMatchViews();


    }


    public void initializeLayouts(int fullRoundSize, int sizeMultiplier) {

        roundHeaderButton.setText(String.valueOf(sizeMultiplier));
        //Layout is unhidden
        if (!hidden) {
            if (round.isWinners()) {

                if (round.isGrandFinals()) {
                    initGrandFinals(fullRoundSize, sizeMultiplier);
                } else {
                    initLayoutWinneres(fullRoundSize, sizeMultiplier);
                }
            } else {
                initLayoutLosers(fullRoundSize, sizeMultiplier);
            }


        }
        //TODO when grand finals match is hidden the layout cover less space than it should
        //Layout is hidden
        else {
            //there is a another round
            if (nextRoundLayout != null) {
                //next round is not hidden
                if (!nextRoundLayout.isHidden()) {
                    matchLayout.setVisibility(GONE);
                    matchLayoutCover.setVisibility(VISIBLE);
                    bcvLayout.setVisibility(VISIBLE);

                    bcvLayout.removeAllViews();


                    double isNextGrands = nextRoundLayout.getRound().isGrandFinals() ? 0.5 : 0;
                    //offset
                    bcvLayout.addView(makeSpaceComponent(((Math.pow(2, nextRoundLayout.getSizeMultiplier())) - 1) + isNextGrands));

                    int numberbcv = nextRoundLayout.getRound().getMatchList().size();
                    int bcvMode = nextRoundLayout.getRound().isGrandFinals() ? BracketConnectorView.MODE_TOP : BracketConnectorView.MODE_MIDDLE;
                    if (nextRoundLayout.getRound().isGrandFinals()) {
                        numberbcv = 1;
                    }

                    for (int i = 0; i < numberbcv; i++) {
                        BracketConnectorView bcv = makeBCVComponent(0, bcvMode);
                        bcvLayout.addView(bcv);
                        Match match = nextRoundLayout.getRound().getMatchList().get(i);

                        //if round  has unused match slots then it will set the irrelevant bcvs invisible
                        if (round.getNumber() == 1 || round.getNumber() == -1) {
                            if (round.isWinners()) {
                                if (match.getP1PrereqIdentifier() == 0 && match.getP2PrereqIdentifier() == 0) {
                                    bcv.setVisibility(INVISIBLE);
                                }
                            } else {
                                if (match.isP2IsPrereqLoser()) {
                                    bcv.setVisibility(INVISIBLE);
                                }
                            }
                        }
                        bcvLayout.addView(makeSpaceComponent(Math.pow(2, nextRoundLayout.getSizeMultiplier() + 1) - 1));
                    }

                }
                //next round is hidden
                else {
                    matchLayout.setVisibility(GONE);
                    matchLayoutCover.setVisibility(VISIBLE);
                    bcvLayout.setVisibility(GONE);
                }
            } else {
                matchLayout.setVisibility(GONE);
                matchLayoutCover.setVisibility(VISIBLE);
                bcvLayout.setVisibility(GONE);
            }

        }

    }

    //TODO Possibly change so that the match will go through the layout until its gotten to the i matchLayout, gets rid of magic number but loses out on random access
    public void setUnusedMatchesInvisible() {
        if (round.isWinners()) {
            if (nextRound != null && nextRound.getMatchList() != null) {
                ArrayList<Match> nextRoundMatches = nextRound.getMatchList();

                for (int i = 0; i < nextRoundMatches.size(); i++) {
                    if (nextRoundMatches.get(i).getP1PrereqIdentifier() == 0) {
                        int matchLayoutIndex = (4 * i) + 1;
                        int bcvIndex = (5 * i) + 1;
                        Log.d("RoundNumber", String.valueOf(round.getNumber()));
                        matchLayout.getChildAt(matchLayoutIndex).setVisibility(INVISIBLE);
                        bcvLayout.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }
                for (int i = 0; i < nextRoundMatches.size(); i++) {
                    if (nextRoundMatches.get(i).getP2PrereqIdentifier() == 0) {
                        int matchLayoutIndex = (4 * i) + 3;
                        int bcvIndex = (5 * i) + 2;
                        matchLayout.getChildAt(matchLayoutIndex).setVisibility(INVISIBLE);
                        bcvLayout.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        //losers
        else {

            if (nextRoundLayout != null) {
                ArrayList<Match> nextRoundMatches = nextRoundLayout.getRound().getMatchList();
                //this round has larger dimension than next round
                if (fullRoundSize > nextRoundLayout.fullRoundSize) {

                    for (int i = 0; i < nextRoundMatches.size(); i++) {
                        if (nextRoundMatches.get(i).isP1IsPrereqLoser()) {
                            //index of of matchLayout and BCV corresponding to current second round index
                            int matchLayoutIndex = (4 * i) + 1;
                            int bcvIndex = (5 * i) + 1;
                            matchLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                            bcvLayout.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                        }
                        if (nextRoundMatches.get(i).isP2IsPrereqLoser()) {
                            int matchLayoutIndex = (4 * i) + 3;
                            int bcvIndex = (5 * i) + 2;
                            matchLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                            bcvLayout.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                        }
                    }
                }
                //same dimensions
                else {
                    for (int i = 0; i < nextRoundMatches.size(); i++) {

                        if ((nextRoundMatches.get(i).isP2IsPrereqLoser())) {
                            int matchLayoutIndex = (2 * i) + 1;
                            int bcvIndex = (2 * i) + 1;
                            matchLayout.getChildAt(matchLayoutIndex).setVisibility(View.INVISIBLE);
                            bcvLayout.getChildAt(bcvIndex).setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }


    }

    private void hide() {
        hidden = !hidden;


        if (hidden) {
            roundHeaderButton.setWidth(minusButtonLayout.getMeasuredWidth());
        } else {
            roundHeaderButton.setWidth(mWidthUnit);
        }

        if (nextRoundLayout != null) {
            nextRoundLayout.resize(hidden);
        }
        if (prevRoundLayout != null && prevRoundLayout.isHidden()) {
            prevRoundLayout.initializeLayouts(prevRoundLayout.getFullRoundSize(), prevRoundLayout.getSizeMultiplier());
        }
        initializeLayouts(fullRoundSize, sizeMultiplier);
    }

    /**
     * resizes layout, then calls the next rounds rezise <br>
     * if reduce is true then earlier round has been hidden and layout size should be reduced<br>
     * if false then earlier round has been unhidden and layout should be increased
     *
     * @param reduce true then reduce size, false then increase size
     */
    private void resize(boolean reduce) {


        //expand layout
        if (!reduce) {
            //previous round has same dimension
            if (prevRoundLayout != null && prevRoundLayout.fullRoundSize == fullRoundSize) {
                sizeMultiplier = prevRoundLayout.sizeMultiplier;
            }
            //previous round has greater dimension
            //previous round is hidden
            else if (prevRoundLayout != null && prevRoundLayout.fullRoundSize > fullRoundSize && prevRoundLayout.isHidden()) {
                sizeMultiplier = prevRoundLayout.sizeMultiplier;
            }
            //previous round is not hidden
            else {
                sizeMultiplier = prevRoundLayout.sizeMultiplier + 1;
            }
        }


        //shrink layout
        if (reduce) {


            //previous round has same dimension
            if (prevRoundLayout != null && prevRoundLayout.fullRoundSize == fullRoundSize) {
                sizeMultiplier = prevRoundLayout.sizeMultiplier;
            }
            //previous round has greater dimension
            //previous round is hidden
            else if (prevRoundLayout != null && prevRoundLayout.fullRoundSize > fullRoundSize && prevRoundLayout.isHidden()) {
                sizeMultiplier = prevRoundLayout.sizeMultiplier;
            }
            //previous round is not hidden
            else {
                sizeMultiplier = prevRoundLayout.sizeMultiplier + 1;
            }

        }

        if (nextRoundLayout != null) {
            nextRoundLayout.resize(reduce);
        }

        initializeLayouts(fullRoundSize, sizeMultiplier);

    }


    public void updateMatchViews() {

        int iterator = 0;
        for (int j = 0; j < matchLayout.getChildCount() && iterator < round.getMatchList().size(); j++) {
            //must only set info for the visible constraintlayout/ignore unused matches
            if (matchLayout.getChildAt(j) instanceof ConstraintLayout && matchLayout.getChildAt(j).getVisibility() == View.VISIBLE) {
                setMatchViewInfo((MatchView) matchLayout.getChildAt(j), round.getMatchList().get(iterator));
                iterator++;
            }
        }
    }

    private void setMatchViewInfo(MatchView matchView, Match match) {

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

        if (tournament.getState().equals(Tournament.COMPLETE)) {
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

    //TODO predicting bug where is someone reports a match on one device but doesnt not show on another device,
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
        View dialogueLayout = mInflater.inflate(R.layout.match_report_dialog, null);
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

    private void setReopenDialog(AlertDialog dialog, Match match) {
        View dialogueLayout = mInflater.inflate(R.layout.match_reopen_dialog, null);
        EditText reopenPrompt = dialogueLayout.findViewById(R.id.match_reopen_prompt);
        LinearLayout errorsLayout = dialogueLayout.findViewById(R.id.match_reopen_error_layout);
        dialog.setView(dialogueLayout);
        dialog.setOnShowListener(dialog1 -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                if (reopenPrompt.getText().toString().equals(Tournament.REOPEN)) {
                    sendMatchReopenRequest(match, errorsLayout, dialog);
                } else {
                    TextView error = (TextView) mInflater.inflate(R.layout.menu_spinner_item, null);
                    error.setText("Type 'Reopen' with no spaces or other punctuation on either side");
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            });
        });
    }

    private void sendMatchReopenRequest(Match match, LinearLayout errorsLayout, AlertDialog dialog) {
        dialog.dismiss();
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                fragment.newRefresh();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    TextView error = (TextView) mInflater.inflate(R.layout.menu_spinner_item, null);
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
                fragment.newRefresh();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {
                errorsLayout.removeAllViews();
                for (int i = 0; i < errorList.size(); i++) {
                    TextView error = (TextView) mInflater.inflate(R.layout.menu_spinner_item, null);
                    error.setText(String.valueOf(errorList.get(i)));
                    error.setSelected(true);
                    error.setTextColor(Color.RED);
                    errorsLayout.addView(error);
                }
            }
        }, ChallongeRequests.matchUpdate(match));
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
    private BracketConnectorView makeBCVComponent(double heightMultiplier, int mode) {
        BracketConnectorView bcv = new BracketConnectorView(getContext(), null, mHeightUnit * (int) Math.pow(2, heightMultiplier), mode, null);
        return bcv;
    }


    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    public LinearLayout getMinusButtonLayout() {
        return minusButtonLayout;
    }

    public void setMinusButtonLayout(LinearLayout minusButtonLayout) {
        this.minusButtonLayout = minusButtonLayout;
    }

    public LinearLayout getMatchLayout() {
        return matchLayout;
    }

    public void setMatchLayout(LinearLayout matchLayout) {
        this.matchLayout = matchLayout;
    }

    public LinearLayout getBcvLayout() {
        return bcvLayout;
    }

    public void setBcvLayout(LinearLayout bcvLayout) {
        this.bcvLayout = bcvLayout;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Button getRoundHeaderButton() {
        return roundHeaderButton;
    }

    public void setRoundHeaderButton(Button roundHeaderButton) {
        this.roundHeaderButton = roundHeaderButton;
    }

    public int getSizeMultiplier() {
        return sizeMultiplier;
    }

    public void setSizeMultiplier(int sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }

    public int getFullRoundSize() {
        return fullRoundSize;
    }

    public void setFullRoundSize(int fullRoundSize) {
        this.fullRoundSize = fullRoundSize;
    }

    public Round getPrevRound() {
        return prevRound;
    }

    public void setPrevRound(Round prevRound) {
        this.prevRound = prevRound;
    }

    public Round getNextRound() {
        return nextRound;
    }

    public void setNextRound(Round nextRound) {
        this.nextRound = nextRound;
    }

    public static Tournament getTournament() {
        return tournament;
    }

    public static void setTournament(Tournament tournament) {
        RoundLayout.tournament = tournament;
    }

    public static BracketFragment getFragment() {
        return fragment;
    }

    public static void setFragment(BracketFragment fragment) {
        RoundLayout.fragment = fragment;
    }

    public RoundLayout getPrevRoundLayout() {
        return prevRoundLayout;
    }

    public void setPrevRoundLayout(RoundLayout prevRoundLayout) {
        this.prevRoundLayout = prevRoundLayout;
    }

    public RoundLayout getNextRoundLayout() {
        return nextRoundLayout;
    }

    public void setNextRoundLayout(RoundLayout nextRoundLayout) {
        this.nextRoundLayout = nextRoundLayout;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getPreRoundMultiplier() {
        return preRoundMultiplier;
    }

    public void setPreRoundMultiplier(int preRoundMultiplier) {
        this.preRoundMultiplier = preRoundMultiplier;
    }
}
