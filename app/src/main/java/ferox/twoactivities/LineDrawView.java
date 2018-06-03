package ferox.twoactivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LineDrawView extends View {
    private int lineDrawViewColor;
    Paint paint;
    View startView;
    View endView;
    Canvas canvas;

    public LineDrawView(Context context, View startView, View endView) {
        super(context);
        init(null, 0, startView, endView);

    }

    public LineDrawView(Context context, AttributeSet attrs, View startView, View endView){
        super(context, attrs);
        init(attrs, 0, startView, endView );
    }

    public LineDrawView(Context context, AttributeSet attrs, int defStyle, View startView, View endView){
        super(context, attrs, defStyle);
        init(attrs, defStyle, startView, endView);
    }



    private void init(@Nullable AttributeSet set, int defSytle, View startView, View endView){
        paint = new Paint();

        if(startView != null && endView != null)
        {
            this.startView = startView;
            this.endView = endView;
        }

        if(set == null){
            return;
        }

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.LineDrawView);
        lineDrawViewColor = ta.getColor(R.styleable.LineDrawView_line_color, Color.BLUE);
        paint.setColor(lineDrawViewColor);

        ta.recycle();
    }

    @SuppressLint("NewApi")
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.drawLine(startView.getX()+50, startView.getY(), endView.getX()+50, endView.getY(), paint);
    }
}
