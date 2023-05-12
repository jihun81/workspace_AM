package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseRequestDTO;

/**
 * Schedule Delete Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class ScheduleDeleteRequestDTO extends BaseRequestDTO {
    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
