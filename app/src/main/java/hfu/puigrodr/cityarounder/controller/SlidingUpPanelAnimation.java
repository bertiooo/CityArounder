package hfu.puigrodr.cityarounder.controller;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * see http://stackoverflow.com/questions/22616605/umano-android-slidinguppanel-animation
 */
public class SlidingUpPanelAnimation extends Animation {

    private SlidingUpPanelLayout mLayout;

    private float mTo;
    private float mFrom = 0;

    public SlidingUpPanelAnimation(SlidingUpPanelLayout layout, float to, int duration) {
        mLayout = layout;
        mTo = to;

        setDuration(duration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float dimension = (mTo - mFrom) * interpolatedTime + mFrom;

        mLayout.setPanelHeight((int) dimension);
        mLayout.requestLayout();
    }
}