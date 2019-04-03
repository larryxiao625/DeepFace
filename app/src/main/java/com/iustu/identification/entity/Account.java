package com.iustu.identification.entity;

import retrofit2.http.GET;

/**
 * created by sgh, 2019-4-2
 *
 * 用来记录登录账户的数据表
 */
public class Account {
    String name;     // 作为主键
    String password;
}
