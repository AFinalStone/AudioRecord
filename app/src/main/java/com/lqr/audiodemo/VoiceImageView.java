package com.lqr.audiodemo;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author syl
 * @time 2018/12/28 3:08 PM
 */

public class VoiceImageView extends android.support.v7.widget.AppCompatImageView {

    private Context mContext;

    public VoiceImageView(Context context) {
        super(context);
    }

    public VoiceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 开始播放
     */
    public void startPlay() {
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        if (animationDrawable != null)
            animationDrawable.start();
    }

    /**
     * 结束播放
     */
    public void stopPlay() {
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopPlay();
        super.onDetachedFromWindow();
    }
}
