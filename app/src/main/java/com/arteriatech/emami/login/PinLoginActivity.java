package com.arteriatech.emami.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;

public class PinLoginActivity extends AppCompatActivity {

    Button btn_Okay, btn_pinclear;
    EditText ed_Loginone, ed_Logintwo, ed_Loginthree, ed_Loginfour;
    String accessKey, enteredKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login);
        getAccessKey();
        ed_Loginone = (EditText) findViewById(R.id.ed_loginone);
        ed_Logintwo = (EditText) findViewById(R.id.ed_logintwo);
        ed_Loginthree = (EditText) findViewById(R.id.ed_loginthree);
        ed_Loginfour = (EditText) findViewById(R.id.ed_loginfour);


        btn_Okay = (Button) findViewById(R.id.btn_ok);
        btn_pinclear = (Button) findViewById(R.id.btn_pinclear);
        btn_pinclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ed_Loginfour.setText("");
                ed_Loginthree.setText("");
                ed_Logintwo.setText("");
                ed_Loginone.setText("");
                ed_Loginone.requestFocus();
            }
        });

        btn_Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinValidate();
            }
        });

        ed_Loginone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (ed_Loginone.getText().toString().length() == 0) {
                    ed_Loginone.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed_Loginone.getText().toString().length() == 0) {
                    ed_Loginone.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ed_Loginone.getText().toString().length() == 1) {
                    ed_Logintwo.requestFocus();

                }
            }
        });
        ed_Logintwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ed_Logintwo.getText().toString().length() == 1) {
                    ed_Loginthree.requestFocus();

                }
            }
        });
        ed_Loginthree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ed_Loginthree.getText().toString().length() == 1) {
                    ed_Loginfour.requestFocus();

                }
            }
        });

        ed_Loginfour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ed_Loginfour.getText().toString().length() == 1) {
                    ed_Loginfour.clearFocus();
//                    ed_Loginfour.setCursorVisible(false);
                }
            }
        });
    }

    private void pinValidate() {

        String pinOne = ed_Loginone.getText().toString();
        String pinTwo = ed_Logintwo.getText().toString();
        String pinThree = ed_Loginthree.getText().toString();
        String pinFour = ed_Loginfour.getText().toString();
        enteredKey = pinOne + pinTwo + pinThree + pinFour;
        if (enteredKey.equalsIgnoreCase(accessKey)) {
            Intent goToNextActivity = new Intent(this, MainMenu.class);
            //Navigating to Login Activity
            startActivity(goToNextActivity);
            finish();
        } else {
            UtilConstants.showAlert(getString(R.string.you_have_enterd_worng_pin), PinLoginActivity.this);
//            Toast.makeText(PinLoginActivity.this,"You have entered wrong pin",Toast.LENGTH_LONG).show();
        }
    }

    private void getAccessKey() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        accessKey = sharedPreferences.getString(Constants.QUICK_PIN, "");
        String permission = sharedPreferences.getString(Constants.QUICK_PIN_ACCESS, "");
    }

}
