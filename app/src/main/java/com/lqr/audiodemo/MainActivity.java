package com.lqr.audiodemo;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolder;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.adapter.OnItemClickListener;
import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.AudioRecordManager;
import com.lqr.audio.IAudioPlayListener;
import com.lqr.audio.IAudioRecordListener;
import com.lqr.recyclerview.LQRRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionGen;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_VOICE_TIME = 20;
    private static final String AUDIO_DIR_NAME = "community_audio";

    @BindView(R.id.root)
    LinearLayout mRoot;
    @BindView(R.id.rvMsg)
    LQRRecyclerView mRvMsg;
    @BindView(R.id.btnVoice)
    RecordAudioButton mBtnVoice;

    private Context mContext;
    private File mAudioDir;
    private LQRAdapterForRecyclerView<File> mAdapter;
    private RecordVoicePopWindow mRecordVoicePopWindow;
    private List<File> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        request();
        initVoice();
        initData();
    }

    private void request() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.WAKE_LOCK
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    private void initVoice() {
        mAudioDir = new File(Environment.getExternalStorageDirectory(), AUDIO_DIR_NAME);
        if (!mAudioDir.exists()) {
            mAudioDir.mkdirs();
        }
        AudioRecordManager.getInstance(mContext).setAudioSavePath(mAudioDir.getAbsolutePath());
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(MAX_VOICE_TIME);
        mBtnVoice.setOnVoiceButtonCallBack(new RecordAudioButton.OnVoiceButtonCallBack() {
            @Override
            public void onStartRecord() {
                AudioRecordManager.getInstance(mContext).startRecord();
            }

            @Override
            public void onStopRecord() {
                AudioRecordManager.getInstance(mContext).stopRecord();
            }

            @Override
            public void onWillCancelRecord() {
                AudioRecordManager.getInstance(mContext).willCancelRecord();
            }

            @Override
            public void onContinueRecord() {
                AudioRecordManager.getInstance(mContext).continueRecord();
            }
        });
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {
            @Override
            public void initTipView() {
                if (mRecordVoicePopWindow == null) {
                    mRecordVoicePopWindow = new RecordVoicePopWindow(mContext);
                }
                mRecordVoicePopWindow.showAsDropDown(mRoot);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showTimeOutTipView(counter);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showRecordingTipView();
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showRecordTooShortTipView();
                }
            }

            @Override
            public void setCancelTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showCancelTipView();
                }
            }

            @Override
            public void destroyTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.dismiss();
                }
            }

            @Override
            public void onStartRecord() {

            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                File file = new File(audioPath.getPath());
                if (file.exists()) {
                    Toast.makeText(getApplicationContext(), "录制成功", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.updateCurrentVolume(db);
                }
            }
        });
    }

    private void initData() {
        loadData();
        setAdapter();
    }


    private void loadData() {
        if (mAudioDir.exists()) {
            mData.clear();
            File[] files = mAudioDir.listFiles();
            for (File file : files) {
                if (file.getAbsolutePath().endsWith("voice")) {
                    mData.add(file);
                }
            }
            setAdapter();
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<File>(this, mData, R.layout.community_adapter_chat_list_right_voice) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, File item, int position) {
                    //这里就不考虑语音长度了，实际开发中用到的Sdk有提供保存语音信息的bean

                }
            };
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LQRViewHolder helper, ViewGroup parent, View itemView, int position) {
                    AudioPlayManager.getInstance().stopPlay();
                    File item = mData.get(position);
                    final VoiceImageView ivAudio = helper.getView(R.id.iv_voice);
                    Uri audioUri = Uri.fromFile(item);
                    Log.e("LQR", audioUri.toString());
                    AudioPlayManager.getInstance().startPlay(mContext, audioUri, new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri var1) {
                            ivAudio.startPlay();
                        }

                        @Override
                        public void onStop(Uri var1) {
                            ivAudio.stopPlay();
                        }

                        @Override
                        public void onComplete(Uri var1) {
                            ivAudio.stopPlay();
                        }
                    });
                }
            });
            mRvMsg.setAdapter(mAdapter);
        } else

        {
            mAdapter.notifyDataSetChangedWrapper();
        }
    }
}
