package com.binzosoft.audiotrackdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'libmad' library on application startup.
    static {
        //System.loadLibrary("mad");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NativeMP3Decoder mp3Decoder = new NativeMP3Decoder();

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(mp3Decoder.stringFromJNI());
    }
}
