package com.skybird.controllayout.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;



public class TextDrawable extends AbstractDrawable {

    private final RectF frameBounds = new RectF();
    //bounds of the text this will be used to adjust the text inside of the frame
    private final Rect textBounds = new Rect();

    private String text;
    private TextPaint textPaint = new TextPaint();

    private float cornerRadius;


    public TextDrawable(Rect bounds) {

        setBounds(bounds);

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);

        getPaint().setAntiAlias(true);
        getPaint().setStrokeWidth(DEFAULT_STROKE_WIDTH);
        getPaint().setStyle(Paint.Style.STROKE);
    }


    public TextDrawable() {
        this(new Rect());
    }


    public TextPaint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(TextPaint textPaint) {
        this.textPaint = textPaint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCornerRadius(float cornerRadius){
        this.cornerRadius = cornerRadius;
    }

    public float getCornerRadius(){
        return cornerRadius;
    }


    @Override
    public void draw(Canvas canvas) {


        if (isFrameDrawn()) {
            frameBounds.set(getBounds());
            canvas.drawRoundRect(frameBounds, cornerRadius, cornerRadius, getPaint());
        }

        if (text != null) {
            adjustTextAndDraw(canvas);
        }


    }


    @Override
    public boolean isPointInside(float x , float y){
        return x > getBounds().left && x < getBounds().right && y > getBounds().top && y <getBounds().bottom;
    }

    //measure text length and adjust it for drawing inside the frame
    private void adjustTextAndDraw(Canvas canvas){

        int width = getBounds().right - getBounds().left;

        textPaint.getTextBounds(text , 0 , text.length() , textBounds);

        final int charHeight = textBounds.bottom - textBounds.top;
        final int charWidth = (textBounds.right - textBounds.left) / text.length();

        int drawingTopPos = getBounds().centerY() + charHeight/2;

        for (int index =0; index<text.length();){

            String tempText ="";
            int tempTextWidth =0;

            while (index < text.length() &&tempTextWidth < width - (2 * charWidth) ){

                //concat char of the main string to the tempText string and increase index
                //until tempTextWidth being greater than main width

                tempText += text.charAt(index++);
                textPaint.getTextBounds(tempText , 0 , tempText.length() , textBounds);
                tempTextWidth = textBounds.right - textBounds.left;
            }

            //draw measured text and
            int x =0;
            switch (textPaint.getTextAlign()){
                case LEFT:
                    x = getBounds().left;
                    break;
                case CENTER:
                    x = getBounds().centerX();
                    break;
                case RIGHT:
                    x = getBounds().right;
                    break;
            }
            canvas.drawText(tempText , x , drawingTopPos , textPaint);
            drawingTopPos += charHeight;

        }

    }

}

