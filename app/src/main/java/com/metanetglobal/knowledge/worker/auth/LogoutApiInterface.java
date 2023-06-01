package com.metanetglobal.knowledge.worker.auth;

import com.metanetglobal.knowledge.worker.auth.bean.LogoutRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LogoutResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of logout api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         LogoutRequestDTO
 * @see         LogoutResponseDTO
 */
public interface LogoutApiInterface {
    @POST("api/logout")
    Call<LogoutResponseDTO> doLogout(@Body LogoutRequestDTO dto);
}
