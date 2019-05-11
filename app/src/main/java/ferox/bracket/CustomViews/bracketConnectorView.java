package ferox.bracket.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import ferox.bracket.R;

public class bracketConnectorView extends View {

    public final static int MODE_TEST = 0;
    public final static int MODE_TOP = 1;
    public final static int MODE_MIDDLE = 2;
    public final static int MODE_BOTTOM = 3;


    //Canvas mCanvas;
    Paint mPaint;
    int tX,tY,bX,bY,eX,eY;
    int heightInput;
    int mode;
    int color;



    public bracketConnectorView(Context context, @Nullable AttributeSet attrs, int heightInput, int mode, String lol) {
        super(context, attrs);
        this.heightInput = heightInput;
        this.mode = mode;
        //mCanvas = new Canvas();
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.menu_title));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

    }




    @Override
    public void onDraw(Canvas canvas){
        //draws lines connecting matches
        //Normal to connect two matches in one round to one match in next
        //Top connects one match that is displayed higher to next match
        //Bottom connects one match that is displayed lower to next
        //Mid connects one match that is displayed on the same level to next

        if (mode == MODE_TEST) {

            canvas.drawLine(0, 0,
                    (500), 500, mPaint);

            canvas.drawLine(this.getLeft(), this.getBottom(),
                    (this.getRight() + this.getLeft()) / 2, this.getBottom(), mPaint);


        }
        if (mode == MODE_TOP) {
            canvas.drawLine(0, 0, getWidth() / 2, 0, mPaint);

            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaint);

            canvas.drawLine(getWidth() / 2, getHeight(), getWidth(), getHeight(), mPaint);
        }
        if (mode == MODE_BOTTOM) {
            canvas.drawLine(0, getHeight(), getWidth() / 2, getHeight(), mPaint);

            canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, 0, mPaint);
            canvas.drawLine(getWidth() / 2, 0, getWidth(), 0, mPaint);
        }
        if (mode == MODE_MIDDLE) {
            canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);
        }



    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int width = getResources().getDimensionPixelSize(R.dimen.bcv_width);
        int height = heightInput;
        int desiredWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int desiredHSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        super.onMeasure(desiredWSpec, desiredHSpec);
    }
}
