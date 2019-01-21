package ferox.bracket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;

public class bracket extends AppCompatActivity {

    int numRoundW;
    int numRoundL;
    int matchesInRound1;
    Canvas mCanvas;
    Paint mPaint;
    BracketView bv;
    MatchList mp;
    ArrayList<match> mMatchList;
    int postQualRound;
    int qualifyRound;
    int numOfLR1;
    int mHeightUnit;
    int mWidthUnit;

    ArrayList<match> matchList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracket);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        numRoundW = 3;
        numRoundL = 6;
        matchesInRound1 = 10;

        mHeightUnit = getResources().getDimensionPixelSize(R.dimen.match_height) / 2;
        mWidthUnit = getResources().getDimensionPixelSize(R.dimen.match_width);


        matchList = new ArrayList<match>();
        bv = findViewById(R.id.bracket_root);

        mp = new MatchList(this);
        mp.sendGetParticipants();


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

            if (numberRound == 1) {
                makeGrandFinals(i, bracketWinners);
            }
            numberRound = numberRound / 2;


        }


        setPostQualRoundInfo();
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

    public void makeBracketConnectors(int numMatches, int multiplier, ViewGroup vg, int modifier) {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout bracketConnector = new LinearLayout(this);
        bracketConnector.setLayoutParams(layoutParams);
        bracketConnector.setOrientation(LinearLayout.VERTICAL);
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
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + modifier)));
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

    public void makeRound(int numMatches, int multiplier, ViewGroup vg, int modifier) {

        LinearLayout matches = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        matches.setLayoutParams(layoutParams);

        matches.setOrientation(LinearLayout.VERTICAL);

        matches.setGravity(Gravity.CENTER);


//Matches

        if (multiplier != 0) {
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2 + modifier)));
            matches.addView(space);
        }

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
                ConstraintLayout match = (ConstraintLayout) PQR.getChildAt(2 * i + 1);
                TextView matchNumber = match.findViewById(R.id.matchNumber);
                matchNumber.setText(String.valueOf(matchList.get(i).number));
                TextView P1Seed = match.findViewById(R.id.seed1);
                P1Seed.setText(String.valueOf(matchList.get(i).p1Seed));
                TextView P2Seed = match.findViewById(R.id.seed2);
                P2Seed.setText(String.valueOf(matchList.get(i).p2Seed));
                TextView P1Name = match.findViewById(R.id.participant1);
                P1Name.setText(matchList.get(i).p1.name);
                TextView P2Name = match.findViewById(R.id.participant2);
                P2Name.setText(matchList.get(i).p2.name);
            }
        }

    }

    private void setUnusedMatchesInvisible() {
        ViewGroup bracketWinners = findViewById(R.id.bracket_winners);
        ViewGroup QR = (LinearLayout) bracketWinners.getChildAt(0);
        ViewGroup QRBC = (LinearLayout) bracketWinners.getChildAt(1);
        int iterator = 0;
        for (int i = 0; i < postQualRound; i++) {
            if (matchList.get(i).isP1Decided() == true) {
                QR.getChildAt(4 * i).setVisibility(View.INVISIBLE);
                QRBC.getChildAt(5 * i + 1).setVisibility(View.INVISIBLE);
            } else {
                ConstraintLayout match = (ConstraintLayout) QR.getChildAt(4 * i);
                TextView matchNumber = match.findViewById(R.id.matchNumber);
                matchNumber.setText(String.valueOf(matchList.get(postQualRound + iterator).number));
                TextView P1Seed = match.findViewById(R.id.seed1);
                P1Seed.setText(String.valueOf(matchList.get(postQualRound + iterator).p1Seed));
                TextView P2Seed = match.findViewById(R.id.seed2);
                P2Seed.setText(String.valueOf(matchList.get(postQualRound + iterator).p2Seed));
                TextView P1Name = match.findViewById(R.id.participant1);
                P1Name.setText(matchList.get(postQualRound + iterator).p1.name);
                TextView P2Name = match.findViewById(R.id.participant2);
                P2Name.setText(matchList.get(postQualRound + iterator).p2.name);
                ++iterator;
            }
            if (matchList.get(i).isP2Decided() == true) {
                QR.getChildAt(4 * i + 2).setVisibility(View.INVISIBLE);
                QRBC.getChildAt(5 * i + 2).setVisibility(View.INVISIBLE);
            } else {
                ConstraintLayout match = (ConstraintLayout) QR.getChildAt(4 * i + 2);
                TextView matchNumber = match.findViewById(R.id.matchNumber);
                matchNumber.setText(String.valueOf(matchList.get(postQualRound + iterator).number));
                TextView P1Seed = match.findViewById(R.id.seed1);
                P1Seed.setText(String.valueOf(matchList.get(postQualRound + iterator).p1Seed));
                TextView P2Seed = match.findViewById(R.id.seed2);
                P2Seed.setText(String.valueOf(matchList.get(postQualRound + iterator).p2Seed));
                TextView P1Name = match.findViewById(R.id.participant1);
                P1Name.setText(matchList.get(postQualRound + iterator).p1.name);
                TextView P2Name = match.findViewById(R.id.participant2);
                P2Name.setText(matchList.get(postQualRound + iterator).p2.name);
                ++iterator;
            }
        }
    }


    public ArrayList<match> getMatchList() {
        return matchList;
    }

    public void setMatchList(ArrayList<match> matchList) {
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


}

