package com.iustu.identification.api.message;

public class UploadImageCallBack {


    /**
     * errorCode : 0表示处理成功；非0表示处理失败
     * errorDesc : OK
     */

    private int errorCode;
    private String errorDesc;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
