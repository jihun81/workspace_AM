package com.metanetglobal.knowledge.worker.schedule;

import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListByDateRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListByDateResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Schedule List api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         ScheduleListByDateRequestDTO
 * @see         ScheduleListByDateResponseDTO
 */
public interface ScheduleListApiInterface {
    @POST("activityManager/api/workScheduleListByDate.do")
    Call<ScheduleListByDateResponseDTO> getScheduleListByDate(@Body ScheduleListByDateRequestDTO dto);
}
