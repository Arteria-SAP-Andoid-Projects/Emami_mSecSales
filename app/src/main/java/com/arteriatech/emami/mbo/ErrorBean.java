package com.arteriatech.emami.mbo;

/**
 * Created by e10526 on 6/23/2017.
 */

public class ErrorBean {
    private int ErrorCode = 0;

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }

    private String ErrorMsg = "";

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public boolean hasNoError() {
        return hasNoError;
    }

    public void setHasNoError(boolean hasNoError) {
        this.hasNoError = hasNoError;
    }

    private boolean hasNoError = false;

    public boolean isStoreFailed() {
        return isStoreFailed;
    }

    public void setStoreFailed(boolean storeFailed) {
        isStoreFailed = storeFailed;
    }

    private boolean isStoreFailed = false;
}
