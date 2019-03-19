package ferox.bracket;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class LoadingView extends androidx.appcompat.widget.AppCompatTextView {
    static final int DELAY = 800;
    Handler handler;
    Runnable runnable;
    boolean stop;

    public LoadingView(Context context) {
        super(context);
        stop = false;
        handler = new Handler();
        runnable = () -> {
            updateAnimation();
            if (!stop) {
                handler.postDelayed(runnable, DELAY);
            }
        };
        handler.postDelayed(runnable, DELAY);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
        runnable = () -> {
            updateAnimation();
            if (!stop) {
                handler.postDelayed(runnable, DELAY);
            }
        };
        handler.postDelayed(runnable, DELAY);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new Handler();
        runnable = () -> {
            updateAnimation();
            if (!stop) {
                handler.postDelayed(runnable, DELAY);
            }
        };
        handler.postDelayed(runnable, DELAY);
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

    public void hide() {
        handler.removeCallbacks(null);
        setVisibility(GONE);
    }
}
