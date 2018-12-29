package ferox.twoactivities;

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

    Canvas mCanvas;
    Paint mPaint;
    int tX,tY,bX,bY,eX,eY;
    int heightMultiplier;
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
    public bracketConnectorView(Context context, @Nullable AttributeSet attrs,int heightMultiplier, String lol){
        super(context, attrs);
        this.heightMultiplier = heightMultiplier;
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

        //canvas.drawLine(this.getX(),tY,eX,eY,mPaint);
        canvas.drawLine(this.getX(),this.getY(),
                (this.getRight()+this.getLeft())/2,this.getY(),mPaint);

        canvas.drawLine(this.getLeft(), this.getBottom(),
                (this.getRight()+this.getLeft())/2,this.getBottom(),mPaint);

        canvas.drawLine((this.getRight()+this.getLeft())/2, this.getY(),
                (this.getRight()+this.getLeft())/2, this.getBottom(), mPaint);

        canvas.drawLine((this.getRight()+this.getLeft())/2, (this.getTop()+this.getBottom())/2,
                this.getRight(),this.getTop()+this.getBottom()/2, mPaint);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        setMeasuredDimension(  getResources().getDimensionPixelSize(R.dimen.match_width),  getResources().getDimensionPixelSize(R.dimen.match_height)*heightMultiplier);
    }
}
