package com.carlos.number.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
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

    private static final String KEYCODE_0 = "0";
    private static final String KEYCODE_1 = "1";
    private static final String KEYCODE_2 = "2";
    private static final String KEYCODE_3 = "3";
    private static final String KEYCODE_4 = "4";
    private static final String KEYCODE_5 = "5";
    private static final String KEYCODE_6 = "6";
    private static final String KEYCODE_7 = "7";
    private static final String KEYCODE_8 = "8";
    private static final String KEYCODE_9 = "9";

    private static final InputFilter[] NO_FILTERS = new InputFilter[0];

    private static final String MATCHER_PATTERN_PLACE_HOLDER = "^\\d*(\\.\\d{0,%s}){0,%s}$";

    private static final String MATCHER_PATTERN_LIMIT_LEN_PLACE_HOLDER = "^\\d{0,%s}(\\.\\d{0,%s}){0,%s}$";

    public static final String STR_DOT = ".";

    private Map<String, Pattern> mNoLimitLengthPatternCache;

    private Map<String, Pattern> mLimitLengthPatternCache;

    private StringBuffer mRawStringBuffer = new StringBuffer();

    private int mDecimalDigits;

    private int mInputMaxIntegers;

    private boolean mMayChangedByClipboard;

    private boolean mDisableSysKeyboard;

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
        mDisableSysKeyboard = true;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberDecimalEditText);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.NumberDecimalEditText_decimal_digits) {
                setDecimalDigits(a.getInt(attr, 0));
            } else if (attr == R.styleable.NumberDecimalEditText_max_input_integers) {
                setInputMaxIntegers(a.getInt(attr, 10));
            } else if (attr == R.styleable.NumberDecimalEditText_disable_sys_keyboard) {
                mDisableSysKeyboard = a.getBoolean(attr, true);
            }
        }
        // recycle.
        a.recycle();
        if (mDisableSysKeyboard) {
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
        }

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
        } catch (IndexOutOfBoundsException e) {
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
        } catch (IndexOutOfBoundsException e) {
            clearRawText();
        }
    }

    private void updateText(int rawSelection) {
        mMayChangedByClipboard = false;
        String rawStr = mRawStringBuffer.toString();
        String formatStr = formatDisplayText(rawStr);
        Editable editable = getText();
        InputFilter[] inputFilter = editable.getFilters();
        // clear filter.
        editable.setFilters(NO_FILTERS);
        editable.clear();
        editable.append(formatStr);
        // restore.
        editable.setFilters(inputFilter);
        int selection = resolveDisplaySelectionFormRawSection(rawSelection);
        if (selection > editable.length()) {
            selection = editable.length();
        }
        setSelection(selection);
        mMayChangedByClipboard = true;
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

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (mDisableSysKeyboard) {
            return super.onCreateInputConnection(outAttrs);
        }
        return new InputConnectionWrapper(super.onCreateInputConnection(outAttrs), true) {
            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                if (text == null) {
                    return false;
                }
                if (STR_DOT.equals(text.toString())) {
                    onTextInput(STR_DOT);
                }
                return false;
            }

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
                if (beforeLength == 1 && afterLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }

            @Override
            public boolean sendKeyEvent(KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_UP) {
                    return false;
                }
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_0:
                        onTextInput(KEYCODE_0);
                        break;
                    case KeyEvent.KEYCODE_1:
                        onTextInput(KEYCODE_1);
                        break;
                    case KeyEvent.KEYCODE_2:
                        onTextInput(KEYCODE_2);
                        break;
                    case KeyEvent.KEYCODE_3:
                        onTextInput(KEYCODE_3);
                        break;
                    case KeyEvent.KEYCODE_4:
                        onTextInput(KEYCODE_4);
                        break;
                    case KeyEvent.KEYCODE_5:
                        onTextInput(KEYCODE_5);
                        break;
                    case KeyEvent.KEYCODE_6:
                        onTextInput(KEYCODE_6);
                        break;
                    case KeyEvent.KEYCODE_7:
                        onTextInput(KEYCODE_7);
                        break;
                    case KeyEvent.KEYCODE_8:
                        onTextInput(KEYCODE_8);
                        break;
                    case KeyEvent.KEYCODE_9:
                        onTextInput(KEYCODE_9);
                        break;
                    case KeyEvent.KEYCODE_DEL:
                        onTextDelClicked();
                        break;
                }
                return false;
            }
        };
    }
}
