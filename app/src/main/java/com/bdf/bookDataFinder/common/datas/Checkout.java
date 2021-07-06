package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class Checkout {
    public enum Currency {
        USD, EUR;
    }
    @SerializedName("stripePublicKey")
    public String stripePublicKey;
    @SerializedName("clientSecret")
    public String clientSecret;
    @SerializedName("amount")
    public int amount;
    @SerializedName("currency")
    public Currency currency;

}
