package com.czp.arcprogress;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

/**
 * Created by caizepeng on 16/9/8.
 */
public class OnTextCenter implements ArcProgress.OnCenterDraw {
    private int textColor = Color.BLACK;
    private int textSize = 80;
    private int textResSize = 40;
    private int textDesSize = 30;

    public OnTextCenter(int textColor, int textSize) {
        this.textColor = textColor;
        this.textSize = textSize;
    }

    public OnTextCenter() {
        super();
    }

    @Override
    public void draw(Canvas canvas, RectF rectF, float x, float y, float strokeWidth, int progress) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        textPaint.setTypeface(font);
        String progressStr = progress + ".";
        String progressOverStr = "0";
        String unit = "km/h";
        String desc = "Max speed is 40.8";

        textPaint.setTextSize(textSize);
        float textW = textPaint.measureText(progressStr);
        textPaint.setTextSize(textResSize);
        float textOW = textPaint.measureText(progressOverStr);

        // TODO: 2017/8/17 speed
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        float textX = x - (textW + textOW) / 2;
        float textY = y;
        //float textY = y - ((textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(progressStr, textX, textY, textPaint);
        textPaint.setTextSize(textResSize);
        float textResX = 2 * x - (textX + textOW);
        float textResY = textY;
        canvas.drawText(progressOverStr, textResX, textResY, textPaint);

        textPaint.setColor(Color.GRAY);
        float textUnitX = x - (textPaint.measureText(unit)) / 2;
        float textUnitY = textResY - (textPaint.descent() + textPaint.ascent()) * 2;
        canvas.drawText(unit, textUnitX, textUnitY, textPaint);

        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(textDesSize);
        textPaint.setColor(Color.BLUE);
        float textDescX = x - (textPaint.measureText(desc)) / 2;
        float textDescY = textUnitY - (textPaint.descent() + textPaint.ascent()) * 2;
        canvas.drawText(desc, textDescX, textDescY, textPaint);
    }
}
