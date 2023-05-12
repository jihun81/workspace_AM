package com.metanetglobal.knowledge.worker.schedule;

import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleSaveRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleSaveResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Schedule Save api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         ScheduleSaveRequestDTO
 * @see         ScheduleSaveResponseDTO
 */
public interface ScheduleSaveApiInterface {
    @POST("activityManager/api/saveWorkSchedule.do")
    Call<ScheduleSaveResponseDTO> saveSchedule(@Body ScheduleSaveRequestDTO dto);
}
