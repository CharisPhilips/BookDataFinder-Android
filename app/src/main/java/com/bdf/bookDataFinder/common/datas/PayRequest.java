package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class PayRequest {
    public enum Currency {
        USD, EUR;
    }
    @SerializedName("stripeEmail")
    public String stripeEmail;
    @SerializedName("paymentMethodId")
    public String paymentMethodId;
    @SerializedName("paymentIntentId")
    public String paymentIntentId;

}
