package com.bytedance.videoplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IJKPlayerActivity extends AppCompatActivity {
    private VideoPlayerIJK ijkPlayer;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private SeekBar seekBar;
    private Runnable update;
    private int count;
    private TextView textView;
    private Handler handler = new Handler();

    private void init() {

        update = new Runnable() {
            @Override
            public void run() {
                float progress = (float) (ijkPlayer.getCurrentPosition())/(float)(ijkPlayer.getDuration());
                seekBar.setProgress((int)(progress*100));

                long current_hour = ijkPlayer.getCurrentPosition()/(1000*3600);
                long current_minute = ijkPlayer.getCurrentPosition()%(1000*3600) / (60 * 1000);
                long current_second = ijkPlayer.getCurrentPosition()%(60 * 1000) / 1000;

                String current_time = String.format("%s:%s:%s",
                        String.format( "%02d", current_hour),
                        String.format("%02d", current_minute),
                        String.format("%02d", current_second));

                long total_hour = ijkPlayer.getDuration()/(1000*3600);
                long total_minute = ijkPlayer.getDuration()%(1000*3600) / (60 * 1000);
                long total_second = ijkPlayer.getDuration()%(60 * 1000) / 1000;

                String total_time = String.format("%s:%s:%s",
                        String.format("%02d", total_hour),
                        String.format("%02d", total_minute),
                        String.format("%02d", total_second));
                textView.setText(current_time + "/" + total_time);
                seekBar.invalidate();
                textView.invalidate();
                handler.postDelayed(update, 100);
            }
        };
        handler.post(update);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ijkplayer);
        setTitle("ijkPlayer");
        init();

        ijkPlayer = findViewById(R.id.ijkPlayer);
        seekBar = findViewById(R.id.seekbar);
        textView = findViewById(R.id.textView2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser == true)
                {
                    double f_progress = (double) (progress)/100.0;
                    ijkPlayer.seekTo((long) (f_progress * ijkPlayer.getDuration()));
                    ijkPlayer.start();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer.setListener(new VideoPlayerListener());

//        ijkPlayer.setVideoPath(getVideoPath());
        ijkPlayer.setVideoResource(R.raw.bytedance);

        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.start();

            }
        });

        findViewById(R.id.buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.pause();
            }
        });

//        findViewById(R.id.buttonSeek).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ijkPlayer.seekTo(20 * 1000);
//            }
//        });

//        final float progress = (float) (ijkPlayer.getCurrentPosition())/(float)(ijkPlayer.getDuration());
//        seekBar.setProgress((int)(progress*100));



    }


    private String getVideoPath() {
        return "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
//        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ijkPlayer.isPlaying()) {
            ijkPlayer.stop();
        }

        IjkMediaPlayer.native_profileEnd();
    }
}
