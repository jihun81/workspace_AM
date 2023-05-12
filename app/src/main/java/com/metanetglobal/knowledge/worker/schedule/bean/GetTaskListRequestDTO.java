package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseRequestDTO;

/**
 * Get Task List Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class GetTaskListRequestDTO extends BaseRequestDTO {
    @SerializedName("startDt")
    String startDt;
    @SerializedName("endDt")
    String endDt;

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }
}
