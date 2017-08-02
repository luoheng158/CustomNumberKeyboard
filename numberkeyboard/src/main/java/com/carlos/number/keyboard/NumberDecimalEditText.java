package com.carlos.number.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by carlos on 26/07/2017.
 */

public class NumberDecimalEditText extends NoMenuEditText implements KeyBoardLayout.KeyBoardTextCallback,
        KeyBoardLayout.KeyBoardDelCallback, KeyBoardLayout.KeyBoardClearAllCallback {

    private static final String MATCHER_PATTERN_PLACE_HOLDER = "^\\d*(\\.\\d{0,%s}){0,%s}$";

    private static final String MATCHER_PATTERN_LIMIT_LEN_PLACE_HOLDER = "^\\d{0,%s}(\\.\\d{0,%s}){0,%s}$";

    public static final String STR_DOT = ".";

    private Map<String, Pattern> mNoLimitLengthPatternCache;

    private Map<String, Pattern> mLimitLengthPatternCache;

    private StringBuffer mRawStringBuffer = new StringBuffer();

    private int mDecimalDigits;

    private int mInputMaxIntegers;

    private boolean mMayChangedByClipboard;

    public NumberDecimalEditText(Context context) {
        super(context);
        init(context, null);
    }

    public NumberDecimalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumberDecimalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        // clear text. use setRawText.
        setText("");
        mNoLimitLengthPatternCache = new HashMap<>();
        mLimitLengthPatternCache = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            try {
                Method method = EditText.class.getMethod("setShowSoftInputOnFocus", new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(this, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberDecimalEditText);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.NumberDecimalEditText_decimal_digits) {
                setDecimalDigits(a.getInt(attr, 0));
            } else if (attr == R.styleable.NumberDecimalEditText_max_input_integers) {
                setInputMaxIntegers(a.getInt(attr, 10));
            }
        }
        // recycle.
        a.recycle();

        // check if the text may be changed by Clipboard.
        // although we want to disable Clipboard, but in some devices, may not effect.
        // so we simple to clear text, when the text is also changed by the other way.
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMayChangedByClipboard) {
                    String text = s.toString();
                    if (text == null || !text.equals(formatDisplayText(mRawStringBuffer.toString()))) {
                        // simple to clear.
                        clearRawText();
                    }
                }
            }
        });
        mMayChangedByClipboard = true;
        buildPatterns();
    }

    @Override
    public void onClearAllClicked() {
        clearRawText();
    }

    @Override
    public void onTextDelClicked() {
        int rawSelection = getRawSelection();
        if (rawSelection <= 0) {
            return;
        }
        try {
            mRawStringBuffer.deleteCharAt(rawSelection - 1);
            updateText(rawSelection - 1);
        } catch (StringIndexOutOfBoundsException e) {
            clearRawText();
        }
    }

    @Override
    public void onTextInput(String text) {
        handleInputText(text, true);
    }

    @Override
    public boolean handleKeyBoardEvent() {
        return hasFocus();
    }

    private void handleInputText(String text, boolean limitLength) {
        Map<String, Pattern> map = limitLength ? mLimitLengthPatternCache : mNoLimitLengthPatternCache;
        try {
            int rawSelection = getRawSelection();
            mRawStringBuffer.insert(rawSelection, text);
            String key = generateKey();
            if (map.containsKey(key) && !map.get(key).matcher(mRawStringBuffer).matches()) {
                mRawStringBuffer.deleteCharAt(rawSelection);
                return;
            }
            rawSelection++;
            updateText(rawSelection);
        } catch (StringIndexOutOfBoundsException e) {
            clearRawText();
        }
    }

    private void updateText(int rawSelection) {
        String rawStr = mRawStringBuffer.toString();
        String formatStr = formatDisplayText(rawStr);
        mMayChangedByClipboard = false;
        setText(formatStr);
        mMayChangedByClipboard = true;
        setSelection(resolveDisplaySelectionFormRawSection(rawSelection));
    }

    /**
     * set raw text.
     *
     * @param text
     */
    public void setRawText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            clearRawText();
            return;
        }
        int index = 0;
        String key = generateKey();
        mRawStringBuffer.setLength(0);
        for (int i = 0, len = text.length(); i < len; i++) {
            mRawStringBuffer.insert(index++, text.charAt(i));
            if (mNoLimitLengthPatternCache.containsKey(key)
                    && !mNoLimitLengthPatternCache.get(key).matcher(mRawStringBuffer).matches()) {
                mRawStringBuffer.deleteCharAt(--index);
                if (mDecimalDigits <= 0 && STR_DOT.equals(String.valueOf(text.charAt(i)))) {
                    break;
                }
            }
        }
        updateText(mRawStringBuffer.length());
    }

    /**
     * clear all text.
     */
    public void clearRawText() {
        mRawStringBuffer.setLength(0);
        updateText(0);
    }

    /**
     * set max input max integers.
     *
     * @param inputMaxIntegers
     */
    public void setInputMaxIntegers(int inputMaxIntegers) {
        mInputMaxIntegers = inputMaxIntegers;
    }

    /**
     * set decimalDigits.
     *
     * @param decimalDigits
     */
    public void setDecimalDigits(int decimalDigits) {
        mDecimalDigits = decimalDigits;
    }

    /**
     * build patterns.
     */
    public void buildPatterns() {
        Pattern p = Pattern.compile(String.format(MATCHER_PATTERN_PLACE_HOLDER, mDecimalDigits, mDecimalDigits > 0 ? 1 : 0));
        mNoLimitLengthPatternCache.put(generateKey(), p);
        p = Pattern.compile(String.format(MATCHER_PATTERN_LIMIT_LEN_PLACE_HOLDER, mInputMaxIntegers, mDecimalDigits, mDecimalDigits > 0 ? 1 : 0));
        mLimitLengthPatternCache.put(generateKey(), p);
    }

    private String generateKey() {
        return mDecimalDigits + "-" + mInputMaxIntegers;

    }

    /**
     * obtain decimalDigits.
     *
     * @return
     */
    public int getDecimalDigits() {
        return mDecimalDigits;
    }

    /**
     * api give sub class to format the text.
     *
     * @param sb
     * @return
     */
    protected String formatDisplayText(String sb) {
        return sb;
    }

    /**
     * obtain raw selection form format text.
     *
     * @return
     */
    protected int getRawSelection() {
        return getSelectionStart();
    }

    /**
     * resolve display section for raw section.
     *
     * @param rawSelection
     * @return
     */
    protected int resolveDisplaySelectionFormRawSection(int rawSelection) {
        return rawSelection;
    }

    /**
     * ontain raw StringBuffer.
     *
     * @return
     */
    public StringBuffer getRawStringBuffer() {
        return mRawStringBuffer;
    }
}
