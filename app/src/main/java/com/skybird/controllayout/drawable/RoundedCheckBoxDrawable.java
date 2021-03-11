package com.skybird.controllayout.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class RoundedCheckBoxDrawable extends CircleDrawable {


    private Drawable checkIcon;


    public RoundedCheckBoxDrawable() {
        this(0);
    }

    public RoundedCheckBoxDrawable(float radius) {
        this(radius, 0, 0);
    }

    public RoundedCheckBoxDrawable(float radius, float centerX, float centerY) {
        super(radius, centerX, centerY);
    }

    @Override
    public void draw(Canvas canvas) {

        if (isFrameDrawn()){
            super.draw(canvas);
        }

        if (checkIcon != null) {

            int left = (int) (centerX - radius);
            int top = (int) (centerY - radius);
            int right = (int) (centerX + radius);
            int bottom = (int) (centerY + radius);

            checkIcon.setBounds(left, top, right, bottom);
            checkIcon.draw(canvas);
        }
    }

    public Drawable getCheckIcon() {
        return checkIcon;
    }

    public void setCheckIcon(Drawable checkIcon) {
        this.checkIcon = checkIcon;
    }


}
