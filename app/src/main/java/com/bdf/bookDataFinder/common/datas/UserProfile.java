package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    @SerializedName("dbId")
    public long dbId; //db
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("id")
    public Long id;//server
    @SerializedName("servicedateStr")
    public String servicedateStr;
    @SerializedName("allowService")
    public boolean allowService;
    @SerializedName("creditCardList")
    public List<CreditCardData> creditCardList = new ArrayList<CreditCardData>();

    public void addCreditCard(CreditCardData card) {
        if (card != null) {
            creditCardList.add(card);
        }
    }

}
