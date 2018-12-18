package ferox.twoactivities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class matchView extends RelativeLayout {

    Context mContext;
    String mMatchNumber;
    String mP1Seed;
    String mP2Seed;
    String mP1Name = "";
    String mP2Name = "";

    public matchView(Context context) {
        super(context);

        init(context, null);
    }

    public matchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public matchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public matchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        final TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.matchView);

        mMatchNumber = ta.getString(R.styleable.matchView_matchNumber);
        mP1Seed = ta.getString(R.styleable.matchView_player1Seed);
        mP2Seed = ta.getString(R.styleable.matchView_player2Seed);
        mP1Name = ta.getString(R.styleable.matchView_player1Name);
        mP2Name = ta.getString(R.styleable.matchView_player2Name);

        ta.recycle();

        getMatchView(mMatchNumber, mP1Seed, mP2Seed, mP1Name, mP2Name);
    }


    private void getMatchView(String matchNumber, String P1Seed, String P2Seed, String P1Name, String P2Name){

        System.out.println(matchNumber);
        System.out.println(P1Seed);
        System.out.println(P2Seed);
        System.out.println(P1Name);
        System.out.println(P2Name);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.match,this, false);

        TextView textView1 = constraintLayout.findViewById(R.id.matchNumber);
        textView1.setText(matchNumber);
        TextView textView2 = constraintLayout.findViewById(R.id.seed1);
        textView2.setText(P1Seed);
        TextView textView3 = constraintLayout.findViewById(R.id.seed2);
        textView3.setText(P2Seed);
        TextView textView4 = constraintLayout.findViewById(R.id.participant1);
        textView4.setText(P1Name);
        TextView textView5 = constraintLayout.findViewById(R.id.participant2);
        textView5.setText(P2Name);

        addView(constraintLayout);
        invalidate();
        requestLayout();
    }
}