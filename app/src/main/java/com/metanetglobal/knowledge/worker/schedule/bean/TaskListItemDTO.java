package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Task List Item DTO
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class TaskListItemDTO {
    @SerializedName("id")
    String id;
    @SerializedName("taskNm")
    String taskNm;
    @SerializedName("colrCd")
    String colrCd;
    @SerializedName("validStartDt")
    String validStartDt;
    @SerializedName("validEndDt")
    String validEndDt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskNm() {
        return taskNm;
    }

    public void setTaskNm(String taskNm) {
        this.taskNm = taskNm;
    }

    public String getColrCd() {
        return colrCd;
    }

    public void setColrCd(String colrCd) {
        this.colrCd = colrCd;
    }

    public String getValidStartDt() {
        return validStartDt;
    }

    public void setValidStartDt(String validStartDt) {
        this.validStartDt = validStartDt;
    }

    public String getValidEndDt() {
        return validEndDt;
    }

    public void setValidEndDt(String validEndDt) {
        this.validEndDt = validEndDt;
    }
}
