package com.metanetglobal.knowledge.worker.main.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseRequestDTO;

/**
 * Add Work In/Out Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class PushRequestDTO extends BaseRequestDTO {
    @SerializedName("token")
    String token;

    @SerializedName("empNo")
    String empNo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }
}
