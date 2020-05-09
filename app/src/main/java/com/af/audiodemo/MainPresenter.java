package com.af.audiodemo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.af.audio.AudioPlayManager;
import com.af.audio.AudioRecordManager;
import com.af.audio.IAudioPlayListener;
import com.af.audio.IAudioRecordListener;
import com.af.audiodemo.MainContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页P层
 */
public class MainPresenter<T extends MainContract.View> implements MainContract.Presenter {

    private static final int MAX_VOICE_TIME = 20;//声音最大时间
    private static final String AUDIO_DIR_NAME = "audio_cache";

    private T mView;
    private Context mContext;
    private File mAudioDir;
    private List<File> mListData = new ArrayList<>();

    public MainPresenter(T view, Context context) {
        this.mView = view;
        this.mContext = context;
        mAudioDir = new File(mContext.getExternalCacheDir(), AUDIO_DIR_NAME);
        if (!mAudioDir.exists()) {//判断文件夹是否存在，不存在则创建
            mAudioDir.mkdirs();
        }
    }

    @Override
    public void init() {
        initAudioManager();
        loadAudioCacheData();
    }

    @Override
    public void startRecord() {
        AudioRecordManager.getInstance(mContext).startRecord();
    }

    @Override
    public void stopRecord() {
        AudioRecordManager.getInstance(mContext).stopRecord();
    }

    @Override
    public void willCancelRecord() {
        AudioRecordManager.getInstance(mContext).willCancelRecord();
    }

    @Override
    public void continueRecord() {
        AudioRecordManager.getInstance(mContext).continueRecord();
    }

    @Override
    public void startPlayRecord(final int position) {
        File item = mListData.get(position);
        Uri audioUri = Uri.fromFile(item);
        Log.d("P_startPlayRecord", audioUri.toString());
        AudioPlayManager.getInstance().startPlay(mContext, audioUri, new IAudioPlayListener() {
            @Override
            public void onStart(Uri var1) {
                mView.startPlayAnim(position);
            }

            @Override
            public void onStop(Uri var1) {
                mView.stopPlayAnim();
            }

            @Override
            public void onComplete(Uri var1) {
                mView.stopPlayAnim();
            }
        });
    }

    /**
     * 初始化音频播放管理对象
     */
    private void initAudioManager() {
        AudioRecordManager.getInstance(mContext).setAudioSavePath(mAudioDir.getAbsolutePath());
        AudioRecordManager.getInstance(mContext).setMaxVoiceDuration(MAX_VOICE_TIME);
        AudioRecordManager.getInstance(mContext).setAudioRecordListener(new IAudioRecordListener() {
            @Override
            public void initTipView() {
                mView.showNormalTipView();
            }

            @Override
            public void setTimeoutTipView(int counter) {
                mView.showTimeOutTipView(counter);
            }

            @Override
            public void setRecordingTipView() {
                mView.showRecordingTipView();
            }

            @Override
            public void setAudioShortTipView() {
                mView.showRecordTooShortTipView();
            }

            @Override
            public void setCancelTipView() {
                mView.showCancelTipView();
            }

            @Override
            public void destroyTipView() {
                mView.hideTipView();
            }

            @Override
            public void onStartRecord() {

            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                File file = new File(audioPath.getPath());
                if (file.exists()) {
                    Toast.makeText(mContext.getApplicationContext(), "录制成功", Toast.LENGTH_SHORT).show();
                    loadAudioCacheData();
                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                mView.updateCurrentVolume(db);
            }
        });
    }

    private void loadAudioCacheData() {
        if (mAudioDir.exists()) {
            mListData.clear();
            File[] files = mAudioDir.listFiles();
            for (File file : files) {
                if (file.getAbsolutePath().endsWith("voice")) {
                    mListData.add(file);
                }
            }
            mView.showList(mListData);
        }
    }
}
