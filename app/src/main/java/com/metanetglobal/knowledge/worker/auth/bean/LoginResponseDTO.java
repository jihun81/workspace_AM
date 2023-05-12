package com.metanetglobal.knowledge.worker.auth.bean;

import com.metanetglobal.knowledge.worker.common.bean.BaseResponseDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Login Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 */
public class LoginResponseDTO extends BaseResponseDTO {
    @SerializedName("userInfo")
    UserInfo userInfo;
    @SerializedName("workInOutList")
    List<WorkInOutList> workInOutList;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<WorkInOutList> getWorkInOutList() {
        return workInOutList;
    }

    public void setWorkInOutList(List<WorkInOutList> workInOutList) {
        this.workInOutList = workInOutList;
    }

    /**
     * 회원정보
     */
    public class UserInfo {
        @SerializedName("empRid")
        private String empRid;
        @SerializedName("empName")
        private String empName;
        @SerializedName("empNo")
        private String empNo;
        @SerializedName("prmryDivNm")
        private String prmryDivNm;
        @SerializedName("position")
        private String position;
        @SerializedName("legalEntityName")
        private String legalEntityName;

        @SerializedName("pw")
        private String pw;
        @SerializedName("tel")
        private String tel;

        @SerializedName("workInTime")
        private String workInTime;
        @SerializedName("workOutTime")
        private String workOutTime;


        public String getEmpRid() {
            return empRid;
        }

        public void setEmpRid(String empRid) {
            this.empRid = empRid;
        }

        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }

        public String getEmpNo() {
            return empNo;
        }

        public void setEmpNo(String empNo) {
            this.empNo = empNo;
        }

        public String getPrmryDivNm() {
            return prmryDivNm;
        }

        public void setPrmryDivNm(String prmryDivNm) {
            this.prmryDivNm = prmryDivNm;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getLegalEntityName() {
            return legalEntityName;
        }

        public void setLegalEntityName(String legalEntityName) {
            this.legalEntityName = legalEntityName;
        }

        public String getPw() {
            return pw;
        }

        public void setPw(String pw) {
            this.pw = pw;
        }


        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getWorkInTime() {
            return workInTime;
        }

        public void setWorkInTime(String workInTime) {
            this.workInTime = workInTime;
        }

        public String getWorkOutTime() {
            return workOutTime;
        }

        public void setWorkOutTime(String workOutTime) {
            this.workOutTime = workOutTime;
        }
    }

    /**
     * 출퇴근 목록
     */
    public class WorkInOutList {
        @SerializedName("wrkInOutDate")
        private String wrkInOutDate;
        @SerializedName("wrkInOutTime")
        private String wrkInOutTime;
        @SerializedName("wrkInOutStatus")
        private String wrkInOutStatus;
        @SerializedName("wrkInOutStatusNm")
        private String wrkInOutStatusNm;

        public String getWrkInOutDate() {
            return wrkInOutDate;
        }

        public void setWrkInOutDate(String wrkInOutDate) {
            this.wrkInOutDate = wrkInOutDate;
        }

        public String getWrkInOutTime() {
            return wrkInOutTime;
        }

        public void setWrkInOutTime(String wrkInOutTime) {
            this.wrkInOutTime = wrkInOutTime;
        }

        public String getWrkInOutStatus() {
            return wrkInOutStatus;
        }

        public void setWrkInOutStatus(String wrkInOutStatus) {
            this.wrkInOutStatus = wrkInOutStatus;
        }

        public String getWrkInOutStatusNm() {
            return wrkInOutStatusNm;
        }

        public void setWrkInOutStatusNm(String wrkInOutStatusNm) {
            this.wrkInOutStatusNm = wrkInOutStatusNm;
        }
    }
}
