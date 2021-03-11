package com.skybird.controllayout.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;


public class CircleDrawable extends AbstractDrawable {



    float centerX, centerY;
    float radius;


    public CircleDrawable(float radius, float centerX, float centerY) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;

        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(DEFAULT_STROKE_WIDTH);
        getPaint().setAntiAlias(true);



    }

    public CircleDrawable(float radius) {
        this(radius, 0, 0);
    }

    public CircleDrawable() {
        this(0);

    }


    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    @Override
    public void draw(Canvas canvas) {

        canvas.drawCircle(centerX, centerY, radius, getPaint());
    }

    @Override
    public boolean isPointInside(float x, float y) {
        float d = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
        return d <= radius * radius;
    }


}
