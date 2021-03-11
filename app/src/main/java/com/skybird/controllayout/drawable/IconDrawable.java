package com.skybird.controllayout.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class IconDrawable extends AbstractDrawable {

    private final RectF frameBounds = new RectF();
    private Drawable icon;
    private Paint paint = new Paint();

    private float strokeRadius;

    public IconDrawable(Rect bounds) {
        setBounds(bounds);
    }

    public IconDrawable() {
    }

    @Override
    public void draw(@NonNull Canvas canvas) {



        if (isFrameDrawn()) {
            frameBounds.set(getBounds());
            canvas.drawRoundRect(frameBounds, strokeRadius, strokeRadius, paint);
        }
        if (icon != null) {
            /*
            int left =(int) (getBounds().left + strokeRadius);
            int top = (int) (getBounds().top + strokeRadius);
            int right = (int)(getBounds().right - strokeRadius);
            int bottom = (int) (getBounds().bottom - strokeRadius);

             */

            icon.setBounds(getBounds());

            icon.draw(canvas);
        }

    }

    @Override
    public boolean isPointInside(float x , float y){
        return x > getBounds().left && x < getBounds().right && y > getBounds().top && y <getBounds().bottom;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


    public void setStrokeRadius(float strokeRadius){
         this.strokeRadius = strokeRadius;
    }

    public float getStrokeRadius(){
        return strokeRadius;
    }

}
