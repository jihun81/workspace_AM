package com.metanetglobal.knowledge.worker.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Get Common Code List Request DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseRequestDTO
 */
public class GetCommCodeListRequestDTO extends BaseRequestDTO {
    @SerializedName("groupCode")
    String groupCode;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
