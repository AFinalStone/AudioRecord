package com.af.audiodemo;

import java.io.File;
import java.util.List;

public interface MainContract {

    interface Presenter {
        /**
         * 初始化数据
         */
        void init();

        /**
         * 开始录制
         */
        void startRecord();

        /**
         * 停止录制
         */
        void stopRecord();

        /**
         * 即将取消录制
         */
        void willCancelRecord();

        /**
         * 继续录制
         */
        void continueRecord();

        /**
         * 开始播放音频
         */
        void startPlayRecord(int position);
    }

    interface View {
        /**
         * 显示列表
         */
        void showList(List<File> list);

        /**
         * 显示提示控件
         */
        void showNormalTipView();

        /**
         * 即将超时
         *
         * @param remainder
         */
        void showTimeOutTipView(int remainder);

        /**
         * 正常录制
         */
        void showRecordingTipView();

        /**
         * 录制时间太短
         */
        void showRecordTooShortTipView();

        /**
         * 松开手指，取消发送
         */
        void showCancelTipView();

        /**
         * 隐藏提示view
         */
        void hideTipView();

        /**
         * 调整当前音量
         *
         * @param db
         */
        void updateCurrentVolume(int db);

        /**
         * 开始播放动画
         *
         * @param position
         */
        void startPlayAnim(int position);

        /**
         * 停止播放动画
         */
        void stopPlayAnim();
    }


}
