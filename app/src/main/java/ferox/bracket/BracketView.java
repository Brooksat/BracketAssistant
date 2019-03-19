package ferox.bracket;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;


public class BracketView extends ConstraintLayout {


    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1f;
    final static float mMinZoom = 0.5f;
    final static float mMaxZoom = 5.0f;
    int screenWidth;
    int screenHeight;
    int statusBarHeight;
    int navigationBarHeight;
    Context mContext;


//    public BracketView(Context context) {
//        //not called?
//        super(context);
//        setWillNotDraw(false);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }

    public BracketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        init();
    }

    private void init() {

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int resourceId2 = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            Log.d("statusBarHeight", String.valueOf(statusBarHeight));
        }

        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId2);
            Log.d("navigationBarHeight", String.valueOf(navigationBarHeight));
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int childSumWidth = 0;
        int childSumHeight = 0;
        int childHspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int childWspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);


        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(childWspec, childHspec);
            int childWidth = child.getMeasuredWidth(),
                    childHeight = child.getMeasuredHeight();
            childSumWidth += childWidth;
            childSumHeight += childHeight;
        }


        int desiredWSpec = MeasureSpec.makeMeasureSpec(childSumWidth, MeasureSpec.UNSPECIFIED);
        int desiredHSpec = MeasureSpec.makeMeasureSpec(childSumHeight + statusBarHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        super.onMeasure(desiredWSpec, desiredHSpec);
    }

    public void montitor(TextView tv1, TextView tv2, TextView tv3) {
        tv1.setText(String.valueOf(mPosY * -1));
        tv2.setText(String.valueOf(getHeight() * mScaleFactor));
        tv3.setText(String.valueOf(getHeight() * mScaleFactor - (mPosY * -1)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPosX * -1 < 0) {
            mPosX = 0;
        } else if (mPosX * -1 > getWidth() * mScaleFactor - screenWidth + 300 && (getWidth() * mScaleFactor > screenWidth)) {
            mPosX = (getWidth() * mScaleFactor - screenWidth + 300) * -1;
        } else if ((getWidth() * mScaleFactor < screenWidth)) {
            mPosX = 0;
        }

        //stops bracket from scrolling too far down
        if (mPosY * -1 < 0) {
            mPosY = 0;
        }
        //stops bracket from scrolling too far up
        else if (mPosY * -1 > getHeight() * mScaleFactor - screenHeight && (getHeight() * mScaleFactor > screenHeight)) {
            mPosY = (getHeight() * mScaleFactor - screenHeight) * -1;
        } else if ((getHeight() * mScaleFactor < screenHeight)) {
            mPosY = 0;
        }


        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //scale detector inspects all events
        mScaleDetector.onTouchEvent(event);


        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                //get coordintates where screeen was touches
                final float x = event.getX();
                final float y = event.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                //save the ID of the pointer
                mActivePointerId = event.getPointerId(0);

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);

                //get  coordinates of the active pointer index
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);


                if (!mScaleDetector.isInProgress()) {

                    //calculate distance in x/y diractions
                    final float distX = x - mLastTouchX;
                    final float distY = y - mLastTouchY;

                    mPosX += distX;
                    mPosY += distY;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;


                break;

            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                //Extract the index of the pointer that left the screen
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    //if active pointer left screen set new active pointer
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }


        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            mScaleFactor = Math.max(mMinZoom, Math.min(mMaxZoom, mScaleFactor));
            invalidate();
            return true;
        }
    }


}