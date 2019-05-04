package ferox.bracket;

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

    final static float MIN_ZOOM = 0.5f;
    final static float MAX_ZOOM = 2.0f;

    private Mode mode = Mode.NONE;
    float scale = 1.0f;
    private float lastScaleFactor = 0f;

    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    float dx = 0f;
    float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    float minDx;
    float minDy;


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

    public MyZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }

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
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "DOWN");
                    mode = Mode.DRAG;
                    startX = event.getX() - prevDx;
                    startY = event.getY() - prevDy;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == mode.DRAG) {
                        dx = event.getX() - startX;
                        dy = event.getY() - startY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = mode.ZOOM;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = Mode.DRAG;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "UP");
                    mode = Mode.NONE;
                    prevDx = dx;
                    prevDy = dy;
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


            dx = Math.min(Math.max(dx, minDx), 0);
            dy = Math.min(Math.max(dy, minDy), 0);
//            Log.i(TAG, "ZoomWidth: " + this.getWidth() + ", ChildWidth: " + child().getWidth() + ", ZoomHeight " + this.getHeight()
//                    + ", ChildHeight: " + child().getHeight());
            applyScaleAndTranslation(child());


            return true;
        });

    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        Log.i(TAG, "onScale" + scaleFactor);
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.i(TAG, "onScaleEnd");
    }


    private void applyScaleAndTranslation(View child) {
        child.setPivotX(0);
        child.setPivotY(0);
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setTranslationX(dx);
        child.setTranslationY(dy);

        requestLayout();
    }

    public View child() {
        return getChildAt(0);
    }

}
