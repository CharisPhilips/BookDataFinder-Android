package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class PayResponse {
    @SerializedName("requiresAction")
    public boolean requiresAction;
    @SerializedName("clientSecret")
    public String clientSecret;
    @SerializedName("error")
    public String error;

}
