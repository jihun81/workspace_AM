package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseRequestDTO;

/**
 * Schedule Save Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class ScheduleSaveRequestDTO extends BaseRequestDTO {
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
    @SerializedName("dutyCd")
    private String dutyCd;
    @SerializedName("wrkDesc")
    private String wrkDesc;

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

    public String getDutyCd() {
        return dutyCd;
    }

    public void setDutyCd(String dutyCd) {
        this.dutyCd = dutyCd;
    }

    public String getWrkDesc() {
        return wrkDesc;
    }

    public void setWrkDesc(String wrkDesc) {
        this.wrkDesc = wrkDesc;
    }
}
