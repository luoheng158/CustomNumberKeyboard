package com.carlos.number.keyboard;

import android.content.Context;
import android.util.AttributeSet;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;


/**
 * Created by carlos on 27/07/2017.
 */

public class CurrencyEditText extends NumberDecimalEditText {

    private NumberFormat mCurrencyFormatter;
    private String mGroupingSeparator;
    private String mDecimalSeparator;
    private StringBuffer mDisplayBuffer;
    private StringBuffer mTempDisplayBuffer;

    public CurrencyEditText(Context context) {
        super(context);
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mDisplayBuffer = new StringBuffer();
        mTempDisplayBuffer = new StringBuffer();
        setCurrencyFormatter(NumberFormat.getCurrencyInstance());
    }

    /**
     * set currency formatter.
     *
     * @param currencyFormatter
     */
    public void setCurrencyFormatter(NumberFormat currencyFormatter) {
        mCurrencyFormatter = currencyFormatter;
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
        mGroupingSeparator = String.valueOf(decimalFormatSymbols.getGroupingSeparator());
        mDecimalSeparator = String.valueOf(decimalFormatSymbols.getDecimalSeparator());
    }

    @Override
    protected int getRawSelection() {
        int selection = getSelectionStart();
        int subCount = 0;
        String displayStr = mDisplayBuffer.toString();
        for (int i = 0, len = Math.min(displayStr.length(), selection); i < len; i++) {
            if (mGroupingSeparator.equals(String.valueOf(displayStr.charAt(i)))) {
                subCount++;
            }
        }
        return selection - subCount;
    }

    @Override
    protected String formatDisplayText(String sb) {
        mDisplayBuffer.setLength(0);
        try {
            Double.parseDouble(sb);
        } catch (NumberFormatException e) {
            mDisplayBuffer.append(sb.replace(STR_DOT, mDecimalSeparator));
            return mDisplayBuffer.toString();
        }
        int dotIndex = sb.indexOf(STR_DOT);
        int decimals = 0;
        boolean dotAtTheEnd = false;
        if (dotIndex != -1) {
            decimals = sb.length() - dotIndex - 1;
            dotAtTheEnd = decimals == 0;
        }
        mCurrencyFormatter.setMaximumFractionDigits(decimals);
        mCurrencyFormatter.setMinimumFractionDigits(decimals);
        String displayText = mCurrencyFormatter.format(new BigDecimal(sb));
        if (displayText == null) {
            return mDisplayBuffer.toString();
        }
        mTempDisplayBuffer.setLength(0);
        mTempDisplayBuffer.append(displayText);
        for (int i = mTempDisplayBuffer.length() - 1; i >= 0; i--) {
            if (!String.valueOf(mTempDisplayBuffer.charAt(i)).equals(mGroupingSeparator)) {
                break;
            }
            mTempDisplayBuffer.deleteCharAt(i);
        }
        if (dotAtTheEnd && !mTempDisplayBuffer.toString().endsWith(mDecimalSeparator)) {
            mTempDisplayBuffer.append(mDecimalSeparator);
        }
        displayText = mTempDisplayBuffer.toString();
        sb = sb.replace(STR_DOT, mDecimalSeparator);

        int offsetCount = displayText.length() - sb.length();
        for (int i = sb.length() - 1; i >= 0; i--) {
            String rawStr = String.valueOf(sb.charAt(i));
            if (i + offsetCount < 0) {
                mDisplayBuffer.append(rawStr);
                continue;
            }
            String display = String.valueOf(displayText.charAt(i + offsetCount));
            if (display.equals(rawStr)) {
                mDisplayBuffer.append(display);
                continue;
            }
            if (display.equals(mGroupingSeparator)) {
                mDisplayBuffer.append(display);
            }
            offsetCount--;
            i++;
        }
        // keep display buffer have all sb buffer.
        return mDisplayBuffer.reverse().toString();
    }

    @Override
    protected int resolveDisplaySelectionFormRawSection(int rawSelection) {
        String displayStr = mDisplayBuffer.toString();
        int len = displayStr.length();
        for (int i = 0; i < len; i++) {
            if (rawSelection == 0) {
                return i;
            }
            if (!mGroupingSeparator.equals(String.valueOf(displayStr.charAt(i)))) {
                rawSelection--;
            }
        }
        return len;
    }

    public StringBuffer getDisplayBuffer() {
        return mDisplayBuffer;
    }

}
