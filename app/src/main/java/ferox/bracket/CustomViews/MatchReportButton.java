package ferox.bracket.CustomViews;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import ferox.bracket.R;

public class MatchReportButton extends AppCompatButton {

    boolean won;

    public MatchReportButton(Context context) {
        super(context);
    }

    public MatchReportButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatchReportButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
        if (won) {
            setBackgroundTintList(getResources().getColorStateList(R.color.menu_title));
        } else {
            setBackgroundTintList(getResources().getColorStateList(R.color.menu_background_light));
        }
    }
}
