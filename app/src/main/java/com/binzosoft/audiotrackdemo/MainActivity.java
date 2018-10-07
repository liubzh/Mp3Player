package com.binzosoft.audiotrackdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.binzosoft.audiotrackdemo.audio.wav.AudioTrackPlayer;
import com.binzosoft.audiotrackdemo.audio.wav.WavDecoder;
import com.binzosoft.audiotrackdemo.audio.wav.model.Lyric;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private AudioTrackPlayer player;
    private TextView tv;
    private Button bt1;
    private Button bt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.seekTo();

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        bt1 = (Button) findViewById(R.id.sample_button1);
        bt1.setOnClickListener(this);
        bt2 = (Button) findViewById(R.id.sample_button2);
        bt2.setOnClickListener(this);


        player = new AudioTrackPlayer();
        player.setOnLyricUpdateListener(lyricUpdateListener);

        try {
            player.setAudioData(new FileInputStream("/sdcard/Lesson01.wav"));
            player.setLyricData(new FileInputStream("/sdcard/Lesson01.lrc"));
            player.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavDecoder.DecoderException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            if (id == R.id.sample_button1) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                }
            } else if (id == R.id.sample_button2) {
                player.seekTo(121400);
//                player.seekTo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AudioTrackPlayer.OnLyricUpdateListener lyricUpdateListener = new AudioTrackPlayer.OnLyricUpdateListener() {
        @Override
        public void onLyricUpdate(Lyric lyric) {
            if (lyric == null) {
                tv.setText("");
            } else {
                tv.setText(lyric.getCurrentItem().getContent());
            }
        }
    };
}
