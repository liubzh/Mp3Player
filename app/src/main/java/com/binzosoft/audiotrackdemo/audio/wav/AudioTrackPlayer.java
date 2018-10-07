package com.binzosoft.audiotrackdemo.audio.wav;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.binzosoft.audiotrackdemo.audio.wav.model.Lyric;
import com.binzosoft.audiotrackdemo.audio.wav.model.LyricItem;
import com.binzosoft.audiotrackdemo.audio.wav.model.WavInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AudioTrackPlayer implements AudioTrack.OnPlaybackPositionUpdateListener {

    private String TAG = getClass().getSimpleName();

    private WavInfo audioInfo;
    private WavDecoder audioDecoder;
    private InputStream audioInputStream;

    private int bufferSizeInBytes;
    private AudioTrack mAudioTrack;

    private Lyric lyric;

    private int positionOffset = 0;

    private OnCompletionListener completionListener;
    private OnLyricUpdateListener lyricUpdateListener;

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        Log.i(TAG, "onMarkerReached: " + audioTrack.getNotificationMarkerPosition());
        mAudioTrack.setPositionNotificationPeriod(0);
        mAudioTrack.stop();
        if (completionListener != null) {
            completionListener.onCompletion(mAudioTrack);
        }
        if (lyricUpdateListener != null) {
            lyricUpdateListener.onLyricUpdate(null);
        }
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {
        int position = audioTrack.getPlaybackHeadPosition() + positionOffset;
        audioTrack.getPlaybackHeadPosition();
        Log.i(TAG, "position: " + position);
        int msec = position / (audioTrack.getSampleRate() / 1000); // ms
        Log.i(TAG, "msec: " + msec + "ms");
        LyricItem nextItem = lyric.getNextItem();
        Log.i(TAG, "nextItem:" + nextItem);
        if (lyricUpdateListener != null && nextItem != null && msec >= nextItem.getTimestamp()) {
            Log.i(TAG, "lyric.next");
            if (lyric.next()) {
                //Log.i(TAG, "onLyricUpdate: " + lyric.getCurrentItem());
                lyricUpdateListener.onLyricUpdate(lyric);
            } else {
                lyricUpdateListener.onLyricUpdate(null);
            }
        }
    }

    public void setAudioData(InputStream inputStream) throws IOException, WavDecoder.DecoderException {
        Log.i(TAG, "setDataSource()");
        completionListener = null;
        this.audioInputStream = inputStream;
        audioDecoder = new WavDecoder(this.audioInputStream);
        this.audioInfo = audioDecoder.readHeader();

        /*
        bufferSizeInBytes = AudioTrack.getMinBufferSize(audioInfo.getRate(),
                audioInfo.isStereo()? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
                */
        bufferSizeInBytes = audioInfo.getDataSize();

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                audioInfo.getRate(),// 设置音频数据的采样率
                audioInfo.isStereo() ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                bufferSizeInBytes,
                AudioTrack.MODE_STATIC //AudioTrack.MODE_STREAM // 设置模式类型
        );

        // 播放到结束位置触发监听
        mAudioTrack.setNotificationMarkerPosition(getDuration());
        mAudioTrack.setPositionNotificationPeriod(audioInfo.getRate() * 100 / 1000); // 每隔 100 毫秒触发一次监听
        mAudioTrack.setPlaybackPositionUpdateListener(this);
    }

    public void setLyricData(InputStream inputStream) {
        Log.i(TAG, "setLyricData()");
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            isr = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(isr);
            lyric = new Lyric(getDuration());
            String str;
            int timestamp;
            String content;
            while ((str = reader.readLine()) != null) {
                //Log.i(TAG, str);
                content = str.substring(str.indexOf("]") + 1).trim();
                //Log.i(TAG, "content: " + content);
                str = str.substring(1, str.indexOf("]"));
                timestamp = (int) (
                        Integer.valueOf(str.substring(0, str.indexOf(":"))) * 60 * 1000 +
                        Double.valueOf(str.substring(str.indexOf(":") + 1)) * 1000
                );
                //Log.i(TAG, "timestamp: " + timestamp);
                LyricItem item = new LyricItem(timestamp, content);
                Log.i(TAG, item.toString());
                lyric.addItem(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDuration() {
        if (audioInfo == null) {
            return 0;
        } else {
            return audioInfo.getDataSize() / audioInfo.getChannels() / 2;
        }
    }

    public void prepare() throws IOException {
        byte[] data = new byte[audioInfo.getDataSize()];
        audioInputStream.read(data, 0, data.length);
        mAudioTrack.write(data, 0, data.length);
    }

    public void start() {
        Log.i(TAG, "start()");
        mAudioTrack.play();
        //new WriteThread().start();
    }

    public void pause() {
        Log.i(TAG, "pause()");
        mAudioTrack.pause();
    }

    public void stop() {
        Log.i(TAG, "stop()");
        mAudioTrack.flush();
        mAudioTrack.stop();
    }

    public void release() {
        Log.i(TAG, "release()");
        try {
            audioInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        completionListener = null;
        mAudioTrack.release(); // 关闭并释放资源
    }

    public boolean isPlaying() {
        boolean playing = mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
        Log.i(TAG, "isPlaying() ? " + playing);
        return playing;
    }

    public boolean seekTo(int msec) {
        int position = (int)(1l * msec * audioInfo.getRate() / 1000);
        Log.i(TAG, "position: " + position);
        mAudioTrack.stop();
        int result = mAudioTrack.setPlaybackHeadPosition(position);
        boolean success = result == AudioTrack.SUCCESS;
        Log.i(TAG, "seekTo: " + msec + ", result: " + result + ", success: " + success);
        mAudioTrack.play();
        if (success) {
            positionOffset = position;
            lyric.target(msec);
            if (lyricUpdateListener != null) {
                lyricUpdateListener.onLyricUpdate(lyric);
            }
        }
        Log.i(TAG, ":::" + lyric.getCurrentItem());
        return success;
    }

    public void setOnCompletionListener(OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setOnLyricUpdateListener(OnLyricUpdateListener lyricUpdateListener) {
        this.lyricUpdateListener = lyricUpdateListener;
    }

    class WriteThread extends Thread {
        byte[] data = new byte[bufferSizeInBytes];

        @Override
        public void run() {
            try {
                while (true) {
                    if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
                        audioInputStream.read(data, 0, data.length);
                    } else {
                        break;
                    }
                    mAudioTrack.write(data, 0, data.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnCompletionListener {
        void onCompletion(AudioTrack audioTrack);
    }

    public interface OnLyricUpdateListener {
        void onLyricUpdate(Lyric lyric);
    }

}
