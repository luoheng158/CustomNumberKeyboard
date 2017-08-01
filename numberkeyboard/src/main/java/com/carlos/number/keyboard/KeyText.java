package com.carlos.number.keyboard;

import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by carlos on 25/07/2017.
 */

public class KeyText extends Key {

    public static final int TEXT_NORMAL = 0;
    public static final int TEXT_CLEAR = 10;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TEXT_NORMAL, TEXT_CLEAR})
    public @interface TextType {
    }

    @TextType
    private int mTextType;

    private String mKeyText;

    private int mKeyTextSize;

    @ColorRes
    private int mKeyTextColor;

    public KeyText(@TextType int textType) {
        super(Key.TYPE_TEXT);
        mTextType = textType;
    }

    @TextType
    public int getTextType() {
        return mTextType;
    }

    public String getKeyText() {
        return mKeyText;
    }

    public void setKeyText(String keyText) {
        mKeyText = keyText;
    }

    public int getKeyTextSize() {
        return mKeyTextSize;
    }

    public void setKeyTextSize(int keyTextSize) {
        mKeyTextSize = keyTextSize;
    }

    @ColorRes
    public int getKeyTextColor() {
        return mKeyTextColor;
    }

    public void setKeyTextColor(@ColorRes int keyTextColor) {
        mKeyTextColor = keyTextColor;
    }
}
