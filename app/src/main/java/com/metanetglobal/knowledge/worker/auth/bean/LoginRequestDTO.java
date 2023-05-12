package com.metanetglobal.knowledge.worker.auth.bean;

import com.metanetglobal.knowledge.worker.common.bean.BaseRequestDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Login Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class LoginRequestDTO extends BaseRequestDTO {
    @SerializedName("userId")
    private String userId;
    @SerializedName("userPW")
    private String userPW;
    @SerializedName("telNo")
    private String telNo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }
}
