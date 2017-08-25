package com.czp.arcprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    ArcProgress mProgress;
    SeekBar seekBar;
    SeekBar seekBar2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = (ArcProgress) findViewById(R.id.myProgress);

        // TODO: 2017/8/24 debug
//        seekBar = (SeekBar) findViewById(R.id.seekBar);
//        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
//        seekBar.setOnSeekBarChangeListener(this);
//        seekBar2.setOnSeekBarChangeListener(this);
//        seekBar.setVisibility(View.VISIBLE);
//        seekBar2.setVisibility(View.VISIBLE);

        // default usage
        // TODO: 2017/8/24 standard usage
        // before setmSpeedValue, do setMaxSpeed & setmOverSpeedValue
        mProgress.setOnCenterDraw(new OnTextCenter());
        mProgress.setMaxSpeed(20.f);
        mProgress.setmOverSpeedValue(16.f);
        mProgress.setmSpeedValue(12.f);

        // TODO: 2017/8/24 standard usage
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    // TODO: 2017/8/24 debug
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

    @Override
    public void onClick(View v) {
        // TODO: 2017/8/24 standard usage
        mProgress.setmOverSpeedValue((float) (mProgress.getMaxSpeed() * Math.random()));
        mProgress.setmSpeedValue((float) (mProgress.getmOverSpeedValue() * Math.random()));
    }
}
