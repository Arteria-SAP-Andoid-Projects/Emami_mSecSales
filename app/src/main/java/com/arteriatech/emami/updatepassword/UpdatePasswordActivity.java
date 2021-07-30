package com.arteriatech.emami.updatepassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;

/**
 * Created by e10526 on 5/30/2017.
 *
 */

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private String mStrNewPwd="";
    EditText etNewPwd,etConfirmPwd;
    CheckBox showPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.update_pwd_menu));
        if (!Constants.restartApp(UpdatePasswordActivity.this)) {
            initUI();
        }

    }

private void initUI(){
    showPass = (CheckBox) findViewById(R.id.cbShowPwd);
    showPass.setOnClickListener(this);

    etNewPwd = (EditText)findViewById(R.id.etNewPwd);
    etNewPwd.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etNewPwd.setBackgroundResource(R.drawable.edittext);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    etConfirmPwd = (EditText)findViewById(R.id.etConfirmPwd);
    etConfirmPwd.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etNewPwd.setBackgroundResource(R.drawable.edittext);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
}


    private void setPwdInDataVault() {
        try {
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            lgCtx.setBackendPassword(mStrNewPwd);
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update_pwd, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_update_pwd:

                if (isVaildationSuccess()) {
                    mStrNewPwd = etNewPwd.getText().toString();
                    setPwdInDataVault();
                    UtilConstants.showAlert( getString(R.string.alert_pwd_updated_succefully),UpdatePasswordActivity.this);
                    onBackPressed();
                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    public Boolean isVaildationSuccess() {

        if (etNewPwd.getText().toString().equals("") || etConfirmPwd.getText().toString().equals("")) {
            if (etNewPwd.getText().toString().equals("")) {
                etNewPwd.setBackgroundResource(R.drawable.edittext_border);
            }
            if (etConfirmPwd.getText().toString().equals("")) {
                etConfirmPwd.setBackgroundResource(R.drawable.edittext_border);
            }
            UtilConstants.showAlert(getString(R.string.validation_plz_enter_mandatory_flds),UpdatePasswordActivity.this);
            return false;
        }else if(etNewPwd.getText().toString().length()<6 || etConfirmPwd.getText().toString().length()<6) {
            UtilConstants.showAlert( getString(R.string.validation_pwd_length_should_6_characters),UpdatePasswordActivity.this);
            return false;
        }else if(!etNewPwd.getText().toString().equalsIgnoreCase(etConfirmPwd.getText().toString())){
            UtilConstants.showAlert( getString(R.string.validation_new_pwd_confirm_pwd_same),UpdatePasswordActivity.this);
            return false;
        }else{
            return true;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cbShowPwd:
                if (showPass.isChecked()) {
                    etNewPwd.setTransformationMethod(null);
                    etNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    etConfirmPwd.setTransformationMethod(null);
                    etConfirmPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etNewPwd.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    etNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirmPwd.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    etConfirmPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
            default:
                break;
        }
    }


}
