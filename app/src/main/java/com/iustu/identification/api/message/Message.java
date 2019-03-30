package com.iustu.identification.api.message;

import com.google.gson.reflect.TypeToken;

/**
 * Created by Liu Yuchuan on 2017/11/27.
 */

public class Message<T> {
    public static final int CODE_SUCCESS = 0;
    public static final int VERIFY_ERROR = 100;
    public static final int INVALID_SESSION = 101;
    public static final int INVALID_URL = 102;
    public static final int CODE_NO_LILBRARY = -9;

    private String type;
    private String id;
    private String name;
    private String session;
    private T body;
    private int code;

    public static<C> java.lang.reflect.Type getClassType(){
        TypeToken<Message<C>> typeToken = new TypeToken<Message<C>>(){};
        return typeToken.getType();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getBody() {
        return body;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}