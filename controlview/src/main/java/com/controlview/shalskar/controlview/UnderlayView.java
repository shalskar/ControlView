package com.controlview.shalskar.controlview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by RachelTeTau on 5/05/17.
 */
public class UnderlayView extends View {

    private int currentPosition = 0;

    private int baseColour;
    private int accentColour;

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

        this.basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.accentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.basePaint.setColor(this.baseColour);
        this.accentPaint.setColor(this.accentColour);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (this.sectionWidths.length > 0) {
            int leftPosition = 0;
            for (int i = 0; i < this.sectionWidths.length; i++) {
                int sectionWidth = this.sectionWidths[i];
                Rect rect = new Rect(leftPosition, 0, leftPosition + sectionWidth, getHeight());
                leftPosition += sectionWidth;

                if (i == this.currentPosition)
                    canvas.drawRect(rect, this.accentPaint);
                else
                    canvas.drawRect(rect, this.basePaint);

            }
        }
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

    public int getCurrentPosition() {
        return currentPosition;
    }
}
