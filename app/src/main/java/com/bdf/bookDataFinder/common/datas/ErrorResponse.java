package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("message")
    private String message;

    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
