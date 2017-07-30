package com.carlos.number.keyboard;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by carlos on 25/07/2017.
 */

public class Key {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 10;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_TEXT, TYPE_IMAGE})
    public @interface KeyType {
    }

    @KeyType
    private int mKeyType;

    @DrawableRes
    private int mKeyBackgroundRes;

    @DrawableRes
    private int mKeyItemBackgroundRes;

    public Key(@KeyType int keyType) {
        mKeyType = keyType;
    }

    @KeyType
    public int getKeyType() {
        return mKeyType;
    }

    public void setKeyBackgroundRes(@DrawableRes int keyBackgroundRes) {
        mKeyBackgroundRes = keyBackgroundRes;
    }

    @DrawableRes
    public int getKeyBackgroundRes() {
        return mKeyBackgroundRes;
    }

    public void setKeyItemBackgroundRes(@DrawableRes int keyItemBackgroundRes) {
        mKeyItemBackgroundRes = keyItemBackgroundRes;
    }

    @DrawableRes
    public int getKeyItemBackgroundRes() {
        return mKeyItemBackgroundRes;
    }

}
