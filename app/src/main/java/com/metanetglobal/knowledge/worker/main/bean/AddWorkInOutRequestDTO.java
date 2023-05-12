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
public class AddWorkInOutRequestDTO extends BaseRequestDTO {
    @SerializedName("wrkInOutStatus")
    String wrkInOutStatus;
    @SerializedName("latitudeNum")
    double latitudeNum;
    @SerializedName("longitudeNum")
    double longitudeNum;
    @SerializedName("addressInfo")
    String addressInfo;

    @SerializedName("empNo")
    String empNo;
    @SerializedName("usrId")
    String usrId;
    @SerializedName("telNo")
    String telNo;

    public String getWrkInOutStatus() {
        return wrkInOutStatus;
    }

    public void setWrkInOutStatus(String wrkInOutStatus) {
        this.wrkInOutStatus = wrkInOutStatus;
    }

    public double getLatitudeNum() {
        return latitudeNum;
    }

    public void setLatitudeNum(double latitudeNum) {
        this.latitudeNum = latitudeNum;
    }

    public double getLongitudeNum() {
        return longitudeNum;
    }

    public void setLongitudeNum(double longitudeNum) {
        this.longitudeNum = longitudeNum;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }


    //추가

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }


    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }
}
