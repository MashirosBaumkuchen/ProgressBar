package com.czp.arcprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    ArcProgress mProgress;
    SeekBar seekBar;
    SeekBar seekBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = (ArcProgress) findViewById(R.id.myProgress);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);

        // default usage
        mProgress.setOnCenterDraw(new OnTextCenter());
        mProgress.setmSpeedValue(0.0f);
        mProgress.setMaxSpeed(7.2f);
        mProgress.setmOverSpeedValue(6.8f);

        seekBar2.setProgress((int) (680 / 7.2));
    }

    @Override
    public void onProgressChanged(SeekBar bar, int i, boolean b) {
        float speed = (float) (i * mProgress.getMaxSpeed() / 100);
        if (bar.getId() == R.id.seekBar) {
            // todo
            if (speed > mProgress.getmOverSpeedValue()) return;
            mProgress.setmSpeedValue((float) (i * mProgress.getMaxSpeed() / 100));
        } else if (bar.getId() == R.id.seekBar2) {
            if (speed < mProgress.getmSpeedValue()) return;
            mProgress.setmOverSpeedValue((float) (i * mProgress.getMaxSpeed() / 100));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    Thread speedGradient = new Thread(){
        @Override
        public void run() {
            super.run();
        }
    };

}
