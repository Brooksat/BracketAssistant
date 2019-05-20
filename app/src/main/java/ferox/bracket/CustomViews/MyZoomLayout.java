package ferox.bracket.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyZoomLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "MyZoomLayout";

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    final static float MIN_ZOOM = 0.3f;
    final static float MAX_ZOOM = 2.0f;

    private Mode mode = Mode.NONE;
    float scale = 1.0f;
    private float lastScaleFactor = 0f;


    //New Stuff

    private float posX = 0f;
    private float posY = 0f;

    private float scaleFocusX = 0f;
    private float scaleFocusY = 0f;

    private float dX = 0f;
    private float dY = 0f;

    private float prevX = 0f;
    private float prevY = 0f;

    boolean pivotWasSet = false;


    //

    // Where the finger first  touches the screen
//    private float startX = 0f;
//    private float startY = 0f;

    // How much to translate the canvas
//    float dx = 0f;
//    float dy = 0f;
//    private float prevDx = 0f;
//    private float prevDy = 0f;

    float minDx;
    float minDy;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerID = INVALID_POINTER_ID;

    int firstTouchLocation;

    public MyZoomLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MyZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

//    public MyZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//
//        int childSumWidth = 0;
//        int childSumHeight = 0;
//        int childHspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        int childWspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//
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
//
//
//        int desiredWSpec = MeasureSpec.makeMeasureSpec(childSumWidth, MeasureSpec.UNSPECIFIED);
//        //statusbarheight, bracket activity toolbar height and navbar height are to make it so that a part of the bracket doesnt get cut off
//        int desiredHSpec = MeasureSpec.makeMeasureSpec(childSumHeight , MeasureSpec.UNSPECIFIED);
//        setMeasuredDimension(desiredWSpec, desiredHSpec);
//        super.onMeasure(desiredWSpec, desiredHSpec);
//    }


    //TODO make sure this is accessible
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {


        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    //Log.i(TAG, "DOWN");
                    mode = Mode.DRAG;

                    prevX = event.getX();
                    prevY = event.getY();

                    mActivePointerID = event.getPointerId(0);
                    break;
                }


                case MotionEvent.ACTION_MOVE: {
//                    Log.d("ActionMove", String.valueOf(mode));
//                    Log.d("PointerCount", String.valueOf(event.getPointerCount()));
                    if (event.getPointerCount() == 1) {
                        mode = Mode.DRAG;
                    }
                    //find the index of the active pointer and fetch its position
                    final int pointerIndex = event.findPointerIndex(mActivePointerID);


                    //pointerIndex = event.findPointerIndex(mActivePointerID);
//                    final float x = event.getX(pointerIndex);
//                    final float y = event.getY(pointerIndex);

                    if (mode == Mode.DRAG || mode == mode.ZOOM) {
                        dX = event.getX(pointerIndex) - prevX;
                        dY = event.getY(pointerIndex) - prevY;

                        posX += dX;
                        posY += dY;

                        prevX = event.getX(pointerIndex);
                        prevY = event.getY(pointerIndex);
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = Mode.ZOOM;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    //Log.d("PointerCount", String.valueOf(event.getPointerCount()));


                    final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerID) {
                        //Our active pointer is going up Choose another active pointer and adjust
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        prevX = event.getX(newPointerIndex);
                        prevY = event.getY(newPointerIndex);
                        mActivePointerID = event.getPointerId(newPointerIndex);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //Log.i(TAG, "UP");
                    mode = Mode.NONE;
                    mActivePointerID = INVALID_POINTER_ID;
                    break;


            }
            scaleDetector.onTouchEvent(event);


            getParent().requestDisallowInterceptTouchEvent(true);


            if (getParent() != null) {
                minDx = ((child().getWidth() * scale) - ((View) getParent()).getWidth()) * (-1);
                minDy = ((child().getHeight() * scale) - ((View) getParent()).getHeight()) * (-1);
            } else {
                minDx = ((child().getWidth() * scale)) * (-1);
                minDy = ((child().getHeight() * scale)) * (-1);
            }


            posX = Math.min(Math.max(posX, minDx), 0);
            posY = Math.min(Math.max(posY, minDy), 0);
//            Log.i(TAG, "ZoomWidth: " + this.getWidth() + ", ChildWidth: " + child().getWidth() + ", ZoomHeight " + this.getHeight()
//                    + ", ChildHeight: " + child().getHeight());
            applyScaleAndTranslation(child());


            return true;
        });


    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //Log.d("InterceptTouchEvent", "called");
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mode = Mode.NONE;

            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case (MotionEvent.ACTION_DOWN): {
                mode = Mode.DRAG;
                Log.d("InterceptDown", "called");

                prevX = event.getX();
                prevY = event.getY();

                mActivePointerID = event.getPointerId(0);
                firstTouchLocation = (int) (event.getX() + event.getY());
                break;

            }
            //use DX/DY instead of thread sleep
            case (MotionEvent.ACTION_MOVE): {
                Log.d("InterceptMove", "called");

                final int pointerIndex = event.findPointerIndex(mActivePointerID);


                if (mode == Mode.DRAG) {
                    dX = event.getX(pointerIndex) - prevX;
                    dY = event.getY(pointerIndex) - prevY;

                    posX += dX;
                    posY += dY;

                    prevX = event.getX(pointerIndex);
                    prevY = event.getY(pointerIndex);
                }
                int touchDelta = (int) (dX + dY);
                Log.d("TouchDelta", String.valueOf(Math.abs((int) (dX + dY))));
                return Math.abs(touchDelta) > 3;


//                if (mode == Mode.NONE) {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                    }
//
//                    int newTouchLocation = (int) (event.getX() + event.getY());
//                    Log.d("MoveAmount", String.valueOf((int)Math.abs(dX + dY)));
//                    int moveAmount = Math.abs(newTouchLocation - firstTouchLocation);
//                    if (moveAmount > 20) {
//                        Log.d("MovedFarEnough", "true");
//                        if (mode != Mode.ZOOM) {
//                            mode = Mode.DRAG;
//                        }
//                        return true;
//                    } else {
//                        Log.d("StayedStill", "true");
//                        mode = Mode.NONE;
//                        return false;
//                    }
//                }

                //break;
            }


            case (MotionEvent.ACTION_POINTER_DOWN): {
                mode = Mode.ZOOM;
                return true;
            }
            case (MotionEvent.ACTION_POINTER_UP): {

                return true;
            }


        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        //Log.i(TAG, "onScale" + scaleFactor);
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }


        if (scale < MAX_ZOOM && scale > MIN_ZOOM) {
//            prevX*=scaleFactor;
//            prevY*= scaleFactor;
//            dX+= scaleFactor;
//            dY+= scaleFactor;
            final float focalX = detector.getFocusX();
            final float focalY = detector.getFocusY();

            float diffX = focalX - posX;
            float diffY = focalY - posY;

            float diffPrevX = focalX - prevX;
            float diffPrevY = focalY - prevY;

            diffX = diffX * scaleFactor - diffX;
            diffY = diffY * scaleFactor - diffY;

            diffPrevX = diffPrevX * scaleFactor - diffPrevX;
            diffPrevY = diffPrevY * scaleFactor - diffPrevY;

            posX -= diffX;
            posY -= diffY;
            prevX -= diffPrevX;
            prevY -= diffPrevY;


        }

        //Log.i(TAG, " posX: " + posX + " posY: " + posY + " newX: " + newX + " newY: " + newY + " changeX: " + changeX + " changeY: " + changeY );

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        //Log.i(TAG, "onScaleEnd");
    }


    private void applyScaleAndTranslation(View child) {
        if (!pivotWasSet) {
            child.setPivotX(0);
            child.setPivotY(0);
            pivotWasSet = true;
        }
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setTranslationX(posX);
        child.setTranslationY(posY);

        requestLayout();
    }

    public View child() {
        return getChildAt(0);
    }

}
