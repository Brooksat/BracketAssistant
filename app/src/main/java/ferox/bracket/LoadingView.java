package ferox.bracket;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class LoadingView extends android.support.v7.widget.AppCompatTextView {
    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateAnimation(){
        String s = this.getText().toString();
        if(s.length()<6){
            s += ". ";
        }
        else {
            s = "";
        }

        this.setText(s);
        this.invalidate();
    }
}
