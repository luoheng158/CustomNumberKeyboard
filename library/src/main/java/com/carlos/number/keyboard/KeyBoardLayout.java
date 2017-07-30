package com.carlos.number.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 25/07/2017.
 */

public class KeyBoardLayout extends RecyclerView {

    private static final String TAG = "KeyBoardLayout";

    private static final String DECIMALS = ".";
    private static final String NUMBER_1 = "1";
    private static final String NUMBER_2 = "2";
    private static final String NUMBER_3 = "3";
    private static final String NUMBER_4 = "4";
    private static final String NUMBER_5 = "5";
    private static final String NUMBER_6 = "6";
    private static final String NUMBER_7 = "7";
    private static final String NUMBER_8 = "8";
    private static final String NUMBER_9 = "9";
    private static final String NUMBER_0 = "0";

    public static final int TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS = 0;

    public static final int TYPE_KEY_BOARD_NUMBER_WITH_CLEAR = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS, TYPE_KEY_BOARD_NUMBER_WITH_CLEAR})
    public @interface KeyBoardType {
    }

    @DrawableRes
    private int mKeyLayoutBackgroundRes;

    @DrawableRes
    private int mKeyItemBackgroundRes;

    @ColorRes
    private int mKeyTextColor;

    private int mKeyTextSize;

    @DrawableRes
    private int mKeyDelRes;

    // key clear text.
    private CharSequence mKeyClearText;

    // draw line flag.
    private boolean mDrawLine;

    @DrawableRes
    private int mLineRes;

    private List<Key> mKeys = new ArrayList<>();

    // key adapter.
    private KeyBoardAdapter mKeyBoardAdapter;

    // vertical divider.
    private DividerItemDecoration mVerticalDivider;

    // horizontal divider.
    private DividerItemDecoration mHorizontalDivider;

    @KeyBoardType
    private int mKeyBoardType = TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS;

    private List<KeyBoardCallback> mKeyBoardCallbacks;

    public KeyBoardLayout(Context context) {
        super(context);
        init(context, null);
    }

    public KeyBoardLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public KeyBoardLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mKeyBoardCallbacks = new ArrayList<>();
        // gone init.
        setVisibility(GONE);
        // init attrs.
        mKeyTextColor = R.color.white;
        mKeyClearText = "";
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyBoardLayout);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.KeyBoardLayout_key_layout_background) {
                setKeyLayoutBackgroundRes(a.getResourceId(attr, 0));

            } else if (attr == R.styleable.KeyBoardLayout_key_item_background) {
                setKeyItemBackgroundRes(a.getResourceId(attr, 0));

            } else if (attr == R.styleable.KeyBoardLayout_key_text_color) {
                setKeyTextColor(a.getResourceId(attr, 0));

            } else if (attr == R.styleable.KeyBoardLayout_key_text_size) {
                setKeyTextSize(a.getDimensionPixelSize(attr, 20));

            } else if (attr == R.styleable.KeyBoardLayout_key_board_del_res) {
                setKeyDelRes(a.getResourceId(attr, 0));

            } else if (attr == R.styleable.KeyBoardLayout_key_board_clear_text) {
                setKeyClearText(a.getText(attr));

            } else if (attr == R.styleable.KeyBoardLayout_key_board_type) {
                setKeyBoardType(getSafetyKeyBoardType(a.getInt(attr, 0)));

            } else if (attr == R.styleable.KeyBoardLayout_key_board_with_line) {
                setDrawLine(a.getBoolean(attr, false));

            } else if (attr == R.styleable.KeyBoardLayout_key_board_line_res) {
                setLineRes(a.getResourceId(attr, 0));
            }
        }
        // recycle.
        a.recycle();
    }

    @KeyBoardType
    private int getSafetyKeyBoardType(int type) {
        switch (type) {
            case TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS:
                return TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS;
            case TYPE_KEY_BOARD_NUMBER_WITH_CLEAR:
                return TYPE_KEY_BOARD_NUMBER_WITH_CLEAR;

        }
        Log.w(TAG, "getSafetyKeyBoardType failed.");
        return TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS;
    }

    /**
     * build keys by mKeyBoardType.
     */
    private void generateDefaultKeys() {
        mKeys.clear();
        switch (mKeyBoardType) {
            case TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS: {
                String[] keys = {
                        NUMBER_1, NUMBER_2, NUMBER_3,
                        NUMBER_4, NUMBER_5, NUMBER_6,
                        NUMBER_7, NUMBER_8, NUMBER_9,
                        DECIMALS, NUMBER_0
                };
                for (int i = 0, len = keys.length; i < len; i++) {
                    KeyText key = new KeyText(KeyText.TEXT_NORMAL);
                    key.setKeyBackgroundRes(mKeyLayoutBackgroundRes);
                    key.setKeyItemBackgroundRes(mKeyItemBackgroundRes);
                    key.setKeyText(keys[i]);
                    key.setKeyTextColor(mKeyTextColor);
                    key.setKeyTextSize(mKeyTextSize);
                    mKeys.add(key);
                }

                KeyImage key = new KeyImage(KeyImage.IMAGE_DEL);
                key.setKeyBackgroundRes(mKeyLayoutBackgroundRes);
                key.setKeyItemBackgroundRes(mKeyItemBackgroundRes);
                key.setImageRes(mKeyDelRes);
                mKeys.add(key);
                break;
            }
            case TYPE_KEY_BOARD_NUMBER_WITH_CLEAR: {
                String[] keys = {
                        NUMBER_1, NUMBER_2, NUMBER_3,
                        NUMBER_4, NUMBER_5, NUMBER_6,
                        NUMBER_7, NUMBER_8, NUMBER_9,
                        mKeyClearText.toString(), NUMBER_0
                };
                int clearTextIndex = keys.length - 2;
                for (int i = 0, len = keys.length; i < len; i++) {
                    KeyText key = new KeyText(i == clearTextIndex ? KeyText.TEXT_CLEAR : KeyText.TEXT_NORMAL);
                    key.setKeyBackgroundRes(mKeyLayoutBackgroundRes);
                    key.setKeyItemBackgroundRes(mKeyItemBackgroundRes);
                    key.setKeyText(keys[i]);
                    key.setKeyTextColor(mKeyTextColor);
                    key.setKeyTextSize(mKeyTextSize);
                    mKeys.add(key);
                }

                KeyImage key = new KeyImage(KeyImage.IMAGE_DEL);
                key.setKeyBackgroundRes(mKeyLayoutBackgroundRes);
                key.setKeyItemBackgroundRes(mKeyItemBackgroundRes);
                key.setImageRes(mKeyDelRes);
                mKeys.add(key);
                break;
            }
        }
    }

    /**
     * set key layout bg res.
     *
     * @param keyLayoutBackgroundRes
     */
    public void setKeyLayoutBackgroundRes(@DrawableRes int keyLayoutBackgroundRes) {
        mKeyLayoutBackgroundRes = keyLayoutBackgroundRes;
    }

    /**
     * set key item bg res.
     *
     * @param keyItemBackgroundRes
     */
    public void setKeyItemBackgroundRes(@DrawableRes int keyItemBackgroundRes) {
        mKeyItemBackgroundRes = keyItemBackgroundRes;
    }

    /**
     * set ket text color.
     *
     * @param keyTextColor
     */
    public void setKeyTextColor(@ColorRes int keyTextColor) {
        mKeyTextColor = keyTextColor;
    }

    /**
     * set key txt
     *
     * @param keyTextSize
     */
    public void setKeyTextSize(int keyTextSize) {
        mKeyTextSize = keyTextSize;
    }

    /**
     * set ket del res, this is a special dispose.
     *
     * @param keyDelRes
     */
    public void setKeyDelRes(@DrawableRes int keyDelRes) {
        mKeyDelRes = keyDelRes;
    }

    /**
     * set key clear text, this is a special dispose.
     *
     * @param keyClearText
     */
    public void setKeyClearText(CharSequence keyClearText) {
        mKeyClearText = keyClearText;
    }

    /**
     * set draw line flag.
     *
     * @param drawLine true, draw.
     */
    public void setDrawLine(boolean drawLine) {
        mDrawLine = drawLine;
    }

    /**
     * set line res.
     *
     * @param lineRes drawable res.
     */
    public void setLineRes(@DrawableRes int lineRes) {
        mLineRes = lineRes;
    }

    /**
     * set keyboard type.
     *
     * @param keyBoardType
     */
    public void setKeyBoardType(@KeyBoardType int keyBoardType) {
        mKeyBoardType = keyBoardType;
    }

    /**
     * add key board text event callback.
     *
     * @param keyBoardCallback
     */
    public void addKeyBoardCallback(KeyBoardCallback keyBoardCallback) {
        if (keyBoardCallback != null) {
            mKeyBoardCallbacks.add(keyBoardCallback);
        }
    }

    /**
     * check if need draw line.
     */
    private void checkDrawLine() {
        // dynamic line divider.
        removeItemDecoration(mVerticalDivider);
        removeItemDecoration(mHorizontalDivider);
        setBackgroundResource(0);
        if (mDrawLine) {
            setBackgroundResource(mLineRes);
            if (mVerticalDivider == null) {
                mVerticalDivider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            }

            Bitmap verticalBp = BitmapUtil.drawableToBitmap((getResources().getDrawable(mLineRes)));
            mVerticalDivider.setDrawable(BitmapUtil.bitmapToDrawable(getResources(), BitmapUtil.bitmapScale(verticalBp, 0.5f)));
            addItemDecoration(mVerticalDivider);

            if (mHorizontalDivider == null) {
                mHorizontalDivider = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
            }
            Bitmap horizontalBp = BitmapUtil.drawableToBitmap((getResources().getDrawable(mLineRes)));
            mHorizontalDivider.setDrawable(BitmapUtil.bitmapToDrawable(getResources(), BitmapUtil.bitmapScale(horizontalBp, 0.5f)));
            addItemDecoration(mHorizontalDivider);
        }
    }

    /**
     * Api to add extra keys, should be called before showKeyBoard {@link #showKeyBoard()}
     *
     * @param key
     */
    public void addKey(Key key) {
        mKeys.add(key);
    }

    /**
     * show the keyboard finally.
     */
    public void showKeyBoard() {
        if (mKeyBoardAdapter == null) {
            mKeyBoardAdapter = new KeyBoardAdapter(getContext());
            mKeyBoardAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    handleInputKey(mKeyBoardAdapter.getItem(position));
                }
            });
            setLayoutManager(new GridLayoutManager(getContext(), 3));
            setAdapter(mKeyBoardAdapter);
            setVisibility(VISIBLE);
        }
        generateDefaultKeys();
        mKeyBoardAdapter.clear();
        mKeyBoardAdapter.addAll(mKeys);
        checkDrawLine();
    }

    /**
     * dispatch input keys.
     *
     * @param key key.
     */
    private void handleInputKey(Key key) {
        switch (key.getKeyType()) {
            case Key.TYPE_TEXT:
                handleKeyText((KeyText) key);
                break;
            case Key.TYPE_IMAGE:
                handleKeyImage((KeyImage) key);
                break;
            default:
                Log.w(TAG, "handleInputKey, type not handle." + key.getKeyType());
                break;
        }
    }

    /**
     * handle KeyImage type.
     *
     * @param keyImage data.
     */
    private void handleKeyImage(KeyImage keyImage) {
        switch (keyImage.getImageType()) {
            case KeyImage.IMAGE_DEL:
                if (dispatchTextDelClickedEvent()) {
                    return;
                }
                break;
            default:
                Log.w(TAG, "handleKeyImage, type not handle." + keyImage.getImageType());
                break;
        }
        Log.w(TAG, "handleKeyImage, callback not matched.");
    }

    /**
     * dispatch text del clicked event.
     *
     * @return
     */
    private boolean dispatchTextDelClickedEvent() {
        boolean handle = false;
        for (KeyBoardCallback keyBoardCallback : mKeyBoardCallbacks) {
            if (keyBoardCallback instanceof KeyBoardDelCallback && keyBoardCallback.handleKeyBoardEvent()) {
                ((KeyBoardDelCallback) keyBoardCallback).onTextDelClicked();
                handle = true;
            }
        }
        return handle;
    }

    /**
     * dispatch text input event.
     *
     * @param text
     * @return
     */
    private boolean dispatchTextInputEvent(String text) {
        boolean handle = false;
        for (KeyBoardCallback keyBoardCallback : mKeyBoardCallbacks) {
            if (keyBoardCallback instanceof KeyBoardTextCallback && keyBoardCallback.handleKeyBoardEvent()) {
                ((KeyBoardTextCallback) keyBoardCallback).onTextInput(text);
                handle = true;
            }
        }
        return handle;
    }

    /**
     * dispatch clear all clicked event.
     *
     * @return
     */
    private boolean dispatchClearAllClickedEvent() {
        boolean handle = false;
        for (KeyBoardCallback keyBoardCallback : mKeyBoardCallbacks) {
            if (keyBoardCallback instanceof KeyBoardClearAllCallback && keyBoardCallback.handleKeyBoardEvent()) {
                ((KeyBoardClearAllCallback) keyBoardCallback).onClearAllClicked();
                handle = true;
            }
        }
        return handle;
    }

    /**
     * handle KeyText type.
     *
     * @param keyText data.
     */
    private void handleKeyText(KeyText keyText) {
        switch (keyText.getTextType()) {
            case KeyText.TEXT_NORMAL:
                if (dispatchTextInputEvent(keyText.getKeyText())) {
                    return;
                }
                break;
            case KeyText.TEXT_CLEAR:
                if (dispatchClearAllClickedEvent()) {
                    return;
                }
                break;
            default:
                Log.w(TAG, "handleKeyText, type not handle." + keyText.getTextType());
                break;
        }
        Log.w(TAG, "handleKeyText, callback not matched.");
    }

    /**
     * remove the callback.
     *
     * @param callback
     */
    public void removeKeyBoardCallback(KeyBoardCallback callback) {
        mKeyBoardCallbacks.remove(callback);
    }

    /**
     * clear all key board callback.
     */
    public interface KeyBoardClearAllCallback extends KeyBoardCallback {
        void onClearAllClicked();
    }

    /**
     * del key board callback.
     */
    public interface KeyBoardDelCallback extends KeyBoardCallback {
        void onTextDelClicked();
    }

    /**
     * base key board callback.
     */
    public interface KeyBoardTextCallback extends KeyBoardCallback {
        void onTextInput(String text);
    }

    /**
     * base key board callback.
     */
    public interface KeyBoardCallback {
        boolean handleKeyBoardEvent();
    }

}
