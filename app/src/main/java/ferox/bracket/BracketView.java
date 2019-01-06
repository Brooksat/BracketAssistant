package ferox.bracket;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


public class BracketView extends ConstraintLayout {


    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1f;
    final static float mMinZoom = 1f;
    final static float mMaxZoom = 5.0f;
    int screenWidth;
    int screenHeight;


//    public BracketView(Context context) {
//        //not called?
//        super(context);
//        setWillNotDraw(false);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }

    public BracketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;


    }

//    public BracketView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setWillNotDraw(false);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int childSumWidth = 0;
        int childSumHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth(),
                    childHeight = child.getMeasuredHeight();
            childSumWidth += childWidth;
            childSumHeight += childHeight;
        }


        int desiredWSpec = MeasureSpec.makeMeasureSpec(childSumWidth, MeasureSpec.UNSPECIFIED);
        int desiredHSpec = MeasureSpec.makeMeasureSpec(childSumHeight, MeasureSpec.UNSPECIFIED);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        super.onMeasure(desiredWSpec, desiredHSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPosX * -1 < 0) {
            mPosX = 0;
        } else if (mPosX * -1 > getWidth() * mScaleFactor - screenWidth + 300) {
            mPosX = (getWidth() * mScaleFactor - screenWidth + 300) * -1;
        }
        if (mPosY * -1 < 0) {
            mPosY = 0;
        } else if (mPosY * -1 > getHeight() * mScaleFactor - screenHeight + 200) {
            mPosY = (getHeight() * mScaleFactor - screenHeight + 200) * -1;
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