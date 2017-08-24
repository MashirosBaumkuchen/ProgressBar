package com.czp.arcprogress;

/**
 * Created by biao.ma on 2017/8/21.
 */

public class SpeedGradient extends Thread {
    private String TAG = "SpeedGradient";
    public static final int FLAG_SPEED = 0;
    public static final int FLAG_OVERSPEED = 1;
    public final int MODE_ADD = 0;
    public final int MODE_SUM = 1;
    private int mode;
    private int flag;
    private ArcProgress bar;
    private float from;
    private float to;
    private float maxSpeed;
    private float step;

    public SpeedGradient(ArcProgress arcProgress, float from, float to, float maxSpeed, int flag) {
        super();
        this.bar = arcProgress;
        this.from = (flag == SpeedGradient.FLAG_SPEED) ? from : (maxSpeed - from);// overSpeed刻度值换成overSpeed长度值;
        this.flag = flag;
        this.maxSpeed = maxSpeed;
        this.to = (flag == SpeedGradient.FLAG_SPEED) ? to : (maxSpeed - to);// overSpeed刻度值换成overSpeed长度值
        this.step = Math.abs(this.from - this.to) / 20f;

        if (this.from < this.to) mode = this.MODE_ADD;
        else if (this.from > this.to) mode = this.MODE_SUM;

    }

    @Override
    public void run() {
        if (mode == MODE_ADD) {
            for (int i = 0; i < 20; i++) {
                bar.post(new Runnable() {
                    @Override
                    public void run() {
                        from += step;
                        if (flag == SpeedGradient.FLAG_SPEED) {
                            bar.setProgress((int) (100 * from / maxSpeed));
                            bar.setmShowSpeed(from);
                        } else if (flag == SpeedGradient.FLAG_OVERSPEED) {
                            bar.setOverProgress(100 - (int) (100 * from / maxSpeed));//args 刻度值
                        }
                    }
                });
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (mode == MODE_SUM) {
            for (int i = 0; i < 20; i++) {
                bar.post(new Runnable() {
                    @Override
                    public void run() {
                        from -= step;
                        if (flag == SpeedGradient.FLAG_SPEED) {
                            bar.setProgress((int) (100 * from / maxSpeed));
                            bar.setmShowSpeed(from);
                        } else if (flag == SpeedGradient.FLAG_OVERSPEED) {
                            bar.setOverProgress(100 - (int) (100 * from / maxSpeed));
                        }
                    }
                });
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
