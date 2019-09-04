package com.fmscreenrecord.animation;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fmscreenrecord.activity.MainActivity;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.floatview.FloatViewManager;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;


/**
 * 浮窗动画相关类
 *
 * @author WYX
 */
public class FloatContentAnimation implements OnClickListener {
    private boolean isOpen = false;// 是否菜单打开状态

    private ImageView[] btns;
    /**
     * 半径
     */
    private int radius = 180;
    /**
     * 每个按钮之间的夹角
     */
    private float angle;
    private Context mContext;
    private int leftMargin = 0, bottomMargin = 0;
    int bottomMargins;
    int maxTimeSpent = 200;// 最长动画耗时
    int minTimeSpent = 80;// 最短动画耗时
    int intervalTimeSpent;// 每相邻2个的时间间隔
    private RelativeLayout.LayoutParams params1;
    ImageView btn_menu;
    private int buttonWidth = 58;// 图片宽高
    public FloatViewManager manager;
    // 浮窗展开时的弹性距离
    private int floatElastic = 30;
    // 动画是否结束
    private boolean isAnminPlaying = false;
    // 浮窗菜单宽度
    int btn_width;
    View view;
    // 完成动画的控件数
    private int btnsleng = 0;
    /**
     * 浮窗所在位置 0 在上方 1在下方
     */
    private int location = -1;
    /**
     * 设置旋转动画
     */
    final RotateAnimation RoaAnimation = new RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    public FloatContentAnimation(Context context, View view,
                                 FloatViewManager manager) {

        this.mContext = context;

        this.manager = manager;
        this.view = view;
        initButtons(view);

        // 将手机的分辨率宽度除以4.6作为半径

        // 获取btn_menu的宽高
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        btn_menu.measure(w, h);
        btn_width = btn_menu.getMeasuredWidth();
        // 设置buttonWidth的大小
        buttonWidth = (int) (btn_width / 1.7);
        bottomMargins = view.getMeasuredHeight() - buttonWidth - bottomMargin;

        /***
         * 旋转动画参数设置
         */
        RoaAnimation.setDuration(1);// 设置动画持续时间
        /** 常用方法 */
        RoaAnimation.setRepeatCount(0);// 设置重复次数
        RoaAnimation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        RoaAnimation.setStartOffset(10);// 执行前的等待时间

    }

    /**
     * 按钮菜单旋转动画监听
     */
    public void RoaAnimationEnd() {
        RoaAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RoaAnimation.cancel();

                // 按钮距离原点btn_menu的长度;
                float YLengt;
                // 按钮图片的高度
                float YHeight;

                float[] mfloat;

                float xLenth;
                float yLenth;
                // 浮窗距离屏幕上方边缘的距离
                float UpperDistance;
                // 浮窗距离屏幕下方边缘的距离
                float BelowDistance;
                if (isOpen == false) {// 如果处于展开状态
                    for (int i = 0; i < btns.length; i++) {
                        xLenth = 0;
                        yLenth = 0;

                        UpperDistance = 0;

                        BelowDistance = 0;

                        YLengt = (float) (radius * 0.7);
                        YHeight = 120;

                        // 判断屏幕方向
                        if (MainActivity.getConfiguration()) {
                            UpperDistance = ExApplication.moveY - YLengt;

                            BelowDistance = ExApplication.DEVW
                                    - ExApplication.moveY;
                        } else {
                            UpperDistance = ExApplication.moveY - YLengt;

                            BelowDistance = ExApplication.DEVH
                                    - ExApplication.moveY;
                        }

                        // 判断浮窗是否处于屏幕边角
                        if (((UpperDistance) < (YLengt + YHeight))) {
                            // 在屏幕上方边缘
                            location = 0;

                            xLenth = (float) (radius * Math.sin(i * angle));
                            yLenth = (float) (radius * Math.cos(i * angle));
                            mfloat = MinUtil.getlayoutXY2(xLenth, yLenth);
                        } else if (((BelowDistance) < (YLengt + YHeight))) {
                            // 处于屏幕下方边缘
                            location = 1;

                            xLenth = (float) (radius * Math.sin(i * angle)) + 10;
                            yLenth = (float) (radius * Math.cos(i * angle)) + 50;
                            mfloat = MinUtil.getlayoutXY2(xLenth, yLenth);
                        } else {
                            // 不处于屏幕上方或下方边缘
                            location = -1;

                            if (i == 0) {// 修订三个按钮的X,Y距离
                                xLenth = (float) (radius * 0.75);
                                yLenth = (float) (radius * 0.7);

                            } else if (i == 1) {
                                xLenth = (float) (radius * 1.3);
                                yLenth = (float) (radius * 0.0);

                            } else if (i == 2) {
                                xLenth = (float) (radius * 0.75);
                                yLenth = (float) (radius * -0.7);

                            }
                            // 根据按钮所在的屏幕象限修正xLenth，yLenth坐标(以ExApplication.moveX，Y为原点）
                            mfloat = MinUtil.getlayoutXY(xLenth, yLenth);
                        }

                        btns[i].startAnimation(animTranslate3(-mfloat[0],
                                -mfloat[1], leftMargin, bottomMargins, btns[i],
                                maxTimeSpent - i * intervalTimeSpent));

                        btns[i].setVisibility(View.GONE);

                        // 更新图标
                        btn_menu.setBackgroundResource(MResource.getIdByName(
                                mContext, "drawable", "fm_float_ico"));
                    }
                }
            }
        });
    }

    /**
     * 浮窗展开动画分两步，animTranslate负责将浮窗运动至 指定坐标 加/减floatElastic距离 的坐标
     * animTranslate2负责从指定坐标 加/减 floatElastic 的位置运动至指定坐标. 两步动画完成后，浮窗展开具有回弹效果
     *
     * @param toX            指定X坐标
     * @param toY            指定y坐标
     * @param lastX
     * @param lastY
     * @param button         执行动画的菜单按钮
     * @param durationMillis 动画执行时间
     * @return
     */
    private Animation animTranslate(final float toX, final float toY,
                                    final int lastX, final int lastY, final ImageView button,
                                    final long durationMillis) {

        Animation animation = null;
        if (ExApplication.FloatViewLeft) {// 如果处于屏幕左方
            if (location == -1) {// 处于屏幕中部
                // TODO
                animation = new TranslateAnimation(0, toX + floatElastic, 0,
                        toY + toY / 4);

            } else if (location == 1) {// 处于屏幕底部

                animation = new TranslateAnimation(0, toX + floatElastic, 0,
                        toY - floatElastic);
            } else if (location == 0) {// 处于屏幕顶部

                animation = new TranslateAnimation(0, toX + floatElastic, 0,
                        toY + floatElastic);
            }

        } else {// 处于屏幕右方

            if (location == -1) {// 处于屏幕中部

                animation = new TranslateAnimation(0, toX - floatElastic, 0,
                        toY + toY / 4);
            } else if (location == 1) {// 处于屏幕底部

                animation = new TranslateAnimation(0, toX - floatElastic, 0,
                        toY - floatElastic);
            } else if (location == 0) {// 处于屏幕顶部

                animation = new TranslateAnimation(0, toX - floatElastic, 0,
                        toY + floatElastic);
            }
        }

        // final int[] location = new int[2];
        // btn_menu.getLocationOnScreen(location);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {

                params1 = new RelativeLayout.LayoutParams(0, 0);
                params1.height = buttonWidth;
                params1.width = buttonWidth;
                // 对按钮按照新的坐标排列
                params1.setMargins((int) (btn_menu.getX()) + lastX,
                        (int) (btn_menu.getY()) - lastY - button.getHeight(),
                        0, 0);

                button.startAnimation(animTranslate2(toX, toY, lastX, lastY,
                        button, durationMillis));

            }
        });
        // 动画执行时间
        animation.setDuration(durationMillis);
        return animation;
    }

    private Animation animTranslate2(final float toX, final float toY,
                                     final int lastX, final int lastY, final ImageView button,
                                     final long durationMillis) {

        Animation animation = null;
        if (ExApplication.FloatViewLeft) {// 处于屏幕左方

            if (location == -1) {// 处于屏幕中部
                // TODO
                animation = new TranslateAnimation(toX + floatElastic, toX, toY
                        + toY / 4, toY);
            } else if (location == 1) {// 处于屏幕底部

                animation = new TranslateAnimation(toX + floatElastic, toX, toY
                        - floatElastic, toY);
            } else if (location == 0) {// 处于屏幕顶部
                animation = new TranslateAnimation(toX + floatElastic, toX, toY
                        + floatElastic, toY);
            }
        } else {// 处于屏幕右方
            if (location == -1) {// 处于屏幕中部

                animation = new TranslateAnimation(toX - floatElastic, toX, toY
                        + toY / 4, toY);
            } else if (location == 1) {// 处于屏幕顶部

                animation = new TranslateAnimation(toX - floatElastic, toX, toY
                        - floatElastic, toY);
            } else if (location == 0) {// 处于屏幕底部

                animation = new TranslateAnimation(toX - floatElastic, toX, toY
                        + floatElastic, toY);
            }

        }

        // final int[] location = new int[2];
        // btn_menu.getLocationOnScreen(location);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            // 动画结束后将控件按照新的坐标排列
            public void onAnimationEnd(Animation animation) {

                params1 = new RelativeLayout.LayoutParams(0, 0);
                params1.height = buttonWidth;
                params1.width = buttonWidth;
                // 对按钮按照新的坐标排列
                params1.setMargins((int) (btn_menu.getX()) + lastX,
                        (int) (btn_menu.getY()) - lastY - button.getHeight(),
                        0, 0);
                button.startAnimation(animTranslate(toX, toY, lastX, lastY,
                        button, durationMillis));
                button.setLayoutParams(params1);
                button.clearAnimation();

                btnsleng++;

                // //旋转动画
                if (isOpen == true) {
                    RoaAnimationEnd();
                    RoaAnimation.setDuration(1);// 设置动画持续时间
                    /** 常用方法 */
                    RoaAnimation.setRepeatCount(0);// 设置重复次数
                    button.setAnimation(RoaAnimation);
                    /** 开始动画 */
                    RoaAnimation.startNow();

                }

                // 所有的图片控件执行动画完毕后才能接受新一轮的动画。
                if (btnsleng == 3) {

                    // 将isAnminEnd置为false，表示动画播放结束
                    isAnminPlaying = false;
                    btnsleng = 0;
                    // 如果处于回收状态，回收后直接调回floatview1
                    if (isOpen == false) {

                        manager.back();

                    }
                }

            }
        });
        // 动画执行时间
        animation.setDuration(durationMillis);
        return animation;
    }

    private Animation animTranslate3(final float toX, final float toY,
                                     final int lastX, final int lastY, final ImageView button,
                                     final long durationMillis) {

        Animation animation = null;

        animation = new TranslateAnimation(0, toX, 0, toY);

        // final int[] location = new int[2];
        // btn_menu.getLocationOnScreen(location);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            // 动画结束后将控件按照新的坐标排列
            public void onAnimationEnd(Animation animation) {

                params1 = new RelativeLayout.LayoutParams(0, 0);
                params1.height = buttonWidth;
                params1.width = buttonWidth;
                // 对按钮按照新的坐标排列
                params1.setMargins((int) (btn_menu.getX()) + lastX,
                        (int) (btn_menu.getY()) - lastY - button.getHeight(),
                        0, 0);
                button.startAnimation(animTranslate(toX, toY, lastX, lastY,
                        button, durationMillis));
                button.setLayoutParams(params1);
                button.clearAnimation();

                btnsleng++;

                // //旋转动画
                if (isOpen == true) {
                    RoaAnimationEnd();
                    RoaAnimation.setDuration(1);// 设置动画持续时间
                    /** 常用方法 */
                    RoaAnimation.setRepeatCount(0);// 设置重复次数
                    button.setAnimation(RoaAnimation);
                    /** 开始动画 */
                    RoaAnimation.startNow();

                }

                // 所有的图片控件执行动画完毕后才能接受新一轮的动画。
                if (btnsleng == 3) {

                    // 将isAnminEnd置为false，表示动画播放结束
                    isAnminPlaying = false;
                    btnsleng = 0;
                    // 如果处于回收状态，回收后直接调回floatview1
                    if (isOpen == false) {

                        manager.back();

                    }
                }

            }
        });
        // 动画执行时间
        animation.setDuration(durationMillis);
        return animation;
    }

    public void setFloatContentViewLocation(Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int mwidth = wm.getDefaultDisplay().getWidth();
        int mheight = wm.getDefaultDisplay().getHeight();
        manager.move(view, mwidth, mheight / 2);
        if (ExApplication.moveX == -1 && ExApplication.moveY == -1) {// 首次打开浮窗，使用默认位置的的坐标
            btn_menu.setX(mwidth - radius + btn_width / 2);
            btn_menu.setY(mheight / 2);
            // 将默认位置的坐标赋值给ExApplication.moveX/Y
            ExApplication.moveX = mwidth - radius + btn_width / 2;
            ExApplication.moveY = mheight / 2;

        } else {
            // 根据fm_float_ico的位置设定浮窗位置
            btn_menu.setX(ExApplication.moveX);
            btn_menu.setY(ExApplication.moveY);

        }
    }

    /**
     * * 浮窗展开/回收动画 **
     */
    public void extandAnmin() {

        // 开始执行悬浮框展开动画
        if (!isOpen && isAnminPlaying == false) {
            isOpen = true;
            // 将isAnminEnd设为false，表示动画如未播放结束，按钮点击无效
            isAnminPlaying = true;
            // 按钮距离原点btn_menu的长度;
            float YLengt;
            // 按钮图片的高度
            float YHeight;
            float[] mfloat;

            // 三个子菜单按钮距离父按钮的X,Y距离
            float xLenth;
            float yLenth;
            // 浮窗距离屏幕上方边缘的距离
            float UpperDistance;
            // 浮窗距离屏幕下方边缘的距离
            float BelowDistance;
            for (int i = 0; i < btns.length; i++) {

                xLenth = 0;
                yLenth = 0;

                YLengt = (float) (radius * 0.7);
                YHeight = 120;

                UpperDistance = 0;

                BelowDistance = 0;

                // 判断屏幕方向
                if (MainActivity.getConfiguration()) {
                    UpperDistance = ExApplication.moveY - YLengt;

                    BelowDistance = ExApplication.DEVW - ExApplication.moveY;

                } else {
                    UpperDistance = ExApplication.moveY - YLengt;

                    BelowDistance = ExApplication.DEVH - ExApplication.moveY;

                }

                // 判断浮窗是否处于屏幕边角
                if (((UpperDistance) < (YLengt + YHeight))) {
                    // 在屏幕上方边缘
                    location = 0;

                    xLenth = (float) (radius * Math.sin(i * angle));
                    yLenth = (float) (radius * Math.cos(i * angle));
                    mfloat = MinUtil.getlayoutXY2(xLenth, yLenth);

                } else if (((BelowDistance) < (YLengt + YHeight))) {
                    // 处于屏幕下方边缘
                    location = 1;

                    xLenth = (float) (radius * Math.sin(i * angle)) + 10;
                    yLenth = (float) (radius * Math.cos(i * angle)) + 50;
                    mfloat = MinUtil.getlayoutXY2(xLenth, yLenth);
                } else {
                    // 不处于屏幕上方或下方边缘
                    location = -1;

                    if (i == 0) {// 修订三个按钮的X,Y距离
                        xLenth = (float) (radius * 0.75);
                        yLenth = (float) (radius * 0.7);

                    } else if (i == 1) {
                        xLenth = (float) (radius * 1.3);
                        yLenth = (float) (radius * 0.0);

                    } else if (i == 2) {
                        xLenth = (float) (radius * 0.75);
                        yLenth = (float) (radius * -0.7);

                    }
                    // 根据按钮所在的屏幕象限修正xLenth，yLenth坐标(以ExApplication.moveX，Y为原点）
                    mfloat = MinUtil.getlayoutXY(xLenth, yLenth);
                }

                // 修改btns[i]的坐标原点
                MinUtil.setLayout(btns[i], (int) ExApplication.moveX,
                        (int) ExApplication.moveY);

                btns[i].startAnimation(animTranslate(mfloat[0], mfloat[1],
                        leftMargin + (int) mfloat[0], bottomMargins
                                - (int) mfloat[1], btns[i], minTimeSpent + i
                                * intervalTimeSpent));
                btns[i].setVisibility(View.VISIBLE);

                // 更新图标
                btn_menu.setBackgroundResource(MResource.getIdByName(mContext,
                        "drawable", "fm_float_ico_expand"));

            }

        } else if (isAnminPlaying == false) {// 开始执行悬浮框展开动画
            isOpen = false;
            isAnminPlaying = true;
            // btn_menu.startAnimation(animRotate(90.0f, 0.5f, 0.45f));
            RoaAnimation.setDuration(50);// 设置动画持续时间
            /** 常用方法 */
            RoaAnimation.setRepeatCount(2);// 设置重复次数
            RoaAnimationEnd();
            /** 开始动画 */
            RoaAnimation.startNow();

        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void initButtons(View view) {
        // 3个按钮，具体视情况而定
        btns = new ImageView[3];

        btns[0] = (ImageView) view.findViewById(MResource.getIdByName(mContext,
                "id", "bt_backHome"));
        btns[1] = (ImageView) view.findViewById(MResource.getIdByName(mContext,
                "id", "float_layout_botton_SC"));

        btns[2] = (ImageView) view.findViewById(MResource.getIdByName(mContext,
                "id", "bt_starts"));
        btn_menu = (ImageView) view.findViewById(MResource.getIdByName(
                mContext, "id", "bt_float_ico"));
        btn_menu.setVisibility(View.VISIBLE);
        leftMargin = ((RelativeLayout.LayoutParams) (btn_menu.getLayoutParams())).leftMargin;
        bottomMargin = ((RelativeLayout.LayoutParams) (btn_menu
                .getLayoutParams())).bottomMargin;
        btn_menu.setOnClickListener(this);
        for (int i = 0; i < btns.length; i++) {
            btns[i].setLayoutParams(btn_menu.getLayoutParams());// 初始化的时候按钮都重合
            btns[i].setTag(String.valueOf(i));
        }

        intervalTimeSpent = (maxTimeSpent - minTimeSpent) / btns.length;// 20
        angle = (float) ((float) Math.PI / (2 * (btns.length - 1.18)));
        // 保留小数点后三位
        angle = (float) (Math.floor(angle * 1000) / 1000);

    }

    @Override
    public void onClick(View v) {
        if (v == btn_menu) {
            extandAnmin();
        }

    }

}
