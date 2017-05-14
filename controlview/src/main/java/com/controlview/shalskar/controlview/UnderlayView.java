package com.controlview.shalskar.controlview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class UnderlayView extends View {

    private int currentPosition = 0;

    private int baseColour;

    private int accentColour;

    private float outlineStrokeWidth;

    private boolean drawOutline;

    @NonNull
    private Paint basePaint;

    @NonNull
    private Paint accentPaint;

    private int[] sectionWidths;

    public UnderlayView(@NonNull Context context) {
        super(context);
        initialise();
    }

    public UnderlayView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public UnderlayView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    private void initialise() {
        this.baseColour = getResources().getColor(android.R.color.white);
        this.accentColour = getResources().getColor(android.R.color.darker_gray);
        this.outlineStrokeWidth = getResources().getDimension(R.dimen.underlayview_stroke_width);

        this.basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.accentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.basePaint.setColor(this.baseColour);
        this.accentPaint.setColor(this.accentColour);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawSections(canvas);
        if (this.drawOutline) drawOutline(canvas);
    }

    private void drawSections(@NonNull Canvas canvas) {
        if (this.sectionWidths.length > 0) {
            int leftPosition = 0;
            for (int i = 0; i < this.sectionWidths.length; i++) {
                drawSection(canvas, leftPosition, i);
                leftPosition += this.sectionWidths[i];
            }
        }
    }

    private void drawSection(@NonNull Canvas canvas, int leftPosition, int position) {
        int sectionWidth = this.sectionWidths[position];
        Rect rect = new Rect(leftPosition, 0, leftPosition + sectionWidth, getHeight());

        if (position == this.currentPosition)
            canvas.drawRect(rect, this.accentPaint);
        else
            canvas.drawRect(rect, this.basePaint);
    }

    private void drawOutline(@NonNull Canvas canvas) {
        this.accentPaint.setStyle(Paint.Style.STROKE);
        this.accentPaint.setStrokeWidth(this.outlineStrokeWidth);
        canvas.drawRect(0, 0, getWidth(), getHeight(), this.accentPaint);
        this.accentPaint.setStyle(Paint.Style.FILL);
    }

    public void setBaseColour(int baseColour) {
        this.baseColour = baseColour;
        this.basePaint.setColor(this.baseColour);
        invalidate();
    }

    public void setAccentColour(int accentColour) {
        this.accentColour = accentColour;
        this.accentPaint.setColor(this.accentColour);
        invalidate();
    }

    public void setSectionWidths(int[] sectionWidths) {
        this.sectionWidths = sectionWidths;
        invalidate();
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        invalidate();
    }

    public void setDrawOutline(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
