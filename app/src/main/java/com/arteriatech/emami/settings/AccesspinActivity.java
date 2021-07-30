package com.arteriatech.emami.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;

public class AccesspinActivity extends AppCompatActivity implements View.OnClickListener{

    EditText ed_Pinone, ed_Pintwo, ed_Pinthree, ed_Pinfour, ed_Confrimone, ed_Confrimtwo, ed_Confrimthree, ed_Confrimfour, ed_Oldone, ed_Oldtwo, ed_Oldthree, ed_Oldfour;
    TextView tv_Oldpin, tv_enterPin, tv_confirmPin;
    LinearLayout ll_Oldlay, ll_Pinlay, ll_Confirmlay, ll_Pinconfirm, ll_Oldconfirm;
    String pinOne, pinTwo, pinThree, pinFour, confrimOne, confirmTwo, confrimThree, confirmFour;
    String passwordOne, confirmPassword;
    Button btn_Confrim, btn_OldConfirm, bt_Clear, bt_Oldclear;
    private String mStrOldPin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarView.initActionBarView(this, true, "Enter Pin");
        setContentView(R.layout.activity_accesspin);
        tv_Oldpin = (TextView) findViewById(R.id.tv_oldpin);
        tv_enterPin = (TextView) findViewById(R.id.tv_enterpin);
        tv_confirmPin = (TextView) findViewById(R.id.tv_confirm);
        ed_Oldone = (EditText) findViewById(R.id.ed_oldone);
        ed_Oldtwo = (EditText) findViewById(R.id.ed_oldtwo);
        ed_Oldthree = (EditText) findViewById(R.id.ed_oldthree);
        ed_Oldfour = (EditText) findViewById(R.id.ed_oldfour);
        ll_Oldlay = (LinearLayout) findViewById(R.id.ll_oldpin);
        ll_Pinlay = (LinearLayout) findViewById(R.id.ll_pinlay);
        ll_Confirmlay = (LinearLayout) findViewById(R.id.ll_confirmlay);
        ed_Pinone = (EditText) findViewById(R.id.ed_pinone);
        ed_Pintwo = (EditText) findViewById(R.id.ed_pintwo);
        ed_Pinthree = (EditText) findViewById(R.id.ed_pinthree);
        ed_Pinfour = (EditText) findViewById(R.id.ed_pinfour);
        ed_Confrimone = (EditText) findViewById(R.id.ed_confrim_one);
        ed_Confrimtwo = (EditText) findViewById(R.id.ed_confrim_two);
        ed_Confrimthree = (EditText) findViewById(R.id.ed_confrim_three);
        ed_Confrimfour = (EditText) findViewById(R.id.ed_confrim_four);
        ll_Oldconfirm = (LinearLayout) findViewById(R.id.ll_oldconfirm);
        ll_Pinconfirm = (LinearLayout) findViewById(R.id.ll_pinconfirm);
        btn_Confrim = (Button) findViewById(R.id.btn_confirm);
        btn_OldConfirm = (Button) findViewById(R.id.btn_oldconfirm);
        bt_Clear = (Button) findViewById(R.id.bt_clear);
        bt_Oldclear = (Button) findViewById(R.id.bt_oldclear);
        if (!Constants.restartApp(AccesspinActivity.this)) {
        /*
        to check already access pin is configured
         */
            checkAccessPin();


            ed_Oldone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Oldone.getText().toString().length() == 1) {
                        ed_Oldtwo.requestFocus();

                    }
                }
            });
            ed_Oldtwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Oldtwo.getText().toString().length() == 1) {
                        ed_Oldthree.requestFocus();

                    }
                }
            });
            ed_Oldthree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Oldthree.getText().toString().length() == 1) {
                        ed_Oldfour.requestFocus();

                    }
                }
            });

            ed_Oldfour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Oldfour.getText().toString().length() == 1) {
                        ed_Oldfour.clearFocus();

                    }

                }
            });

            ed_Pinone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Pinone.getText().toString().length() == 1) {
                        ed_Pintwo.requestFocus();

                    }
                }
            });


            ed_Pintwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Pintwo.getText().toString().length() == 1) {

                        ed_Pinthree.requestFocus();
                    }
                }
            });

            ed_Pinthree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Pinthree.getText().toString().length() == 1) {

                        ed_Pinfour.requestFocus();
                    }
                }
            });

            ed_Pinfour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Pinfour.getText().toString().length() == 1) {

                        ed_Confrimone.requestFocus();
                    }
                }
            });

            ed_Confrimone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Confrimone.getText().toString().length() == 1) {

                        ed_Confrimtwo.requestFocus();
                    }
                }
            });

            ed_Confrimtwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Confrimtwo.getText().toString().length() == 1) {

                        ed_Confrimthree.requestFocus();
                    }
                }
            });

            ed_Confrimthree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Confrimthree.getText().toString().length() == 1) {

                        ed_Confrimfour.requestFocus();
                    }
                }
            });

            ed_Confrimfour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (ed_Confrimfour.getText().toString().length() == 1) {
                        ed_Confrimfour.clearFocus();
                    }
                }
            });


            bt_Clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_Pinone.setText("");
                    ed_Pintwo.setText("");
                    ed_Pinthree.setText("");
                    ed_Pinfour.setText("");

                    ed_Confrimone.setText("");
                    ed_Confrimtwo.setText("");
                    ed_Confrimthree.setText("");
                    ed_Confrimfour.setText("");

                }
            });
            bt_Oldclear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_Oldone.setText("");
                    ed_Oldtwo.setText("");
                    ed_Oldthree.setText("");
                    ed_Oldfour.setText("");
                }
            });
            btn_Confrim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pinOne = ed_Pinone.getText().toString();
                    pinTwo = ed_Pintwo.getText().toString();
                    pinThree = ed_Pinthree.getText().toString();
                    pinFour = ed_Pinfour.getText().toString();

                    passwordOne = pinOne + pinTwo + pinThree + pinFour;

                    confrimOne = ed_Confrimone.getText().toString();
                    confirmTwo = ed_Confrimtwo.getText().toString();
                    confrimThree = ed_Confrimthree.getText().toString();
                    confirmFour = ed_Confrimfour.getText().toString();
                    confirmPassword = confrimOne + confirmTwo + confrimThree + confirmFour;


                    if (TextUtils.isEmpty(pinOne) || TextUtils.isEmpty(pinTwo) || TextUtils.isEmpty(pinThree) || TextUtils.isEmpty(pinFour)) {
                        UtilConstants.showAlert(getString(R.string.plz_enter_all_values), AccesspinActivity.this);
                    } else if (TextUtils.isEmpty(confrimOne) || TextUtils.isEmpty(confirmTwo) || TextUtils.isEmpty(confrimThree) || TextUtils.isEmpty(confirmFour)) {
                        UtilConstants.showAlert(getString(R.string.plz_enter_all_values), AccesspinActivity.this);
                    } else {
                        SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences(AccesspinActivity.this);
                        // Toast.makeText(AccesspinActivity.this, "First:" + passwordOne + "==" + "Second:" + confirmPassword, Toast.LENGTH_LONG).show();
                        if ("yes".equalsIgnoreCase(sharedPreferencess.getString(Constants.QUICK_PIN_ACCESS, ""))) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(AccesspinActivity.this, R.style.MyTheme);
                            builder.setMessage(getString(R.string.do_u_want_change_access_pin))
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    if (!mStrOldPin.equalsIgnoreCase("")) {
                                                        if (mStrOldPin.equalsIgnoreCase(confirmPassword)) {
                                                            UtilConstants.showAlert(getString(R.string.enter_diffrent_pwd), AccesspinActivity.this);
                                                        } else {
                                                            savePin();
                                                        }
                                                    } else {
                                                        savePin();
                                                    }
                                                }
                                            })
                                    .setNegativeButton(R.string.cancel,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            builder.show();

                        } else {
                            savePin();
                        }
                    }


                }
            });

            btn_OldConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oldPinValidation();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_clear:
                clearPWD();
                break;
        }

    }

    private void clearPWD(){
        ed_Pinone.setText("");
        ed_Pintwo.setText("");
        ed_Pinthree.setText("");
        ed_Pinfour.setText("");

        ed_Confrimone.setText("");
        ed_Confrimtwo.setText("");
        ed_Confrimthree.setText("");
        ed_Confrimfour.setText("");
    }

    private void oldPinValidation() {
        SharedPreferences pinPreferences = PreferenceManager.getDefaultSharedPreferences(AccesspinActivity.this);
        String oldPin = pinPreferences.getString(Constants.QUICK_PIN, "");
        String oldPinOne = ed_Oldone.getText().toString();
        String oldPinTwo = ed_Oldtwo.getText().toString();
        String oldPinThree = ed_Oldthree.getText().toString();
        String oldPinFour = ed_Oldfour.getText().toString();
        String enteredPin = oldPinOne + oldPinTwo + oldPinThree + oldPinFour;
        if (oldPin.equalsIgnoreCase(enteredPin)) {

            tv_Oldpin.setVisibility(View.GONE);
            ll_Oldlay.setVisibility(View.GONE);
            ll_Pinconfirm.setVisibility(View.VISIBLE);
            tv_enterPin.setVisibility(View.VISIBLE);
            tv_confirmPin.setVisibility(View.VISIBLE);
            ll_Pinlay.setVisibility(View.VISIBLE);
            ll_Confirmlay.setVisibility(View.VISIBLE);
            ll_Oldconfirm.setVisibility(View.GONE);

        } else {
            Toast.makeText(AccesspinActivity.this, "Entered wrong pin", Toast.LENGTH_LONG).show();
            tv_Oldpin.setVisibility(View.VISIBLE);
            ll_Oldlay.setVisibility(View.VISIBLE);
            tv_enterPin.setVisibility(View.GONE);
            tv_confirmPin.setVisibility(View.GONE);
            ll_Pinlay.setVisibility(View.GONE);
            ll_Confirmlay.setVisibility(View.GONE);
            ll_Pinconfirm.setVisibility(View.GONE);
            ll_Oldconfirm.setVisibility(View.VISIBLE);
        }
    }

    private void checkAccessPin() {

        SharedPreferences pinPreferences = PreferenceManager.getDefaultSharedPreferences(AccesspinActivity.this);
        String oldPin = pinPreferences.getString(Constants.QUICK_PIN_ACCESS, "");

        if (!TextUtils.isEmpty(oldPin)) {
            mStrOldPin = pinPreferences.getString(Constants.QUICK_PIN, "");
            tv_Oldpin.setVisibility(View.VISIBLE);
            ll_Oldlay.setVisibility(View.VISIBLE);
            ll_Pinconfirm.setVisibility(View.GONE);
            tv_enterPin.setVisibility(View.GONE);
            tv_confirmPin.setVisibility(View.GONE);
            ll_Pinlay.setVisibility(View.GONE);
            ll_Confirmlay.setVisibility(View.GONE);
            ll_Oldconfirm.setVisibility(View.VISIBLE);

        } else {
            tv_Oldpin.setVisibility(View.GONE);
            ll_Oldlay.setVisibility(View.GONE);
            ll_Pinconfirm.setVisibility(View.VISIBLE);
            tv_enterPin.setVisibility(View.VISIBLE);
            tv_confirmPin.setVisibility(View.VISIBLE);
            ll_Pinlay.setVisibility(View.VISIBLE);
            ll_Confirmlay.setVisibility(View.VISIBLE);
            ll_Oldconfirm.setVisibility(View.GONE);
        }

    }

    private void savePin() {

        if (passwordOne.equalsIgnoreCase(confirmPassword)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AccesspinActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.QUICK_PIN, passwordOne);
            editor.putString(Constants.QUICK_PIN_ACCESS, "yes");
            editor.apply();
            //  Toast.makeText(AccesspinActivity.this, sharedPreferences.getString(Constants.QUICK_PIN, ""), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(AccesspinActivity.this, "Pin entered do not match", Toast.LENGTH_LONG).show();
        }
    }
}
