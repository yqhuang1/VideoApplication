package com.li.videoapplication.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2015/9/8 0008.
 */
public class MyScrollView extends ScrollView {
    private View contentView;  //ScrollView包含的子组件

    private OnBorderListener onBorderListener;
    private OnTouchListener onBorderTouchListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* 在滑动发生时监听滑动靠岸 */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        doOnBorderListener();
    }

    /**
     * 关键函数，判断什么时候滑动靠岸
     * 因为ScrollView只包含一个子组件，因此子组件的原始测量高度就是ScrollView的高度，
     * 当纵向滑动值（滑动出屏幕的高度）加上屏幕上显示的高度大于或等于ScrollView的高度时，就说明到底了，
     * 当纵向滑动值为0的时候，是从顶部开始显示的，因此说明处于顶部位置
     */
    private void doOnBorderListener() {
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        } else { //没靠岸的时候调用onMiddle方法
            if (onBorderListener != null) {
                onBorderListener.onMiddle();
            }
        }
    }

    public void setOnBorderListener(final OnBorderListener onBorderListener) {
        if (onBorderListener == null) {
            this.onBorderTouchListener = null;
            return;
        } else {
            this.onBorderListener = onBorderListener;
        }

        if (contentView == null) {
            contentView = getChildAt(0);  //因为ScrollView只能包含一个子组件，因此可以直接通过索引‘0’获取子组件
        }
        /* 其实这是另一种处理监听滑动靠岸的方法，是在touch事件发生时进行滑动靠岸监听 */
        this.onBorderTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //这里面试了几个动作，最终正明MOVE动作的效果是最好，最及时的
                    case MotionEvent.ACTION_MOVE:
                        doOnBorderListener();
                        break;
                }
                return false;
            }

        };
        super.setOnTouchListener(onBorderTouchListener);
    }

    /**
     * 重写setOnTouchListener方法，防止在MyScrollView实例中调用该方法的时候，覆盖掉我们上面做的处理
     */
    @Override
    public void setOnTouchListener(final OnTouchListener l) {
        OnTouchListener onTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (onBorderTouchListener != null) {
                    onBorderTouchListener.onTouch(v, event);
                }
                return l.onTouch(v, event);
            }

        };
        super.setOnTouchListener(onTouchListener);
    }

    /**
     * OnborderListener, called when scroll to top or bottom
     */
    public static interface OnBorderListener {
        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();

        /**
         * Called when scroll in middle
         */
        public void onMiddle();
    }
}
