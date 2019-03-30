package com.iustu.identification.api.message.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/12/5.
 */

public class LoginRequest {
    @SerializedName("user_name")
    private String userName;
    private String nonce;
    @SerializedName("auth_code")
    private String authCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
