package com.metanetglobal.knowledge.worker.intro;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface of Get Version (Read output.json)
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         Call
 */
public interface GetVersionApiInterface {
    //@GET("output.json")
    @GET("output")
    Call<JsonArray> getOutput();
}
