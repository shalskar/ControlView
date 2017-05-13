package com.controlview.shalskar.controlview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ControlView extends CardView {

    /**
     * State constants
     **/

    private static final String EXTRA_SUPER_STATE = "super_state";
    private static final String EXTRA_CONTROL_OPTIONS = "control_options";
    private static final String EXTRA_CURRENT_POSITION = "current_position";

    /**
     * View constants
     */

    private static final float BASE_ELEVATION = 2;
    private static final float RAISED_ELEVATION = 8;

    /**
     * Views
     **/

    private LinearLayout controlOptionsLinearLayout;

    private UnderlayView foregroundView;

    private UnderlayView backgroundView;

    private List<TextView> controlOptionTextViews = new ArrayList<>();

    /**
     * Attributes
     **/

    private boolean hasBorder;

    private boolean isRaised;

    private int controlOptionTextViewPadding;

    private int selectedTextColour;

    private int unselectedTextColour;

    private int baseColour;

    private int accentColour;

    /**
     * State
     **/

    @Nullable
    private ArrayList<String> controlOptions;

    private int selectedControlOptionPosition;

    /**
     * Other fields
     **/

    @Nullable
    private OnControlOptionSelectedListener onControlOptionSelectedListener;

    public ControlView(@NonNull Context context) {
        super(context);
        inflate(context);
    }

    public ControlView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
        resolveAttributes(attrs);
        initialise();
    }

    public ControlView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context);
        resolveAttributes(attrs);
        initialise();
    }

    private void inflate(@NonNull Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.controlview, this);
    }

    private void resolveAttributes(@NonNull AttributeSet attributesSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributesSet, R.styleable.ControlView, 0, R.style.ControlView);
        try {
            this.selectedTextColour = typedArray.getColor(R.styleable.ControlView_selectedTextColor, getResources().getColor(android.R.color.white));
            this.unselectedTextColour = typedArray.getColor(R.styleable.ControlView_unselectedTextColor, getResources().getColor(android.R.color.black));
            this.baseColour = typedArray.getColor(R.styleable.ControlView_baseColor, getResources().getColor(android.R.color.white));
            this.accentColour = typedArray.getColor(R.styleable.ControlView_accentColor, getResources().getColor(android.R.color.black));
            this.isRaised = typedArray.getBoolean(R.styleable.ControlView_isRaised, false);
            this.hasBorder = typedArray.getBoolean(R.styleable.ControlView_hasBorder, false);
        } finally {
            typedArray.recycle();
        }
    }

    private void initialise() {
        float elevation = this.isRaised ? BASE_ELEVATION : 0;
        setCardElevation(elevation);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initialiseViews();
        initialiseDimensions();
    }

    private void initialiseViews() {
        this.controlOptionsLinearLayout = (LinearLayout) findViewById(R.id.control_options_linearlayout);

        this.foregroundView = (UnderlayView) findViewById(R.id.foreground_underlayview);
        this.backgroundView = (UnderlayView) findViewById(R.id.background_underlayview);
        this.foregroundView.setDrawOutline(this.hasBorder);
        this.backgroundView.setDrawOutline(this.hasBorder);
    }

    private void initialiseDimensions() {
        this.controlOptionTextViewPadding = (int) getResources().getDimension(R.dimen.control_option_textview_padding);
    }

    public void setControlOptions(@NonNull ArrayList<String> controlOptions) {
        this.controlOptions = controlOptions;
        addControlOptionsViews();
        this.foregroundView.setCurrentPosition(0);
    }

    private void addControlOptionsViews() {
        this.controlOptionsLinearLayout.removeAllViews();
        for (int i = 0; i < this.controlOptions.size(); i++) {
            String controlOption = this.controlOptions.get(i);
            TextView controlOptionTextView = createControlOptionTextView(controlOption, i == selectedControlOptionPosition);
            this.controlOptionsLinearLayout.addView(controlOptionTextView);
            this.controlOptionTextViews.add(controlOptionTextView);
        }

        this.controlOptionsLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateDimensions();
                ViewUtil.removeOnGlobalLayoutListener(controlOptionsLinearLayout, this);
            }
        });
    }

    @NonNull
    private TextView createControlOptionTextView(@NonNull String controlOption, boolean selected) {
        TextView controlOptionTextView = new TextView(getContext());
        controlOptionTextView.setText(controlOption.toUpperCase());
        controlOptionTextView.setTextColor(selected ? this.selectedTextColour : this.unselectedTextColour);
        controlOptionTextView.setPadding(this.controlOptionTextViewPadding, this.controlOptionTextViewPadding
                , this.controlOptionTextViewPadding, this.controlOptionTextViewPadding);
        return controlOptionTextView;
    }

    private void updateDimensions() {
        getLayoutParams().width = this.controlOptionsLinearLayout.getWidth();
        getLayoutParams().height = this.controlOptionsLinearLayout.getHeight();

        int[] sectionWidths = new int[this.controlOptionsLinearLayout.getChildCount()];
        for (int i = 0; i < sectionWidths.length; i++) {
            sectionWidths[i] = this.controlOptionsLinearLayout.getChildAt(i).getWidth();
        }
        this.foregroundView.setSectionWidths(sectionWidths);
        this.backgroundView.setSectionWidths(sectionWidths);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (!isEnabled()) return super.dispatchTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (shouldAnimateElevation())
                    animateElevationUp();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (shouldAnimateElevation())
                    animateElevationDown();
                break;
            case MotionEvent.ACTION_UP:
                onClick(event);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void onClick(@NonNull MotionEvent motionEvent) {
        this.selectedControlOptionPosition = findClickPosition(motionEvent);
        animateToPosition(motionEvent, this.selectedControlOptionPosition);
        updateTextViewColours();

        if (this.onControlOptionSelectedListener != null)
            sendClickEvent();

        if (shouldAnimateElevation())
            animateElevationDown();
    }

    private void sendClickEvent() {
        this.onControlOptionSelectedListener.onControlOptionSelected(this.selectedControlOptionPosition,
                this.controlOptions.get(this.selectedControlOptionPosition));
    }

    private boolean shouldAnimateElevation() {
        return isLollipop() && this.isRaised;
    }

    private void animateToPosition(@Nullable MotionEvent motionEvent, int position) {
        this.backgroundView.setCurrentPosition(this.foregroundView.getCurrentPosition());
        this.foregroundView.setCurrentPosition(position);

        if (isLollipop() && motionEvent != null)
            circularRevealForegroundView((int) motionEvent.getX(), (int) motionEvent.getY());
        else
            fadeInForegroundView();

        this.backgroundView.setAlpha(1);
        this.backgroundView.animate().alpha(0).start();
    }

    private void circularRevealForegroundView(int x, int y) {
        int circularRevealEndRadius = Math.max(getWidth(), getHeight());
        ViewAnimationUtils.createCircularReveal(this.foregroundView, x, y, 0, circularRevealEndRadius).start();
    }

    private void fadeInForegroundView() {
        this.foregroundView.setAlpha(0);
        this.foregroundView.animate()
                .alpha(1)
                .start();
    }

    private void updateTextViewColours() {
        for (int i = 0; i < this.controlOptionTextViews.size(); i++) {
            TextView controlOptionTextView = this.controlOptionTextViews.get(i);
            boolean isSelected = i == this.selectedControlOptionPosition;
            controlOptionTextView.setTextColor(isSelected ? this.selectedTextColour : this.unselectedTextColour);
        }
    }

    private void animateElevationUp() {
        animate()
                .z(RAISED_ELEVATION)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void animateElevationDown() {
        animate()
                .z(BASE_ELEVATION)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private int findClickPosition(@NonNull MotionEvent motionEvent) {
        int leftPosition = 0;
        for (int i = 0; i < this.controlOptionsLinearLayout.getChildCount(); i++) {
            int sectionWidth = this.controlOptionsLinearLayout.getChildAt(i).getWidth();
            if (motionEvent.getX() >= leftPosition && motionEvent.getX() < leftPosition + sectionWidth)
                return i;
            leftPosition += sectionWidth;
        }
        return -1;
    }

    private boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public void setOnControlOptionSelectedListener(@Nullable final OnControlOptionSelectedListener onControlOptionSelectedListener) {
        this.onControlOptionSelectedListener = onControlOptionSelectedListener;
    }

    public void setBaseColour(@ColorRes int baseColour) {
        this.baseColour = getResources().getColor(baseColour);
        this.foregroundView.setBaseColour(this.baseColour);
        this.backgroundView.setBaseColour(this.baseColour);
    }

    public void setAccentColour(int accentColour) {
        this.accentColour = getResources().getColor(accentColour);
        this.foregroundView.setAccentColour(this.accentColour);
        this.backgroundView.setAccentColour(this.accentColour);
    }

    public void setSelectedControlOptionPosition(int selectedControlOptionPosition, boolean animate) {
        this.selectedControlOptionPosition = selectedControlOptionPosition;

        if (animate)
            animateToPosition(null, selectedControlOptionPosition);
        else
            this.foregroundView.setCurrentPosition(selectedControlOptionPosition);

        updateTextViewColours();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle customStateBundle = new Bundle();
        customStateBundle.putParcelable(EXTRA_SUPER_STATE, superState);
        customStateBundle.putStringArrayList(EXTRA_CONTROL_OPTIONS, this.controlOptions);
        customStateBundle.putInt(EXTRA_CURRENT_POSITION, this.selectedControlOptionPosition);

        return customStateBundle;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Parcelable state) {
        Bundle customStateBundle = (Bundle) state;

        this.controlOptions = customStateBundle.getStringArrayList(EXTRA_CONTROL_OPTIONS);
        setSelectedControlOptionPosition(customStateBundle.getInt(EXTRA_CURRENT_POSITION), false);

        super.onRestoreInstanceState(customStateBundle.getParcelable(EXTRA_SUPER_STATE));
    }

    public interface OnControlOptionSelectedListener {

        void onControlOptionSelected(int position, @NonNull String controlOption);

    }
}
