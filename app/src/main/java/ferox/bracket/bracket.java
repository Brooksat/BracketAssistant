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
    int doublePostQualRound;
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

        //layout params
        ViewGroup.MarginLayoutParams roundHeaderLayoutParams = new ViewGroup.MarginLayoutParams(
                mWidthUnit * 2, getResources().getDimensionPixelSize(R.dimen.round_header_height));
        roundHeaderLayoutParams.setMargins(0, 0, 0, 0);


        //adds round headers
        for (int i = 0; i < numRoundW; i++) {
            TextView roundNumber = new TextView(this);
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            roundNumber.setText("Round " + i);
            roundWinners.addView(roundNumber);

        }
        for (int i = 0; i < numRoundL; i++) {
            TextView roundNumber = new TextView(this);
            roundNumber.setLayoutParams(roundHeaderLayoutParams);
            roundNumber.setGravity(Gravity.CENTER);
            roundNumber.setText("Round " + i);
            roundLosers.addView(roundNumber);
        }
        //bracket winners


        //adds winners rounds
        int numberRound = qualifyRound == 0 ? postQualRound : doublePostQualRound;
        for (int i = 0; numberRound != 0; i++) {
            makeRound(numberRound, i, bracketWinners, 0);


            if (numberRound == 1) {
                for (int j = 0; j < 2; j++) {
                    makeRound(numberRound, i, bracketWinners, 1);
                }
            }
            numberRound = numberRound / 2;
        }


        setPostQualRoundInfo();
        if (qualifyRound != 0) {
            setUnusedMatchesInvisible();
        }
        //make round for "losers qualifying round"
        makeRound(numOfLR1, 0, bracketLosers, bracketConnectorView.MODE_MIDDLE);


    }



    //adds two layouts to view one is list of matchees the other the bracket connector views
//mulitplier for sizing based on round
    public void makeRound(int numMatches, int multiplier, ViewGroup vg, int modifier) {

        LinearLayout matches = new LinearLayout(this);
        LinearLayout bracketConnector = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        matches.setLayoutParams(layoutParams);
        bracketConnector.setLayoutParams(layoutParams);
        matches.setOrientation(LinearLayout.VERTICAL);
        bracketConnector.setOrientation(LinearLayout.VERTICAL);
        matches.setGravity(Gravity.CENTER);


//Matches

        if (multiplier != 0) {
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2)));
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


//Bracket connector


        for (int i = 0; i < numMatches / 2; i++) {
            Space space = new Space(this);
            Space space2 = new Space(this);
            View space3 = new View(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
            space2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 1)));
            space3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * 2));

            bracketConnectorView bcv = new bracketConnectorView(this, null
                    , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                    , bracketConnectorView.MODE_TOP
                    , "", i + 1);
            bracketConnectorView bcv2 = new bracketConnectorView(this, null
                    , mHeightUnit * (int) Math.pow(2, multiplier + 1)
                    , bracketConnectorView.MODE_BOTTOM
                    , "", i + 1);


            bracketConnector.addView(space);
            bracketConnector.addView(bcv);
            bracketConnector.addView(bcv2);
            bracketConnector.addView(space2);
            bracketConnector.addView(space3);

        }
        if (modifier == 1) {
            bracketConnectorView bcvr = new bracketConnectorView(this, null,
                    mHeightUnit * 2,
                    bracketConnectorView.MODE_MIDDLE,
                    "",
                    0);

            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    mHeightUnit * (int) (Math.pow(2, multiplier + 1) - 2)));
            bracketConnector.addView(space);
            bracketConnector.addView(bcvr);
            vg.addView(bracketConnector);
            vg.addView(matches);
        } else {
            vg.addView(matches);
            vg.addView(bracketConnector);
        }


    }

    private void setPostQualRoundInfo() {
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

    private void setUnusedMatchesInvisible() {
        ViewGroup bracketWinners = findViewById(R.id.bracket_winners);
        ViewGroup QR = (LinearLayout) bracketWinners.getChildAt(0);
        int iterator = 0;
        for (int i = 0; i < postQualRound; i++) {
            if (matchList.get(i).isP1Decided() == true) {
                QR.getChildAt(4 * i).setVisibility(View.INVISIBLE);
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

    public void setDoublePostQualRound(int doublePostQualRound) {
        this.doublePostQualRound = doublePostQualRound;
    }
}

