package com.carlos.number.keyboard;

import android.content.Context;
import android.util.AttributeSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by carlos on 27/07/2017.
 */

public class CurrencyEditText extends NumberDecimalEditText {

    private NumberFormat mCurrencyFormatter;
    private String mGroupingSeparator;
    private StringBuffer mDisplayBuffer;

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
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(decimalFormatSymbols);
        setCurrencyFormatter(currencyFormatter);
    }

    /**
     * set currency formatter.
     * @param currencyFormatter
     */
    public void setCurrencyFormatter(NumberFormat currencyFormatter) {
        mCurrencyFormatter = currencyFormatter;
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
        mGroupingSeparator = String.valueOf(decimalFormatSymbols.getGroupingSeparator());
    }

    @Override
    protected int getRawSelection(CharSequence displayStr) {
        int selection = getSelectionStart();
        int subCount = 0;
        for (int i = 0, len = Math.min(displayStr.length(), selection); i < len; i++) {
            if (mGroupingSeparator.equals(String.valueOf(displayStr.charAt(i)))) {
                subCount++;
            }
        }
        return selection - subCount;
    }

    @Override
    protected CharSequence formatDisplayText(StringBuffer sb) {
        double number;
        try {
            number = Double.parseDouble(sb.toString());
        } catch (NumberFormatException e) {
            return sb;
        }
        mDisplayBuffer.setLength(0);
        CharSequence displayText = mCurrencyFormatter.format(number);
        if (displayText == null) {
            return mDisplayBuffer;
        }
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
        return mDisplayBuffer.reverse();
    }

    @Override
    protected int resolveDisplaySelectionFormRawSection(CharSequence displayStr, CharSequence rawStr, int rawSelection) {
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
