package com.metanetglobal.knowledge.worker.main;

import com.metanetglobal.knowledge.worker.main.bean.PushRequestDTO;
import com.metanetglobal.knowledge.worker.main.bean.PushResponseDTO;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Add Work In/Out api

 * @see         Call
 * @see         PushRequestDTO
 */
public interface PushInterface {
    //@POST("activityManager/api/addWrkInOut.do")
    @POST("api/pushTokenSave")
    Call<PushResponseDTO> pushTokenSave(@Body PushRequestDTO dto);
}
