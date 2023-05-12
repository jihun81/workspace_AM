package com.metanetglobal.knowledge.worker.schedule;

import com.metanetglobal.knowledge.worker.schedule.bean.GetTaskListRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.GetTaskListResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Get Task List api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         GetTaskListRequestDTO
 * @see         GetTaskListResponseDTO
 */
public interface GetTaskListApiInterface {
    @POST("activityManager/api/getTaskList.do")
    Call<GetTaskListResponseDTO> getTaskList(@Body GetTaskListRequestDTO dto);
}
