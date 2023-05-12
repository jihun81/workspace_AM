package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseResponseDTO;

import java.util.List;

/**
 * Schedule List By Date Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 * @see         ScheduleListItemDTO
 */
public class ScheduleListByDateResponseDTO extends BaseResponseDTO {
    @SerializedName("scheduleList")
    List<ScheduleListItemDTO> scheduleList;

    public List<ScheduleListItemDTO> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<ScheduleListItemDTO> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
