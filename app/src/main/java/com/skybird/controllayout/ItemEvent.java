package com.skybird.controllayout;

public class ItemEvent {

    public static final int NONE = -1;
    public static final int ICON= 0;
    public static final int TEXT = 1;


    private float x , y;
    public int item = NONE;


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getItem() {
        return item;
    }
}
