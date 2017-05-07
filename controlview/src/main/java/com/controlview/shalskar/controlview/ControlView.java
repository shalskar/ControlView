package com.controlview.shalskar.controlview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
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

    private static final float SELECTED_ELEVATION_OFFSET = 6;

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

    private int controlOptionTextViewPadding;

    private int selectedTextColour;

    private int unselectedTextColour;

    private int baseColour;

    private int accentColour;

    private float elevation;

    /**
     * State
     **/

    @Nullable
    private List<String> controlOptions;

    private int selectedControlOptionPosition;

    /**
     * Other fields
     **/

    @Nullable
    private OnControlOptionSelectedListener onControlOptionSelectedListener;

    public ControlView(@NonNull Context context) {
        super(context);
        initialise(context);
    }

    public ControlView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
        resolveAttributes(attrs);
    }

    public ControlView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
        resolveAttributes(attrs);
    }

    private void initialise(@NonNull Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.controlview, this);
    }

    private void resolveAttributes(@NonNull AttributeSet attributesSet) {
        // todo check default styled res
        TypedArray typedArray = getContext().obtainStyledAttributes(attributesSet, R.styleable.ControlView, 0, 0);
        try {
            this.selectedTextColour = typedArray.getColor(R.styleable.ControlView_selectedTextColor, getResources().getColor(android.R.color.white));
            this.unselectedTextColour = typedArray.getColor(R.styleable.ControlView_unselectedTextColor, getResources().getColor(android.R.color.white));
            this.baseColour = typedArray.getColor(R.styleable.ControlView_baseColor, getResources().getColor(android.R.color.white));
            this.accentColour = typedArray.getColor(R.styleable.ControlView_accentColor, getResources().getColor(android.R.color.white));
            this.elevation = typedArray.getDimension(R.styleable.CardView_cardElevation, getResources().getDimension(R.dimen.cardview_default_elevation));
        } finally {
            typedArray.recycle();
        }
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
    }

    private void initialiseDimensions() {
        this.controlOptionTextViewPadding = (int) getResources().getDimension(R.dimen.control_option_textview_padding);
    }

    public void setControlOptions(@NonNull List<String> controlOptions) {
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
                controlOptionsLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isLollipop())
                    animateElevationUp();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isLollipop())
                    animateElevationDown();
                break;
            case MotionEvent.ACTION_UP:
                this.selectedControlOptionPosition = findClickPosition(event);
                animateToPosition(event, this.selectedControlOptionPosition);
                updateTextViewColours();

                if (this.onControlOptionSelectedListener != null) {
                    this.onControlOptionSelectedListener.onControlOptionSelected(this.selectedControlOptionPosition,
                            this.controlOptions.get(selectedControlOptionPosition));
                }
                if (isLollipop())
                    animateElevationDown();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void animateToPosition(@NonNull MotionEvent motionEvent, int position) {
        this.backgroundView.setCurrentPosition(this.foregroundView.getCurrentPosition());
        this.foregroundView.setCurrentPosition(position);

        if (isLollipop()) {
            int circularRevealEndRadius = Math.max(getWidth(), getHeight());
            ViewAnimationUtils.createCircularReveal(this.foregroundView,
                    (int) motionEvent.getX(),
                    (int) motionEvent.getY(),
                    0,
                    circularRevealEndRadius)
                    .start();
        } else {
            this.foregroundView.setAlpha(0);
            this.foregroundView.animate().alpha(1).start();
        }
    }

    private void updateTextViewColours() {
        for (int i = 0; i < this.controlOptionTextViews.size(); i++) {
            TextView controlOptionTextView = this.controlOptionTextViews.get(i);
            boolean isSelected = i == this.selectedControlOptionPosition;
            controlOptionTextView.setTextColor(isSelected ? this.selectedTextColour : this.unselectedTextColour);
        }
    }

    private boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    private void animateElevationUp() {
        animate()
                .z(this.elevation + SELECTED_ELEVATION_OFFSET)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void animateElevationDown() {
        animate()
                .z(this.elevation)
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

    public interface OnControlOptionSelectedListener {

        void onControlOptionSelected(int position, @NonNull String controlOption);

    }
}