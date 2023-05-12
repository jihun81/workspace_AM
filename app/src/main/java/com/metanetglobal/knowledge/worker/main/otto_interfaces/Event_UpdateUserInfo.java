package com.metanetglobal.knowledge.worker.main.otto_interfaces;

import com.google.gson.annotations.SerializedName;

/**
 * Event of Update User Information for OTTO
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class Event_UpdateUserInfo {
    @SerializedName("userInfo")
    private String userInfo;
    @SerializedName("workInOutList")
    private String workInOutList;

    public Event_UpdateUserInfo() {
        // NOTHING
    }

    public Event_UpdateUserInfo(String userInfo, String workInOutList) {
        this.userInfo = userInfo;
        this.workInOutList = workInOutList;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getWorkInOutList() {
        return workInOutList;
    }

    public void setWorkInOutList(String workInOutList) {
        this.workInOutList = workInOutList;
    }
}
