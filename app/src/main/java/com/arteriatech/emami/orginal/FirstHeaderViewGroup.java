package com.arteriatech.emami.orginal;/*
package com.arteriatech.ss.orginal;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arteriatech.ss.msfa.R;

*/
/**
 * Created by e10769 on 09-06-2017.
 *//*


public class FirstHeaderViewGroup extends FrameLayout implements TableFixHeaderAdapter.FirstHeaderBinder<String>{
    private Context context;
    private TextView textView;
    private View vg_root;

    public FirstHeaderViewGroup(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(context).inflate(R.layout.text_view_group, this, true);
        textView = (TextView) findViewById(R.id.tv_text);
//        hEditText = (EditText) findViewById(R.id.etQty);
        vg_root = findViewById(R.id.vg_root);
    }

    @Override
    public void bindFirstHeader(String item) {
        if (textView != null) {
            textView.setText(item.toUpperCase());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
        }
        if (vg_root != null) {
            vg_root.setBackgroundResource(R.drawable.cell_header_border_bottom_right_gray);
        }

    }
}
*/
