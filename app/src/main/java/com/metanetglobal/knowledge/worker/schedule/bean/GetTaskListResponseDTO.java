package com.metanetglobal.knowledge.worker.schedule.bean;

import com.google.gson.annotations.SerializedName;
import com.metanetglobal.knowledge.worker.common.bean.BaseResponseDTO;

import java.util.List;

/**
 * Get Task List Response DTO
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseResponseDTO
 * @see         TaskListItemDTO
 */
public class GetTaskListResponseDTO extends BaseResponseDTO {
    @SerializedName("taskList")
    List<TaskListItemDTO> taskList;

    public List<TaskListItemDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskListItemDTO> taskList) {
        this.taskList = taskList;
    }
}
