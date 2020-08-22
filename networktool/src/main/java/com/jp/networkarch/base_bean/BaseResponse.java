package com.jp.networkarch.base_bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/21 22:40
 * @Description : 文件描述
 */
public class BaseResponse {
    @SerializedName("error_code")
    @Expose
    public int code;
    @SerializedName("reason")
    @Expose
    public String msg;
    @SerializedName("resultcode")
    @Expose
    public int resultCode;

}
