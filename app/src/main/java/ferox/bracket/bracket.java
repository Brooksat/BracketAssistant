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
    MatchPairs mp;
    ArrayList<match> mMatchList;
    int postQualRound;


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

        mp = new MatchPairs(this);
        mp.sendGetParticipants();


        ///matchlist.size is size of round one from matchpairs result, still need to set challongerequest to be able to return any needed api url
        makeBracketDisplay(numRoundW, numRoundL, matchList.size(), this);

    }


    public void makeBracketDisplay(int numRoundW, int numRoundL, int matchesInRound1,
                                   final Context context) {

        ConstraintLayout bracketRoot = findViewById(R.id.bracket_root);
        LinearLayout roundWinners = findViewById(R.id.round_winners);
        LinearLayout bracketWinners = findViewById(R.id.bracket_winners);
        LinearLayout roundLosers = findViewById(R.id.round_losers);
        LinearLayout bracketLosers = findViewById(R.id.bracket_losers);

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
        int numberRound = matchesInRound1;
        for (int i = 0; numberRound != 0; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout space = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            linearLayout.setLayoutParams(layoutParams);
            space.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);


            for (int j = 0; j < numberRound; j++) {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ConstraintLayout match = (ConstraintLayout) inflater.inflate(R.layout.match, null);
                TextView textView1 = match.findViewById(R.id.matchNumber);
                textView1.setText(Integer.toString(j));
                linearLayout.addView(match);
                Space filler = new Space(this);
                filler.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getResources().getDimensionPixelSize(R.dimen.match_height) / 2) * ((int) Math.pow(2, i))));
                linearLayout.addView(filler);
            }
            numberRound = numberRound / 2;
            bracketWinners.addView(linearLayout);


            bracketConnectorView bcv = new bracketConnectorView(this, null, (int) Math.pow(2, i), "");
            //bcv.setBackgroundResource(R.drawable.outline);
            space.addView(bcv);
            bracketWinners.addView(space);


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
}

