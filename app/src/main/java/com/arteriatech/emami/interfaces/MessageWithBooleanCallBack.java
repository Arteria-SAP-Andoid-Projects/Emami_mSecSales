package com.arteriatech.emami.interfaces;

import com.arteriatech.emami.mbo.ErrorBean;

/**
 * Created by e10526 on 6/27/2017.
 */

public interface MessageWithBooleanCallBack {
    void clickedStatus(boolean clickedStatus,String errorMsg,ErrorBean errorBean);
}
