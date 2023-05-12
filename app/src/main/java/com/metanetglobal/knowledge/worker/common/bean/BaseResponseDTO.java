package com.metanetglobal.knowledge.worker.common.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Base Response DTO
 *
 * 모든 Response DTO 들은 이 Class 를 상속받는다.
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class BaseResponseDTO {
    @SerializedName("success")
    private boolean success;

    @SerializedName("errorType")
    private int errorType;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
            this.message = message;
        }
}
