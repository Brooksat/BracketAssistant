package ferox.bracket.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import ferox.bracket.R;
import ferox.bracket.Tournament.Match;


public class MatchView extends ConstraintLayout {

    Context mContext;
    String mMatchId;
    String mMatchNumber;
    String mP1Seed;
    String mP2Seed;
    String mP1Name = "";
    String mP2Name = "";
    TextView MatchNumber;
    TextView P1Seed;
    TextView P2Seed;
    TextView P1Name;
    TextView P2Name;
    TextView P1MatchScore;
    TextView P2MatchScore;
    Match match;


    public MatchView(Context context) {
        super(context);
        Log.d("MatchConstructor1", "Called");
        init(context, null);

    }

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("MatchConstructor2", "Called");

    }

    public MatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("MatchConstructor3", "Called");
        init(context, attrs);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

//        final TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MatchView);
//
//        mMatchNumber = ta.getString(R.styleable.MatchView_matchNumber);
//        mP1Seed = ta.getString(R.styleable.MatchView_player1Seed);
//        mP2Seed = ta.getString(R.styleable.MatchView_player2Seed);
//        mP1Name = ta.getString(R.styleable.MatchView_player1Name);
//        mP2Name = ta.getString(R.styleable.MatchView_player2Name);
//
//        ta.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.match, this, true);
        MatchNumber = findViewById(R.id.matchNumber);
        P1Seed = findViewById(R.id.player1_seed);
        P2Seed = findViewById(R.id.player2_seed);
        P1Name = findViewById(R.id.player1_name);
        P2Name = findViewById(R.id.player2_name);
        P1MatchScore = findViewById(R.id.player1_score);
        P2MatchScore = findViewById(R.id.player2_score);
        mMatchId = "";


        // getMatchView(mMatchNumber, mP1Seed, mP2Seed, mP1Name, mP2Name);
    }


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

//        int width = getResources().getDimensionPixelSize(R.dimen.match_width);
//        int height = getResources().getDimensionPixelSize(R.dimen.match_height);
//        int desiredWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        int desiredHSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        setMeasuredDimension(desiredWSpec, desiredHSpec);
//        super.onMeasure(desiredWSpec, desiredHSpec);
//    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        for(int i = 0 ; i < getChildCount() ; i++){
//            getChildAt(i).layout(left, top, right, bottom);
//        }
//    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getmMatchId() {
        return mMatchId;
    }

    public void setmMatchId(String mMatchId) {
        this.mMatchId = mMatchId;
    }

    public String getmMatchNumber() {
        return mMatchNumber;
    }

    public void setmMatchNumber(String mMatchNumber) {
        this.mMatchNumber = mMatchNumber;
    }

    public String getmP1Seed() {
        return mP1Seed;
    }

    public void setmP1Seed(String mP1Seed) {
        this.mP1Seed = mP1Seed;
    }

    public String getmP2Seed() {
        return mP2Seed;
    }

    public void setmP2Seed(String mP2Seed) {
        this.mP2Seed = mP2Seed;
    }

    public String getmP1Name() {
        return mP1Name;
    }

    public void setmP1Name(String mP1Name) {
        this.mP1Name = mP1Name;
    }

    public String getmP2Name() {
        return mP2Name;
    }

    public void setmP2Name(String mP2Name) {
        this.mP2Name = mP2Name;
    }

    public TextView getMatchNumber() {
        return MatchNumber;
    }

    public void setMatchNumber(TextView matchNumber) {
        this.MatchNumber = matchNumber;
    }

    public TextView getP1Seed() {
        return P1Seed;
    }

    public void setP1Seed(TextView p1Seed) {
        P1Seed = p1Seed;
    }

    public TextView getP2Seed() {
        return P2Seed;
    }

    public void setP2Seed(TextView p2Seed) {
        P2Seed = p2Seed;
    }

    public TextView getP1Name() {
        return P1Name;
    }

    public void setP1Name(TextView p1Name) {
        P1Name = p1Name;
    }

    public TextView getP2Name() {
        return P2Name;
    }

    public void setP2Name(TextView p2Name) {
        P2Name = p2Name;
    }

    public TextView getP1MatchScore() {
        return P1MatchScore;
    }

    public void setP1MatchScore(TextView p1MatchScore) {
        P1MatchScore = p1MatchScore;
    }

    public TextView getP2MatchScore() {
        return P2MatchScore;
    }

    public void setP2MatchScore(TextView p2MatchScore) {
        P2MatchScore = p2MatchScore;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}