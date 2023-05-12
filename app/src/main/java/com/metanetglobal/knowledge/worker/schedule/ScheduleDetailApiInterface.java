package com.metanetglobal.knowledge.worker.schedule;

import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDetailRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDetailResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Schedule Detail api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         ScheduleDetailRequestDTO
 * @see         ScheduleDetailResponseDTO
 */
public interface ScheduleDetailApiInterface {
    @POST("activityManager/api/workScheduleDtl.do")
    Call<ScheduleDetailResponseDTO> getScheduleDetail(@Body ScheduleDetailRequestDTO dto);
}
