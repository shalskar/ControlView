package com.controlview.shalskar.controlview;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewUtil {

    public static void removeOnGlobalLayoutListener(@NonNull View view,
                                                    @NonNull ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT >= 16)
            view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        else
            view.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);

    }

}
