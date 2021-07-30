package com.arteriatech.emami.expense;

import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.msecsales.R;

public class ExpenseEntryActivity extends AppCompatActivity {

    private RadioButton rbDaily;
    private Bundle bundle;
    private boolean isDailyFragment=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_expense_entry));
        rbDaily = (RadioButton) findViewById(R.id.rb_expense_daily);
        rbDaily.setChecked(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(ExpenseEntryActivity.this)) {
            openFragment(new ExpenseDailyFragment1(), 1);
            isDailyFragment = true;
        }
    }

    /*radio button click event*/
    public void onRadioButtonClicked(View view) {
        boolean btChecked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb_expense_daily:
                if (btChecked) {
                    ActionBarView.initActionBarView(this, true, getString(R.string.radio_expense_daily));
                    openFragment(new ExpenseDailyFragment1(), 1);
                    isDailyFragment=true;
                }
                break;
            case R.id.rb_expense_monthly:
                if (btChecked) {
                    openFragment(new ExpenseMonthlyFragment1(), 2);
                    isDailyFragment=false;
                }
                break;
        }
    }

    /*open fragment*/
    private void openFragment(Fragment fragment, int type) {
        bundle = new Bundle();
        if (type == 1) {
            bundle.putString(Constants.ExpenseFreq, Constants.ExpenseDaily);
        } else {
            ActionBarView.initActionBarView(this, true, getString(R.string.radio_expense_monthly));
            bundle.putString(Constants.ExpenseFreq, Constants.ExpenseMonthly);
        }
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        KeyboardView keyboardView =null;
        if(isDailyFragment){
            keyboardView = ExpenseDailyFragment1.keyboardView;
        }else {
            keyboardView = ExpenseMonthlyFragment1.keyboardView;
        }
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            Constants.dialogBoxWithButton(ExpenseEntryActivity.this, "", "Do you want to exit from Expense entry?", "Yes", "No", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    if (clickedStatus) {
                        finish();
                    }
                }
            });
        }
    }
}
