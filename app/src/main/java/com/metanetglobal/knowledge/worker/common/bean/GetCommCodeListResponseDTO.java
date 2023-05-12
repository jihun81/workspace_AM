package com.metanetglobal.knowledge.worker.common.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Get Common Code List Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 * @see         CommCodeDTO
 */
public class GetCommCodeListResponseDTO extends BaseResponseDTO {
    @SerializedName("commCodeList")
    List<CommCodeDTO> commCodeList;

    public List<CommCodeDTO> getCommCodeList() {
        return commCodeList;
    }

    public void setCommCodeList(List<CommCodeDTO> commCodeList) {
        this.commCodeList = commCodeList;
    }
}
