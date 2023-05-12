package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Schedule List Item DTO
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class ScheduleListItemDTO {
    @SerializedName("id")
    private String id;
    @SerializedName("startDt")
    private String startDt;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("endDt")
    private String endDt;
    @SerializedName("endTime")
    private String endTime;
    @SerializedName("taskId")
    private String taskId;
    @SerializedName("taskNm")
    private String taskNm;
    @SerializedName("dutyCd")
    private String dutyCd;
    @SerializedName("dutyCdNm")
    private String dutyCdNm;
    @SerializedName("wrkDesc")
    private String wrkDesc;
    @SerializedName("wrkComnt")
    private String wrkComnt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskNm() {
        return taskNm;
    }

    public void setTaskNm(String taskNm) {
        this.taskNm = taskNm;
    }

    public String getDutyCd() {
        return dutyCd;
    }

    public void setDutyCd(String dutyCd) {
        this.dutyCd = dutyCd;
    }

    public String getDutyCdNm() {
        return dutyCdNm;
    }

    public void setDutyCdNm(String dutyCdNm) {
        this.dutyCdNm = dutyCdNm;
    }

    public String getWrkDesc() {
        return wrkDesc;
    }

    public void setWrkDesc(String wrkDesc) {
        this.wrkDesc = wrkDesc;
    }

    public String getWrkComnt() {
        return wrkComnt;
    }

    public void setWrkComnt(String wrkComnt) {
        this.wrkComnt = wrkComnt;
    }
}
