package com.carlos.number.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by carlos on 04/08/2017.
 */

public class KeyItemLayout extends RelativeLayout {

    private LongPressStatusCallback mLongPressStatusCallback;

    private boolean mLongPressTriggered;

    private Key mKey;

    public KeyItemLayout(Context context) {
        super(context);
    }

    public KeyItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateKey(Key key) {
        mKey = key;
    }

    public void setLongPressStatusCallback(LongPressStatusCallback longPressStatusCallback) {
        mLongPressStatusCallback = longPressStatusCallback;
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongPressTriggered = true;
                if (mLongPressStatusCallback != null) {
                    mLongPressStatusCallback.onLongPressTriggered(mKey);
                }
                return true;
            }
        });
    }

    private boolean pointInView(float localX, float localY) {
        return localX >= 0 && localY >= 0 && localX < ((getRight() - getLeft())) &&
                localY < ((getBottom() - getTop()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLongPressTriggered && (!pointInView(event.getX(), event.getY())
                || event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP)) {
            mLongPressTriggered = false;
            if (mLongPressStatusCallback != null) {
                mLongPressStatusCallback.onLongPressReleased(mKey);
            }
        }
        return super.onTouchEvent(event);
    }

    public interface LongPressStatusCallback {

        void onLongPressTriggered(Key key);

        void onLongPressReleased(Key key);
    }

}
