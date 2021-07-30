package com.arteriatech.emami.visit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;


/**
 * This class may be will use in future for the displaying USP description,This class coming from NewProductListActivity.Java
 */
public class FocusedProductActivity extends AppCompatActivity {

    private TextView tvMatDesc, tvMatcode, tv_usp_val;
    String matCode = "", matDesc = "", segmntedDesc = "", uspDesc = "";
    TextView tvHeadingView;
    String desc = "";
    String[] splitDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_product);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matDesc = extras.getString(Constants.Description);
            matCode = extras.getString(Constants.ID);
            uspDesc = extras.getString(Constants.UspDesc);
            segmntedDesc = extras.getString(Constants.UspDesc);
        }
        if (!Constants.restartApp(FocusedProductActivity.this)) {
            initUI();
            setValuesToUI();
            setUSPValue();
        }
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, segmntedDesc);
    }

    // TODO Initialize UI
    private void initUI() {
        tvMatDesc = (TextView) findViewById(R.id.tvMatDesc);
        tvMatcode = (TextView) findViewById(R.id.tvMatcode);
        tv_usp_val = (TextView) findViewById(R.id.tv_usp_desc);
        tvHeadingView = (TextView) findViewById(R.id.tv_page_name);
    }

    // TODO set values to UI
    private void setValuesToUI() {
        tvHeadingView.setText(segmntedDesc);
        tvMatcode.setText(matCode);
        tvMatDesc.setText(matDesc);

    }

    /*
    TODO Display USP description in button format
     */
    private void setUSPValue() {
        if (!uspDesc.equalsIgnoreCase("")) {
            splitDesc = uspDesc.split("#");
            for (String aSplitDesc : splitDesc) {
                desc += "\n &#8226; \t " + aSplitDesc + "<br>";
            }
            tv_usp_val.setText(Html.fromHtml(desc));
        } else {
            tv_usp_val.setText(getString(R.string.no_data_found));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}
