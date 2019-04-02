package com.iustu.identification.api.message.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Liu Yuchuan on 2017/12/5.
 */

public class PreLoginResponse {
    @SerializedName("need_auth")
    private boolean needAuth;
    private String nonce;

    public boolean isNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
