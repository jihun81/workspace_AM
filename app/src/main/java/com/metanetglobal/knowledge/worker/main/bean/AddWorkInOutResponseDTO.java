package com.metanetglobal.knowledge.worker.main.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.common.bean.BaseResponseDTO;

import java.util.List;

/**
 * Add Work In/Out Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 * @see         com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO.WorkInOutList
 */
public class AddWorkInOutResponseDTO extends BaseResponseDTO {
    @SerializedName("workInOutList")
    List<LoginResponseDTO.WorkInOutList> workInOutList;

    public List<LoginResponseDTO.WorkInOutList> getWorkInOutList() {
        return workInOutList;
    }

    public void setWorkInOutList(List<LoginResponseDTO.WorkInOutList> workInOutList) {
        this.workInOutList = workInOutList;
    }
}
