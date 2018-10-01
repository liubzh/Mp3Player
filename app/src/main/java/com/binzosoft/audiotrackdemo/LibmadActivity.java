package com.binzosoft.audiotrackdemo;

/*
 * author:conowen
 * date:2012.7.29
 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LibmadActivity extends Activity {


    private Thread mThread;
    private short[] audioBuffer;
    private AudioTrack mAudioTrack;
    private Button btnPlay, btnPauseButton;


    private int samplerate;
    private int mAudioMinBufSize;
    private int ret;
    private NativeMP3Decoder MP3Decoder;

    private boolean mThreadFlag;

    private String filePath = "/sdcard/test.mp3";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        requestPermissions();
        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnPauseButton = (Button) findViewById(R.id.buttonPause);
        MP3Decoder = new NativeMP3Decoder();
        ret = MP3Decoder.initAudioPlayer(filePath, 0);
        if (ret == -1) {
            Log.i("conowen", "Couldn't open file '" + filePath + "'");

        } else {
            mThreadFlag = true;
            initAudioPlayer();

            audioBuffer = new short[1024 * 1024];
            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (mThreadFlag) {
                        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
                            // ****从libmad处获取data******/
                            MP3Decoder.getAudioBuf(audioBuffer,
                                    mAudioMinBufSize);
                            mAudioTrack.write(audioBuffer, 0, mAudioMinBufSize);

                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                }
            });
            mThread.start();

        }
        btnPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ret == -1) {
                    Log.i("conowen", "Couldn't open file '" + filePath + "'");
                    Toast.makeText(getApplicationContext(),
                            "Couldn't open file '" + filePath + "'",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                        //mThreadFlag = true;// 音频线程开始
                        mAudioTrack.play();
                        // mThread.start();
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        //mThreadFlag = true;// 音频线程开始
                        mAudioTrack.play();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Already in play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnPauseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (ret == -1) {
                    Log.i("conowen", "Couldn't open file '" + filePath + "'");
                    Toast.makeText(getApplicationContext(),
                            "Couldn't open file '" + filePath + "'",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        mAudioTrack.pause();

                    } else {
                        Toast.makeText(getApplicationContext(), "Already stop",
                                Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    private void initAudioPlayer() {
        // TODO Auto-generated method stub
        samplerate = MP3Decoder.getAudioSamplerate();
        System.out.println("samplerate = " + samplerate);
        samplerate = samplerate / 2;
        // 声音文件一秒钟buffer的大小
        mAudioMinBufSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
                // STREAM_ALARM：警告声
                // STREAM_MUSCI：音乐声，例如music等
                // STREAM_RING：铃声
                // STREAM_SYSTEM：系统声音
                // STREAM_VOCIE_CALL：电话声音

                samplerate,// 设置音频数据的采样率
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                mAudioMinBufSize, AudioTrack.MODE_STREAM);// 设置模式类型，在这里设置为流类型
        // AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
        // STREAM方式表示由用户通过write方式把数据一次一次得写到audiotrack中。
        // 这种方式的缺点就是JAVA层和Native层不断地交换数据，效率损失较大。
        // 而STATIC方式表示是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
        // 后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
        // 这种方法对于铃声等体积较小的文件比较合适。
    }

    static {
        System.loadLibrary("mad");

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mAudioTrack.stop();
        mAudioTrack.release();// 关闭并释放资源
        mThreadFlag = false;// 音频线程暂停
        MP3Decoder.closeAduioFile();
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
