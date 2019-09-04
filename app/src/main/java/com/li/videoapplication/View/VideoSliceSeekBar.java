package com.li.videoapplication.View;

import com.fmscreenrecord.utils.MResource;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoSliceSeekBar extends ImageView {
    private static final int SELECT_THUMB_LEFT = 1;
    private static final int SELECT_THUMB_RIGHT = 2;
    private static final int SELECT_THUMB_NON = 0;


    //params
    //private Bitmap thumbSlice = BitmapFactory.decodeResource(getResources(), R.drawable.fm_player_current_position);
    private Bitmap thumbSliceLeft;
    private Bitmap thumbSliceRight;

    private Bitmap thumbCurrentVideoPosition;

    private int progressMinDiff = 10; //percentage
    private int progressColor;
    private int txtColor;

    private int secondaryProgressColor;
    private int progressHalfHeight = 4;  //进度条粗细
    private int thumbPadding;
    private int maxValue = 100;

    private int minValue = 10;

    private int progressMinDiffPixels;
    private int thumbSliceLeftX, thumbSliceRightX, thumbCurrentVideoPositionX;
    private int thumbSliceLeftValue, thumbSliceRightValue;
    private int thumbSliceAllValue;
    //private int thumbSliceY;
    private int thumbSliceRightY;
    private int thumbSliceLeftY;
    private int thumbCurrentVideoPositionY;
    private Paint paint = new Paint();

    private Paint paintTxT = new Paint();
    private Paint paintTxT2 = new Paint();

    private Paint paintThumb = new Paint();
    private int selectedThumb;
    private int thumbSliceHalfWidth, thumbCurrentVideoPositionHalfWidth;
    private SeekBarChangeListener scl;

    private int progressTop;
    private int progressBottom;

    private boolean blocked;
    private boolean isVideoStatusDisplay;

    private int limitMovecnt = 0;//极限处往极限方向位移次数

    private int lastTouchEventX = 0;//第一次触屏jf位置

    private Context mContext;

    public VideoSliceSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_left"));
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_right"));
        thumbCurrentVideoPosition = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_leftthumb"));
        thumbPadding = getResources().getDimensionPixelOffset(MResource.getIdByName(mContext,"dimen","fm_video_editor_margin"));

        progressColor = getResources().getColor(MResource.getIdByName(mContext,"color","gray"));
        txtColor = getResources().getColor(MResource.getIdByName(mContext,"color","black_75_transparent"));

        secondaryProgressColor = getResources().getColor(MResource.getIdByName(mContext,"color","green2"));

    }

    public VideoSliceSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext= context;
        thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_left"));
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_right"));
        thumbCurrentVideoPosition = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_leftthumb"));
        thumbPadding = getResources().getDimensionPixelOffset(MResource.getIdByName(mContext,"dimen","fm_video_editor_margin"));

        progressColor = getResources().getColor(MResource.getIdByName(mContext,"color","gray"));
        txtColor = getResources().getColor(MResource.getIdByName(mContext,"color","black_75_transparent"));

        secondaryProgressColor = getResources().getColor(MResource.getIdByName(mContext,"color","green2"));
    }

    public VideoSliceSeekBar(Context context) {
        super(context);
        mContext= context;
        thumbSliceLeft = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_left"));
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_thumb_slice_right"));
        thumbCurrentVideoPosition = BitmapFactory.decodeResource(getResources(), MResource.getIdByName(mContext,"drawable","fm_leftthumb"));
        thumbPadding = getResources().getDimensionPixelOffset(MResource.getIdByName(mContext,"dimen","fm_video_editor_margin"));

        progressColor = getResources().getColor(MResource.getIdByName(mContext,"color","gray"));
        txtColor = getResources().getColor(MResource.getIdByName(mContext,"color","black_75_transparent"));

        secondaryProgressColor = getResources().getColor(MResource.getIdByName(mContext,"color","green2"));
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        init();
    }

    private void init()
    {


        if (thumbSliceLeft.getHeight() > getHeight())
        {
            getLayoutParams().height = thumbSliceLeft.getHeight();
        }

        //thumbSliceY = (getHeight()) - (thumbSliceLeft.getHeight() / 2);
        thumbSliceLeftY = (getHeight()/2 + 4);
        thumbSliceRightY = (getHeight()/2) - (thumbSliceLeft.getHeight()+4);

        thumbCurrentVideoPositionY = (getHeight() / 2) - (thumbCurrentVideoPosition.getHeight() / 2);

        thumbSliceHalfWidth = thumbSliceLeft.getWidth() / 2;
        thumbCurrentVideoPositionHalfWidth = thumbCurrentVideoPosition.getWidth() / 2;
        if (thumbSliceLeftX == 0 || thumbSliceRightX == 0)
        {
            thumbSliceLeftX = thumbPadding;
            thumbSliceRightX = getWidth() - thumbPadding;
        }
        progressMinDiffPixels = calculateCorrds(progressMinDiff) - 2 * thumbPadding;
        progressTop = getHeight() / 2 - progressHalfHeight;
        progressBottom = getHeight() / 2 + progressHalfHeight;
        invalidate();
        thumbSliceAllValue = thumbSliceRightValue;
    }

    public void setSeekBarChangeListener(SeekBarChangeListener scl) {
        this.scl = scl;
    }

    @SuppressLint("DrawAllocation") @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect;
        //generate and draw progress
        paintTxT.setColor(txtColor);
        paintTxT.setTextSize(28);
        paintTxT.setTypeface(Typeface.SANS_SERIF);

        paintTxT2.setColor(secondaryProgressColor);
        paintTxT2.setTextSize(28);
        paintTxT2.setTypeface(Typeface.SANS_SERIF);

        paint.setColor(progressColor);
        rect = new Rect(thumbPadding, progressTop, thumbSliceLeftX, progressBottom);
        canvas.drawRect(rect, paint);
        rect = new Rect(thumbSliceRightX, progressTop, getWidth() - thumbPadding, progressBottom);
        canvas.drawRect(rect, paint);

        //generate and draw secondary progress
        paint.setColor(secondaryProgressColor);
        rect = new Rect(thumbSliceLeftX, progressTop, thumbSliceRightX, progressBottom);
        canvas.drawRect(rect, paint);

        if (!blocked) {
            //generate and draw thumbs pointer
            canvas.drawBitmap(thumbSliceLeft, thumbSliceLeftX - thumbSliceHalfWidth, thumbSliceLeftY, paintThumb);
            canvas.drawBitmap(thumbSliceRight, thumbSliceRightX - thumbSliceHalfWidth, thumbSliceRightY, paintThumb);

            canvas.drawText(getTimeForTrackFormat(thumbSliceLeftValue,true), thumbSliceLeftX + thumbSliceHalfWidth, thumbSliceLeftY+thumbSliceLeft.getHeight(), paintTxT2);
            canvas.drawText(getTimeForTrackFormat(thumbSliceRightValue,true), thumbSliceRightX - thumbSliceHalfWidth-65, thumbSliceRightY+20, paintTxT2);

        }
        if (isVideoStatusDisplay) {
            //generate and draw video thump pointer
            canvas.drawBitmap(thumbCurrentVideoPosition, thumbCurrentVideoPositionX - thumbCurrentVideoPositionHalfWidth,
                    thumbCurrentVideoPositionY, paintThumb);
        }

        canvas.drawText("00:00", thumbPadding/5, thumbSliceLeftY, paintTxT);
        canvas.drawText(getTimeForTrackFormat(maxValue,true), getWidth()-thumbPadding+thumbPadding/5, thumbSliceLeftY, paintTxT);

    }

    public String getTimeForTrackFormat(int timeInMills, boolean display2DigitsInMinsSection) {
        int minutes = (timeInMills / (60 * 1000));
        int seconds = timeInMills/ 1000 % 60;
        String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
        result += minutes + ":";
        if (seconds < 10) {
            result += "0" + seconds;
        } else {
            result += seconds;
        }
        return result;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!blocked) {
            int mx = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(event.getY()>getHeight()/2+5)
                    {
                        selectedThumb = SELECT_THUMB_LEFT;
                    }
                    else if(event.getY()<getHeight()/2-5)
                    {
                        selectedThumb = SELECT_THUMB_RIGHT;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(event.getY()>getHeight()/2+5)
                    {
                        selectedThumb = SELECT_THUMB_LEFT;
                    }
                    else if(event.getY()<getHeight()/2-5)
                    {
                        selectedThumb = SELECT_THUMB_RIGHT;
                    }

                    if ((event.getX() <= thumbSliceLeftX + progressMinDiffPixels && selectedThumb == SELECT_THUMB_RIGHT)
                            ||(event.getX() >= thumbSliceRightX - progressMinDiffPixels && selectedThumb == SELECT_THUMB_LEFT))
                    {

                        selectedThumb = SELECT_THUMB_NON;
                    }

                    if (selectedThumb == SELECT_THUMB_LEFT) {
                        thumbSliceLeftX = mx;
                    } else if (selectedThumb == SELECT_THUMB_RIGHT) {
                        thumbSliceRightX = mx;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    selectedThumb = SELECT_THUMB_NON;
                    break;
            }
            notifySeekBarValueChanged();
        }
        return true;
    }

    private void notifySeekBarValueChanged()
    {
        if (thumbSliceLeftX < thumbPadding)
            thumbSliceLeftX = thumbPadding;

        if (thumbSliceRightX < thumbPadding)
            thumbSliceRightX = thumbPadding;

        if (thumbSliceLeftX > getWidth() - thumbPadding)
            thumbSliceLeftX = getWidth() - thumbPadding;

        if (thumbSliceRightX > getWidth() - thumbPadding)
            thumbSliceRightX = getWidth() - thumbPadding;

        invalidate();
        if (scl != null) {
            calculateThumbValue();
            scl.SeekBarValueChanged(thumbSliceLeftValue, thumbSliceRightValue);
        }
    }

    private void calculateThumbValue()
    {
        thumbSliceLeftValue = (maxValue * (thumbSliceLeftX - thumbPadding)) / (getWidth() - 2 * thumbPadding);
        thumbSliceRightValue = (maxValue * (thumbSliceRightX - thumbPadding)) / (getWidth() - 2 * thumbPadding);
    }

    private int calculateCorrds(int progress)
    {
        return (int) (((getWidth() - 2d * thumbPadding) / maxValue) * progress);
    }

    private int calculateCorrdsRight(int progress)
    {
        return (int) (((getWidth() - 2d * thumbPadding) / maxValue) * progress) + thumbPadding;
    }

    public void setLeftProgress(int progress)
    {
        if (progress < thumbSliceRightValue - progressMinDiff) {
            thumbSliceLeftX = calculateCorrds(progress);
        }
        notifySeekBarValueChanged();
    }

    public void setRightProgress(int progress) {
        if (progress > thumbSliceLeftValue + progressMinDiff) {
            thumbSliceRightX = calculateCorrdsRight(progress);
        }
        notifySeekBarValueChanged();
    }

    public int getLeftProgress()
    {
        return thumbSliceLeftValue;
    }

    public int getRightProgress() {
        return thumbSliceRightValue;
    }

    public void setProgress(int leftProgress, int rightProgress) {
        if (rightProgress - leftProgress > progressMinDiff) {
            thumbSliceLeftX = calculateCorrds(leftProgress);
            thumbSliceRightX = calculateCorrds(rightProgress);
        }
        notifySeekBarValueChanged();
    }

    public void videoPlayingProgress(int progress) {
        isVideoStatusDisplay = true;
        thumbCurrentVideoPositionX = calculateCorrdsRight(progress);
        invalidate();
    }

    public void removeVideoStatusThumb() {
        isVideoStatusDisplay = false;
        invalidate();
    }

    public void setSliceBlocked(boolean isBLock) {
        blocked = isBLock;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setProgressMinDiff(int progressMinDiff) {
        this.progressMinDiff = progressMinDiff;
        progressMinDiffPixels = calculateCorrds(progressMinDiff);
    }

    public void setProgressHeight(int progressHeight) {
        this.progressHalfHeight = progressHalfHeight / 2;
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public void setSecondaryProgressColor(int secondaryProgressColor) {
        this.secondaryProgressColor = secondaryProgressColor;
        invalidate();
    }

    public void setThumbSliceLeft(Bitmap thumbSlice) {
        this.thumbSliceLeft = thumbSlice;
        init();
    }
    public void setThumbSliceRight(Bitmap thumbSlice) {
        this.thumbSliceRight = thumbSlice;
        init();
    }

    public void setThumbCurrentVideoPosition(Bitmap thumbCurrentVideoPosition) {
        this.thumbCurrentVideoPosition = thumbCurrentVideoPosition;
        init();
    }

    public void setThumbPadding(int thumbPadding) {
        this.thumbPadding = thumbPadding;
        invalidate();
    }

    public interface SeekBarChangeListener {
        void SeekBarValueChanged(int leftThumb, int rightThumb);
    }
}