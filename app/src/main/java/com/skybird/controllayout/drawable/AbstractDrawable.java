package com.skybird.controllayout.drawable;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.Nullable;

public abstract class AbstractDrawable extends Drawable {

    static final float DEFAULT_STROKE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , 1
            , Resources.getSystem().getDisplayMetrics());

    private Paint paint = new Paint();

    private boolean drawFrame;

    @Override
    public void setAlpha(int alpha) {
        //nothing to do
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        //nothing to do
    }

    @Deprecated
    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setDrawFrame(boolean drawFrame) {
        this.drawFrame = drawFrame;
    }

    public boolean isFrameDrawn() {
        return drawFrame;
    }

    public abstract boolean isPointInside(float x , float y);

    public boolean isPointInside(PointF point){
        return isPointInside(point.x , point.y);
    }

    public int getWidth(){
        return getBounds().right - getBounds().left;
    }

    public int getHeight(){
        return getBounds().bottom - getBounds().top;
    }

}
