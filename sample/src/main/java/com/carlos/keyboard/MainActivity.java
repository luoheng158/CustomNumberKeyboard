package com.carlos.keyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.carlos.number.keyboard.CurrencyEditText;
import com.carlos.number.keyboard.KeyBoardLayout;
import com.carlos.number.keyboard.NumberDecimalEditText;

public class MainActivity extends AppCompatActivity {

    private CurrencyEditText mCurrencyTwoDecimalDigitsView;
    private CurrencyEditText mCurrencyNoneDecimalDigitsView;

    private NumberDecimalEditText mNumberDecimalEditText;
    private NumberDecimalEditText mPhoneNumberEditText;
    private KeyBoardLayout mKeyBoardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrencyTwoDecimalDigitsView = (CurrencyEditText) findViewById(R.id.et_currency1);
        mCurrencyTwoDecimalDigitsView.requestFocus();
        mCurrencyTwoDecimalDigitsView.setFocusableInTouchMode(true);
        mCurrencyTwoDecimalDigitsView.setOnFocusChangeListener(mNumberWithDecimalsKeyboard);

        mCurrencyNoneDecimalDigitsView = (CurrencyEditText) findViewById(R.id.et_currency2);
        mCurrencyNoneDecimalDigitsView.setOnFocusChangeListener(mNumberWithDecimalsKeyboard);

        mNumberDecimalEditText = (NumberDecimalEditText) findViewById(R.id.et_number);
        mNumberDecimalEditText.setOnFocusChangeListener(mNumberWithDecimalsKeyboard);

        mPhoneNumberEditText = (NumberDecimalEditText) findViewById(R.id.et_phone_number);
        mPhoneNumberEditText.setOnFocusChangeListener(mNumberWithClearKeyboard);

        mKeyBoardLayout = (KeyBoardLayout) findViewById(R.id.keyboard_layout);
        mKeyBoardLayout.addKeyBoardCallback(mCurrencyTwoDecimalDigitsView);
        mKeyBoardLayout.addKeyBoardCallback(mCurrencyNoneDecimalDigitsView);
        mKeyBoardLayout.addKeyBoardCallback(mNumberDecimalEditText);
        mKeyBoardLayout.addKeyBoardCallback(mPhoneNumberEditText);

        mKeyBoardLayout.showKeyBoard();
    }

    private View.OnFocusChangeListener mNumberWithDecimalsKeyboard =  new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mKeyBoardLayout.setKeyBoardType(KeyBoardLayout.TYPE_KEY_BOARD_NUMBER_WITH_DECIMALS);
                mKeyBoardLayout.showKeyBoard();
            }
        }
    };

    private View.OnFocusChangeListener mNumberWithClearKeyboard =  new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mKeyBoardLayout.setKeyBoardType(KeyBoardLayout.TYPE_KEY_BOARD_NUMBER_WITH_CLEAR);
                mKeyBoardLayout.showKeyBoard();
            }
        }
    };
}
