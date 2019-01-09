package ferox.bracket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class bracketConnectorView extends View {

    final static int MODE_NORMAL = 0;
    final static int MODE_TOP = 1;
    final static int MODE_MIDDLE = 2;
    final static int MODE_BOTTOM = 3;


    Canvas mCanvas;
    Paint mPaint;
    int tX,tY,bX,bY,eX,eY;
    int heightMultiplier;
    int mode;
    public bracketConnectorView(Context context) {
        super(context);
        init(context, null);
    }

    public bracketConnectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public bracketConnectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public bracketConnectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public bracketConnectorView(Context context, @Nullable AttributeSet attrs, int heightMultiplier, int mode, String lol) {
        super(context, attrs);
        this.heightMultiplier = heightMultiplier;
        this.mode = mode;
        mCanvas = new Canvas();
        mPaint = new Paint(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

    }



    private void init(Context context, AttributeSet attrs){

    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //draws lines connecting matches
        //Normal to connect two matches in one round to one match in next
        //Top connects one match that is displayed higher to next match
        //Bottom connects one match that is displayed lower to next
        //Mid connects one match that is displayed on the same level to next
        if (mode == MODE_NORMAL) {

            canvas.drawLine(this.getX(), this.getY(),
                    (this.getRight() + this.getLeft()) / 2, this.getY(), mPaint);

            canvas.drawLine(this.getLeft(), this.getBottom(),
                    (this.getRight() + this.getLeft()) / 2, this.getBottom(), mPaint);

            canvas.drawLine((this.getRight() + this.getLeft()) / 2, this.getY(),
                    (this.getRight() + this.getLeft()) / 2, this.getBottom(), mPaint);

            canvas.drawLine((this.getRight() + this.getLeft()) / 2, (this.getTop() + this.getBottom()) / 2,
                    this.getRight(), this.getTop() + this.getBottom() / 2, mPaint);
        } else if (mode == MODE_TOP) {
            canvas.drawLine(this.getX(), this.getY(),
                    (this.getRight() + this.getLeft()) / 2, this.getY(), mPaint);
            canvas.drawLine((this.getRight() + this.getLeft()) / 2, this.getY(),
                    (this.getRight() + this.getLeft()) / 2, (this.getBottom() + this.getTop()) / 2, mPaint);
            canvas.drawLine((this.getRight() + this.getLeft()) / 2, (this.getTop() + this.getBottom()) / 2,
                    this.getRight(), this.getTop() + this.getBottom() / 2, mPaint);
        } else if (mode == MODE_BOTTOM) {
            canvas.drawLine(this.getLeft(), this.getBottom(),
                    (this.getRight() + this.getLeft()) / 2, this.getBottom(), mPaint);

            canvas.drawLine((this.getRight() + this.getLeft()) / 2, this.getBottom(),
                    (this.getRight() + this.getLeft()) / 2, (this.getBottom() + this.getTop()) / 2, mPaint);
            canvas.drawLine((this.getRight() + this.getLeft()) / 2, (this.getTop() + this.getBottom()) / 2,
                    this.getRight(), this.getTop() + this.getBottom() / 2, mPaint);
        } else if (mode == MODE_MIDDLE) {
            canvas.drawLine(this.getX(), (this.getTop() + this.getBottom()) / 2,
                    this.getRight(), this.getTop() + this.getBottom() / 2, mPaint);
        }



    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){


        int width = getResources().getDimensionPixelSize(R.dimen.match_width);
        int height = getResources().getDimensionPixelSize(R.dimen.match_height) * heightMultiplier;
        int desiredWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        int desiredHSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        //super.onMeasure(desiredWSpec,desiredHSpec);
    }
}
