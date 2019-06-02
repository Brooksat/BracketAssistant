package ferox.bracket.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class BracketView extends LinearLayout {

    Context mContext;

//    public BracketView(Context context) {
//        //not called?
//        super(context);
//        setWillNotDraw(false);
//    }


    public BracketView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;
        setWillNotDraw(false);

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
        int desiredHSpec = MeasureSpec.makeMeasureSpec(childSumHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(desiredWSpec, desiredHSpec);
        super.onMeasure(desiredWSpec, desiredHSpec);
    }
}