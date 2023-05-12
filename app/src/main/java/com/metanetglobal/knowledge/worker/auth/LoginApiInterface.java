package com.metanetglobal.knowledge.worker.auth;

import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Login api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         LoginRequestDTO
 * @see         LoginResponseDTO
 */
public interface LoginApiInterface {
    /*@POST("activityManager/api/common/loginConfirm.do")*/
    @POST("test/api/loginChk")
    Call<LoginResponseDTO> doLogin(@Body LoginRequestDTO dto);
}
