package com.czp.arcprogress;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

public class OnTextCenter implements ArcProgress.OnCenterDraw {
    private int textColor = Color.WHITE;
    private int textHintColor = Color.argb(255, 49, 49, 49);
    private int textSize = 138;
    private int textSize2 = 88;
    private int textResSize = 24;
    private int textDesSize = 36;

    public OnTextCenter() {
        super();
    }

    @Override
    public void draw(Canvas canvas, RectF rectF, float x, float y, float speedValue, float maxSpeed) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        textPaint.setTypeface(font);
        String[] speed = (speedValue + "").split("\\.");
        String unit = "km/h";
        String desc = "Max speed 6.8";

        textPaint.setTextSize(textSize2);
        float textOW = textPaint.measureText(speed[1]);
        textPaint.setTextSize(textSize);
        float textW = textPaint.measureText("0");
        float textW2 = textPaint.measureText(speed[0] + ".");
        float textNumLWidth;
        if (speed[0].length() == 1)
            textNumLWidth = textW + textW2 + textOW;
        else
            textNumLWidth = textOW + textW2;

        // TODO: 2017/8/17 speed
        float textX = x - (textNumLWidth) / 2;
        float textY = y - ((textPaint.descent() + textPaint.ascent()) / 2);

        if (speed[0].length() == 1) {
            textPaint.setColor(textHintColor);
            canvas.drawText("0", textX, textY, textPaint);
            textX = textX + textW;
            textPaint.setColor(textColor);
            canvas.drawText(speed[0] + ".", textX, textY, textPaint);
        } else {
            char[] a = speed[0].toCharArray();
            textPaint.setColor(textColor);
            canvas.drawText(a[0] + "", textX, textY, textPaint);
            textX = textX + textW;
            canvas.drawText(a[1] + ".", textX, textY, textPaint);
        }

        textPaint.setTextSize(textSize2);
        float textResX;
        float textResY;
        if (speed[0].length() == 1) {
            textResX = textX + textW2;
            textResY = textY;
            canvas.drawText(speed[1], textResX, textResY, textPaint);
        } else {
            textResX = textX + textW2 - textW;
            textResY = textY;
            canvas.drawText(speed[1], textResX, textResY, textPaint);
        }

        textPaint.setTextSize(textResSize);
        float textUnitX = x - (textPaint.measureText(unit)) / 2;
        float textUnitY = textResY - (textPaint.descent() + textPaint.ascent()) * 3;
        canvas.drawText(unit, textUnitX, textUnitY, textPaint);

        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(textDesSize);
        textPaint.setColor(Color.argb(255, 246, 108, 28));
        float textDescX = x - (textPaint.measureText(desc)) / 2;
        float textDescY = textUnitY - (textPaint.descent() + textPaint.ascent()) * 2;
        canvas.drawText(desc, textDescX, textDescY, textPaint);
    }
}
