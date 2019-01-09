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
        matchList = new ArrayList<match>();
        bv = findViewById(R.id.bracket_root);

        mp = new MatchList(this);
        mp.sendGetParticipants();


        ///matchlist.size is size of round one from matchpairs result, still need to set challongerequest to be able to return any needed api url
        makeBracketDisplay(numRoundW, numRoundL, this);

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
                getResources().getDimensionPixelSize(R.dimen.round_header_width), getResources().getDimensionPixelSize(R.dimen.round_header_height));
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
            makeRound(numberRound, i, bracketWinners, bracketConnectorView.MODE_TOP);
            numberRound = numberRound / 2;
        }
        setPostQualRoundInfo();
        if (qualifyRound != 0) {
            setUnusedMatchesInvisible();
        }
        //make round for "losers qualifying round"
        makeRound(numOfLR1, 0, bracketLosers, bracketConnectorView.MODE_MIDDLE);


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

    private void setPostQualRoundInfo() {
        ViewGroup bracketWinners = findViewById(R.id.bracket_winners);
        ViewGroup PQR = (LinearLayout) bracketWinners.getChildAt(qualifyRound == 0 ? 0 : 2);
        for (int i = 0; i < postQualRound; i++) {
            ConstraintLayout match = (ConstraintLayout) PQR.getChildAt(2 * i);
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

    //adds two layouts to view one is list of matchees the other the bracket connector views
//mulitplier for sizing based on round
    public void makeRound(int numMatches, int multiplier, ViewGroup vg, int mode) {
        LinearLayout bracketWinners = findViewById(R.id.bracket_winners);


        LinearLayout matches = new LinearLayout(this);
        LinearLayout bracketConnector = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        matches.setLayoutParams(layoutParams);
        bracketConnector.setLayoutParams(layoutParams);
        matches.setOrientation(LinearLayout.VERTICAL);
        matches.setGravity(Gravity.CENTER);


        for (int j = 0; j < numMatches; j++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConstraintLayout match = (ConstraintLayout) inflater.inflate(R.layout.match, null);
            TextView textView1 = match.findViewById(R.id.matchNumber);
            textView1.setText(Integer.toString(j));
            matches.addView(match);
            Space filler = new Space(this);
            filler.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getResources().getDimensionPixelSize(R.dimen.match_height) / 2) * ((int) Math.pow(2, multiplier))));
            matches.addView(filler);
        }

        vg.addView(matches);


        bracketConnectorView bcv = new bracketConnectorView(this, null, (int) Math.pow(2, multiplier), mode, "");
        //bcv.setBackgroundResource(R.drawable.outline);
        bracketConnector.addView(bcv);
        vg.addView(bracketConnector);

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

