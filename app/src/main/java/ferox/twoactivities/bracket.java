package ferox.twoactivities;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class bracket extends AppCompatActivity {

    int numRoundW;
    int numRoundL;
    int matchesInRound1;
    Canvas mCanvas;
    Paint mPaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracket);

        numRoundW = 3;
        numRoundL = 6;
        matchesInRound1 =8;
        makeBracketDisplay(numRoundW, numRoundL, matchesInRound1, this);

    }

    public void makeBracketDisplay(int numRoundW, int numRoundL, int matchesInRound1, final Context context) {

        ConstraintLayout bracketRoot = findViewById(R.id.bracket_root);
        LinearLayout roundWinners = findViewById(R.id.round_winners);
        LinearLayout bracketWinners = findViewById(R.id.bracket_winners);
        LinearLayout roundLosers = findViewById(R.id.round_losers);
        LinearLayout bracketLosers = findViewById(R.id.bracket_losers);
        //layout params
        ViewGroup.MarginLayoutParams roundHeaderLayoutParams = new ViewGroup.MarginLayoutParams(
                getResources().getDimensionPixelSize(R.dimen.round_header_width), getResources().getDimensionPixelSize(R.dimen.round_header_height));
        roundHeaderLayoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.bracket_padding), 0);


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
            layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.bracket_padding), 0);
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
                filler.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(getResources().getDimensionPixelSize(R.dimen.match_height)/2)*(i+1)));
                linearLayout.addView(filler);
            }
            numberRound = numberRound/2;
            bracketWinners.addView(linearLayout);




            bracketConnectorView bcv = new bracketConnectorView(this, null,i+1, "");
            //bcv.setBackgroundResource(R.drawable.outline);
            space.addView(bcv);
            bracketWinners.addView(space);





        }
    }




}
