package com.metanetglobal.knowledge.worker.schedule;

import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDeleteRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDeleteResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Schedule Delete api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         ScheduleDeleteRequestDTO
 * @see         ScheduleDeleteResponseDTO
 */
public interface ScheduleDeleteApiInterface {
    @POST("activityManager/api/removeWorkSchedule.do")
    Call<ScheduleDeleteResponseDTO> deleteSchedule(@Body ScheduleDeleteRequestDTO dto);
}
