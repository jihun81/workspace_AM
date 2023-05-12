package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseResponseDTO;

/**
 * Schedule Detail Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 * @see         ScheduleListItemDTO
 */
public class ScheduleDetailResponseDTO extends BaseResponseDTO {
    @SerializedName("workScheduleInfo")
    ScheduleListItemDTO workScheduleInfo;

    public ScheduleListItemDTO getWorkScheduleInfo() {
        return workScheduleInfo;
    }

    public void setWorkScheduleInfo(ScheduleListItemDTO workScheduleInfo) {
        this.workScheduleInfo = workScheduleInfo;
    }
}
