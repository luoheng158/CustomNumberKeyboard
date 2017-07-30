package com.carlos.number.keyboard;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by carlos on 25/07/2017.
 */

public class KeyImage extends Key {

    public static final int IMAGE_DEL = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE_DEL})
    public @interface ImageType {
    }

    @ImageType
    private int mImageType;

    @DrawableRes
    private int mImageRes;

    public KeyImage(@ImageType int imageType) {
        super(Key.TYPE_IMAGE);
        mImageType = imageType;
    }

    @ImageType
    public int getImageType() {
        return mImageType;
    }

    public void setImageRes(@DrawableRes int imageRes) {
        mImageRes = imageRes;
    }

    @DrawableRes
    public int getImageRes() {
        return mImageRes;
    }

}
