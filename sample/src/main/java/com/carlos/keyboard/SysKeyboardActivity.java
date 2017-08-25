package com.carlos.keyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carlos.number.keyboard.CurrencyEditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class SysKeyboardActivity extends AppCompatActivity {

    private CurrencyEditText mCurrencyTwoDecimalDigitsView;
    private CurrencyEditText mCurrencyNoneDecimalDigitsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys);
        mCurrencyTwoDecimalDigitsView = (CurrencyEditText) findViewById(R.id.et_currency1);
        mCurrencyTwoDecimalDigitsView.requestFocus();
        mCurrencyTwoDecimalDigitsView.setFocusableInTouchMode(true);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(decimalFormatSymbols);
        mCurrencyTwoDecimalDigitsView.setCurrencyFormatter(currencyFormatter);

        mCurrencyNoneDecimalDigitsView = (CurrencyEditText) findViewById(R.id.et_currency2);
        mCurrencyNoneDecimalDigitsView.setCurrencyFormatter(currencyFormatter);

    }

}
