package com.metanetglobal.knowledge.worker.common;

import com.metanetglobal.knowledge.worker.common.bean.GetCommCodeListRequestDTO;
import com.metanetglobal.knowledge.worker.common.bean.GetCommCodeListResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface of Get Common Code List Api
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 * @see         GetCommCodeListRequestDTO
 * @see         GetCommCodeListResponseDTO
 */
public interface GetCommCodeListApiInterface {
    @POST("activityManager/api/common/getCommCodeList.do")
    Call<GetCommCodeListResponseDTO> getCommCodeList(@Body GetCommCodeListRequestDTO dto);
}
