package com.af.audiodemo.view;

import com.af.audiodemo.R;
import com.af.audiodemo.view.widget.VoiceImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;

/**
 * 适配器
 */
public class VideoAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    private int mCurrentPlayAnimPosition = -1;//当前播放动画的位置

    public VideoAdapter() {
        super(R.layout.community_adapter_chat_list_right_voice);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        //这里就不考虑语音长度了，实际开发中用到的Sdk有提供保存语音信息的bean
        VoiceImageView ivAudio = helper.getView(R.id.iv_voice);
        if (mCurrentPlayAnimPosition == helper.getLayoutPosition()) {
            ivAudio.startPlay();
        } else {
            ivAudio.stopPlay();
        }
        helper.addOnClickListener(R.id.iv_voice);
    }

    /**
     * 开始播放动画
     *
     * @param position
     */
    public void startPlayAnim(int position) {
        mCurrentPlayAnimPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 停止播放动画
     */
    public void stopPlayAnim() {
        mCurrentPlayAnimPosition = -1;
        notifyDataSetChanged();
    }
}
