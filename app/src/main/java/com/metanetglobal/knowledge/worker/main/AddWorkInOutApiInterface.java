package com.metanetglobal.knowledge.worker.main;

import com.metanetglobal.knowledge.worker.main.bean.AddWorkInOutRequestDTO;
import com.metanetglobal.knowledge.worker.main.bean.AddWorkInOutResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Add Work In/Out api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         AddWorkInOutRequestDTO
 * @see         AddWorkInOutResponseDTO
 */
public interface AddWorkInOutApiInterface {
    //@POST("activityManager/api/addWrkInOut.do")
    @POST("test/api/saveWorkTime")
    Call<AddWorkInOutResponseDTO> addWorkInOut(@Body AddWorkInOutRequestDTO dto);
}
