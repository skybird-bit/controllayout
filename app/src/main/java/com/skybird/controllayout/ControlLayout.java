package com.skybird.controllayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.skybird.controllayout.drawable.IconDrawable;
import com.skybird.controllayout.drawable.RoundedCheckBoxDrawable;
import com.skybird.controllayout.drawable.TextDrawable;

public class ControlLayout extends ViewGroup {

    public static final int TEXT_ALIGN_START = -1;
    public static final int TEXT_ALIGN_CENTER = 0;
    public static final int TEXT_ALIGN_END = 1;


    public static final int STROKE_LINE_CAP_ROUND = 1;
    public static final int STROKE_LINE_CAP_SQUARE = 0;

    private static final DisplayMetrics DISPLAY_METRICS = Resources.getSystem().getDisplayMetrics();

    private GestureDetector touchDetector;

    private OnItemClickListener itemClickListener;
    private OnCheckedChangeListener checkedChangeListener;
    private OnItemLongPressedListener itemLongPressedListener;

    private boolean checked;

    private Paint paint;

    private IconDrawable iconRegion;
    private TextDrawable textRegion;
    private RoundedCheckBoxDrawable checkboxRegion;

    private boolean iconEnabled = true;
    private boolean textEnabled = true;
    private boolean checkboxEnabled = true;


    private Drawable checkedIconSource;
    private Drawable uncheckedIconSource;

    private int controlRegionHeight;
    private int margin;

    private float controlRegionIconWeight;
    private float controlRegionTextWeight;
    private float controlRegionCheckboxWeight;

    private int strokeLineCap = STROKE_LINE_CAP_SQUARE;

    public ControlLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ControlLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ControlLayout);
        initDrawables(ta);
        init(ta);
        ta.recycle();

    }


    private void init(TypedArray ta) {

        //enable and/or disable contents of the control part
        iconEnabled = ta.getBoolean(R.styleable.ControlLayout_iconEnabled, iconEnabled);
        textEnabled = ta.getBoolean(R.styleable.ControlLayout_textEnabled, textEnabled);
        checkboxEnabled = ta.getBoolean(R.styleable.ControlLayout_checkboxEnabled, checkboxEnabled);


        int defaultControlHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f
                , Resources.getSystem().getDisplayMetrics());
        controlRegionHeight = ta.getDimensionPixelSize(R.styleable.ControlLayout_controlRegionHeight, defaultControlHeight);


        controlRegionIconWeight = ta.getFloat(R.styleable.ControlLayout_controlRegionIconWeight, 2);
        controlRegionTextWeight = ta.getFloat(R.styleable.ControlLayout_controlRegionTextWeight, 12);
        controlRegionCheckboxWeight = ta.getFloat(R.styleable.ControlLayout_controlRegionCheckboxWeight, 2);



        //set margin for control part of the layout
        margin = ta.getDimensionPixelSize(R.styleable.ControlLayout_margin, 0);

        touchDetector = new GestureDetector(getContext(), new OnTouchGestureListener(ControlLayout.this));

    }



    private void initDrawables(TypedArray ta){

        paint = new Paint();

        //init control part contents iconBox , textBox and checkBox
        iconRegion = new IconDrawable();
        iconRegion.setPaint(paint);

        textRegion = new TextDrawable();
        textRegion.setPaint(paint);

        checkboxRegion = new RoundedCheckBoxDrawable();
        checkboxRegion.setPaint(paint);



        /*
        init stroke of the control part contents
        (strokeWidth, stroke color) which set to the paint field
         */
        int defaultStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f,
                DISPLAY_METRICS);
        int strokeWidth = ta.getDimensionPixelSize(R.styleable.ControlLayout_controlRegionFrameStrokeWidth,
                defaultStrokeWidth);

        int controlRegionFrameColor = ta.getColor(R.styleable.ControlLayout_controlRegionFrameColor, 0xff0091ea);
        int controlRegionFrameStyle = ta.getInteger(R.styleable.ControlLayout_controlRegionFrameStyle, 0);


        //paint for drawing frame of contents
        paint.setAntiAlias(true);
        paint.setStyle(controlRegionFrameStyle == 0 ? Paint.Style.STROKE : Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(controlRegionFrameColor);


        /*
            requirements for drawing texts in the textBox field
            (textSize , textColor and textAlign)
            size indicates what size (at most in sp) text should be have
            color indicates what color text should be have
            and align indicates that where text should be appear (left , center ,and or right)

            these are need for the textBox for its text appearance
         */

        int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f,
                Resources.getSystem().getDisplayMetrics());
        int textSize = ta.getDimensionPixelSize(R.styleable.ControlLayout_android_textSize, defaultTextSize);


        int textColor = ta.getColor(R.styleable.ControlLayout_android_textColor, 0xff212121);
        String text = ta.getString(R.styleable.ControlLayout_android_text);

        int textAlign = ta.getInteger(R.styleable.ControlLayout_textAlign , TEXT_ALIGN_CENTER);

        //setting up the text appearance
        TextPaint textPaint = textRegion.getTextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        setTextAlign(textAlign);
        textRegion.setText(text);

        //whether to draw frame for contents of the control part or not
        boolean iconFrameEnabled = ta.getBoolean(R.styleable.ControlLayout_iconFrameEnabled, true);
        boolean textFrameEnabled = ta.getBoolean(R.styleable.ControlLayout_textFrameEnabled, true);
        boolean checkboxFrameEnabled = ta.getBoolean(R.styleable.ControlLayout_checkboxFrameEnabled, true);

        iconRegion.setDrawFrame(iconFrameEnabled);
        textRegion.setDrawFrame(textFrameEnabled);
        checkboxRegion.setDrawFrame(checkboxFrameEnabled);


        /*
            how stroke line for frame of iconBox and textBox drawn there are two constants:
            round and square
         */
        strokeLineCap = ta.getInt(R.styleable.ControlLayout_strokeLineCapItems, strokeLineCap);
        setStrokeLineCap(strokeLineCap);


        iconRegion.setIcon(ta.getDrawable(R.styleable.ControlLayout_iconSrc));

        checkedIconSource = ta.getDrawable(R.styleable.ControlLayout_checkIconSrc);
        uncheckedIconSource = ta.getDrawable(R.styleable.ControlLayout_uncheckIconSrc);
        checkboxRegion.setCheckIcon(uncheckedIconSource);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final boolean measureMatchParentChildren = widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY;

        int maxWidth = 0;
        int maxHeight = 0;
        int childState = 0;


        for (int i = 0; i < getChildCount(); i++) {

            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);


                childState = View.combineMeasuredStates(childState, child.getMeasuredState());

            }
        }


        maxWidth += getPaddingStart() + getPaddingEnd() +
                (margin * 2 /*margin left and margin right of the controller */);

        maxHeight += controlRegionHeight + getPaddingTop() + getPaddingBottom() +
                (margin * 2/*margin top and margin bottom of the controller */);

        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());


        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidthMeasureSpec;
            if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth()
                                - getPaddingLeft() - getPaddingRight() - lp.leftMargin - lp.rightMargin
                        , MeasureSpec.EXACTLY);
            } else {
                childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        getPaddingStart() + getPaddingEnd() + lp.leftMargin + lp.rightMargin, lp.width);
            }


            int childHeightMeasureSpec;
            if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight()
                                - getPaddingTop() - getPaddingBottom() - lp.topMargin - lp.bottomMargin
                                - (controlRegionHeight + margin * 2)
                        , MeasureSpec.EXACTLY);
            } else {

                childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
                        + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        layoutDrawable(left, top, right, bottom);
        layoutChildren();

    }

    private void layoutChildren() {

        int parentLeft = getPaddingStart();
        int parentTop = getPaddingTop() + controlRegionHeight + margin * 2;


        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                int childLeft = parentLeft + lp.leftMargin;
                int childTop = parentTop + lp.topMargin;

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    private void layoutDrawable(int left, int top, int right, int bottom) {

        final float sumWeight = controlRegionIconWeight + controlRegionTextWeight + controlRegionCheckboxWeight;
        System.out.println("sum weight: " + sumWeight);

        int parentWidth = getMeasuredWidth();
        //later controller will have a user specific height
        int parentHeight = getMeasuredHeight();

        int parentLeft = getPaddingStart();
        int parentRight = parentLeft + parentWidth - getPaddingEnd();


        int iconBoxWidth = (int) (parentWidth * (controlRegionIconWeight / sumWeight));
        int textBoxWidth = (int) ((controlRegionTextWeight / sumWeight) * parentWidth);
        int checkBoxWidth = (int) ((controlRegionCheckboxWeight / sumWeight) * parentWidth);


        final int topBound = getPaddingTop() + margin;
        final int bottomBound = topBound + controlRegionHeight - margin;

        int leftBound, rightBound = parentLeft;

        if (iconEnabled) {
            leftBound = parentLeft + margin;

            rightBound = leftBound + iconBoxWidth - margin;

            iconRegion.setBounds(leftBound, topBound, rightBound, bottomBound);
        }

        if (textEnabled) {

            leftBound = rightBound + margin;
            rightBound = leftBound + textBoxWidth - margin;

            textRegion.setBounds(leftBound, topBound, rightBound, bottomBound);
        }

        if (checkboxEnabled) {
            leftBound = rightBound + margin;
            rightBound = getRight() - getPaddingRight() - margin;

            int radius = Math.min((rightBound - leftBound) / 2 , (bottomBound - topBound)/2);
            int centerX = leftBound + (rightBound - leftBound) /2;
            int centerY =topBound + (bottomBound - topBound)/2;
            checkboxRegion.setCenterX(centerX);
            checkboxRegion.setCenterY(centerY);
            checkboxRegion.setRadius(radius);
            checkboxRegion.setBounds(leftBound, topBound, rightBound, bottomBound);

        }

        System.out.println("CheckBox , left: " + checkboxRegion.getBounds().left  + " right: " + checkboxRegion.getBounds().right);



    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);


        if (iconEnabled)
            iconRegion.draw(canvas);

        if (textEnabled)
            textRegion.draw(canvas);


        if (checkboxEnabled) {

            checkboxRegion.draw(canvas);
        }
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        requestDisallowInterceptTouchEvent(true);
        touchDetector.onTouchEvent(event);
        performClick();

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        checkedChangeListener = listener;
    }

    public void setOnLongItemPressedListener(OnItemLongPressedListener listener) {
        itemLongPressedListener = listener;
    }


    public int getControlRegionHeight() {
        return controlRegionHeight;
    }

    public void setControlRegionHeight(int controlRegionHeight) {
        this.controlRegionHeight = controlRegionHeight;
        requestLayout();
    }

    public int getBodyHeight() {
        return getHeight() - getControlRegionHeight();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
    }


    public String getText() {
        return textRegion.getText();
    }

    public void setText(String text) {
        textRegion.setText(text);
        invalidate();
    }

    public int getTextColor() {
        return textRegion.getTextPaint().getColor();
    }

    public void setTextColor(int textColor) {
        textRegion.getTextPaint().setColor(textColor);
        invalidate();
    }

    public float getTextSize() {
        return textRegion.getTextPaint().getTextSize();
    }

    public void setTextSize(int textSize) {
        textRegion.getTextPaint().setTextSize(textSize);
        invalidate();
    }

    public Paint.Align getTextAlign() {
        return textRegion.getTextPaint().getTextAlign();
    }

    public void setTextAlign(int align){
        switch (align){
            case TEXT_ALIGN_START:
                setTextAlign(Paint.Align.LEFT);break;
            case TEXT_ALIGN_CENTER:
                setTextAlign(Paint.Align.CENTER);
                break;
            case TEXT_ALIGN_END:
                setTextAlign(Paint.Align.RIGHT);
                break;
        }
    }

    public void setTextAlign(Paint.Align align) {
        textRegion.getTextPaint().setTextAlign(align);
    }

    public float getStrokeWidth() {
        return paint.getStrokeWidth();
    }


    public void setStrokeWidth(int strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public int getStrokeColor() {
        return paint.getColor();
    }

    public void setStrokeColor(int strokeColor) {
        paint.setColor(strokeColor);
        invalidate();
    }


    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
        invalidate();
    }


    /*
        get StrokeLineCap of IconDrawable and TextDrawable the values are 0 and 1
        0 means Square and 1 is Round the constants are defined as STROKE_LINE_CAP_ROUND and
        STROKE_LINE_CAP_SQUARE as field
     */
    public int getStrokeLineCap() {
        return strokeLineCap;
    }


    /*
        set StrokeLineCap of IconDrawable and TextDrawable values are 0 for Square and 1 for
        Round the constants are also defined as fields:
        STROKE_LINE_CAP_SQUARE ,STROKE_LINE_CAP_ROUND
     */
    public void setStrokeLineCap(int strokeLineCap) {
        float value = 0F;
        if (strokeLineCap == STROKE_LINE_CAP_ROUND) {
            value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP ,4 , DISPLAY_METRICS);

            iconRegion.setStrokeRadius(value);
            textRegion.setCornerRadius(value);


        } else {
            iconRegion.setStrokeRadius(value);
            textRegion.setCornerRadius(value);
        }
        invalidate();
    }

    //set draw frame of the IconDrawable
    public void setDrawIconFrame(boolean drawIconFrame) {
        iconRegion.setDrawFrame(drawIconFrame);
        invalidate();
    }

    //check if frame of the IconDrawable is set to draw
    public boolean isIconFrameDrawn() {
        return iconRegion.isFrameDrawn();
    }

    //set draw frame of the TextDrawable
    public void setDrawTextBoxFrame(boolean drawTextBoxFrame) {
        textRegion.setDrawFrame(drawTextBoxFrame);
        invalidate();
    }

    //Check if frame of the TextDrawable is set to draw
    public boolean isTextBoxFrameDrawn() {
        return textRegion.isFrameDrawn();
    }

    //set draw frame of the RoundedCheckBoxDrawable
    public void setDrawCheckBoxFrame(boolean drawCheckBoxFrame) {
        checkboxRegion.setDrawFrame(drawCheckBoxFrame);
        invalidate();
    }

    //check if frame of the RoundedCheckBoxDrawable is set to draw
    public boolean isCheckBoxFrameDrawn() {
        return checkboxRegion.isFrameDrawn();
    }


    //set icon of the Icon drawable
    public Drawable getIcon() {
        return iconRegion.getIcon();
    }

    public void setIcon(Drawable icon) {
        this.iconRegion.setIcon(icon);
        invalidate();
    }

    //set icon of the IconDrawable
    public void setIcon(Bitmap icon) {

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), icon);
        drawable.setCornerRadius(iconRegion.getStrokeRadius());

        this.iconRegion.setIcon(drawable);

        invalidate();
    }


    public void setIcon(int resId) {
        this.iconRegion.setIcon(ResourcesCompat.getDrawable(getResources(), resId, null));

    }


    public boolean isIconEnabled() {
        return iconEnabled;
    }

    public void setIconEnabled(boolean iconEnabled) {
        this.iconEnabled = iconEnabled;
        invalidate();
    }

    public boolean isTextEnabled() {
        return textEnabled;
    }

    public void setTextEnabled(boolean textEnabled) {
        this.textEnabled = textEnabled;
        invalidate();
    }

    public boolean isCheckboxEnabled() {
        return checkboxEnabled;
    }

    public void setCheckboxEnabled(boolean checkboxEnabled) {
        this.checkboxEnabled = checkboxEnabled;
        invalidate();
    }


    //three main view drawables
    public IconDrawable getIconRegion() {
        return iconRegion;
    }

    public TextDrawable getTextRegion() {
        return textRegion;
    }

    public RoundedCheckBoxDrawable getCheckboxRegion() {
        return checkboxRegion;
    }


    public float getControlRegionIconWeight() {
        return controlRegionIconWeight;
    }

    public void setControlRegionIconWeight(float controlRegionIconWeight) {
        this.controlRegionIconWeight = controlRegionIconWeight;
        invalidate();
    }

    public float getControlRegionTextWeight() {
        return controlRegionTextWeight;
    }

    public void setControlRegionTextWeight(float controlRegionTextWeight) {
        this.controlRegionTextWeight = controlRegionTextWeight;
        invalidate();
    }

    public float getControlRegionCheckboxWeight() {
        return controlRegionCheckboxWeight;
    }

    public void setControlRegionCheckboxWeight(float controlRegionCheckboxWeight) {
        this.controlRegionCheckboxWeight = controlRegionCheckboxWeight;
        invalidate();
    }


    public int getMargin(){
        return margin;
    }

    public void setMargin(int margin){
        this.margin = margin;
        invalidate();
    }


    public interface OnCheckedChangeListener {
        void onCheckChanged(boolean checked);
    }

    public interface OnItemLongPressedListener {
        void onItemLongPressed(ItemEvent event);
    }

    public interface OnItemClickListener {
        void onItemClick(ItemEvent event);
    }


    //Recognize touch motions and respond to a specific motion
    private static class OnTouchGestureListener implements GestureDetector.OnGestureListener {

        private final ControlLayout controlLayout;
        private final Paint highlight = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final ItemEvent itemEvent = new ItemEvent();

        public OnTouchGestureListener(ControlLayout controlLayout) {

            this.controlLayout = controlLayout;




            int currentColor = controlLayout.paint.getColor();
            int red = Color.red(currentColor);
            int green = Color.green(currentColor);
            int blue = Color.blue(currentColor);

            red = red > 100 ? red - 50: red + 50;
            green = green > 100 ? green -50 : green + 50;
            blue = blue > 100 ? blue - 50 : blue + 50;

            highlight.setStyle(controlLayout.getPaint().getStyle());
            highlight.setColor(Color.argb(255 , red ,green ,blue));
            highlight.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , 1 , DISPLAY_METRICS));
            highlight.setStrokeJoin(Paint.Join.BEVEL);
        }

        @Override
        public boolean onDown(MotionEvent e) {

            final float x = e.getX();
            final float y = e.getY();


            if (controlLayout.iconRegion.isPointInside(x, y) || controlLayout.textRegion.isPointInside(x, y)) {

                if (controlLayout.iconRegion.isPointInside(x, y)) {
                    itemEvent.item = ItemEvent.ICON;
                    controlLayout.iconRegion.setPaint(highlight);
                } else {
                    itemEvent.item = ItemEvent.TEXT;
                    controlLayout.textRegion.setPaint(highlight);
                }

                controlLayout.invalidate();

                return true;
            } else if (controlLayout.checkboxRegion.isPointInside(x, y)) {

                controlLayout.checked = !controlLayout.checked;
                controlLayout.checkboxRegion.setCheckIcon(controlLayout.checked ? controlLayout.checkedIconSource : controlLayout.uncheckedIconSource);

                if (controlLayout.checkedChangeListener != null)
                    controlLayout.checkedChangeListener.onCheckChanged(controlLayout.checked);

                controlLayout.invalidate();
                return true;
            }


            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            if (!(itemEvent.getItem() == ItemEvent.NONE)) {

                switch (itemEvent.getItem()) {
                    case ItemEvent.ICON:
                        controlLayout.iconRegion.setPaint(controlLayout.paint);
                        break;
                    case ItemEvent.TEXT:
                        controlLayout.textRegion.setPaint(controlLayout.paint);
                        break;
                }

                if (controlLayout.itemClickListener != null)
                    controlLayout.itemClickListener.onItemClick(itemEvent);

                itemEvent.item = ItemEvent.NONE;
                controlLayout.invalidate();

                return true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            controlLayout.requestDisallowInterceptTouchEvent(false);


            switch (itemEvent.getItem()) {
                case ItemEvent.ICON:
                    controlLayout.iconRegion.setPaint(controlLayout.paint);
                    break;
                case ItemEvent.TEXT:
                    controlLayout.textRegion.setPaint(controlLayout.paint);
                    break;
                default:
                    return false;
            }

            itemEvent.item = ItemEvent.NONE;
            controlLayout.invalidate();

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {


            try {

                switch (itemEvent.getItem()) {
                    case ItemEvent.ICON:
                        controlLayout.iconRegion.setPaint(controlLayout.paint);
                        break;
                    case ItemEvent.TEXT:
                        controlLayout.textRegion.setPaint(controlLayout.paint);
                        break;
                }

                if (controlLayout.itemLongPressedListener != null)
                    controlLayout.itemLongPressedListener.onItemLongPressed(itemEvent);



                itemEvent.item = ItemEvent.NONE;
                Thread.sleep(300);

                controlLayout.invalidate();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    public static class LayoutParams extends MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
