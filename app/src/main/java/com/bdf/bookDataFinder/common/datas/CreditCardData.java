package com.bdf.bookDataFinder.common.datas;

import com.google.gson.annotations.SerializedName;

public class CreditCardData {

    @SerializedName("dbid")
    public Long dbid;
    @SerializedName("userid")
    public long userid = -1;
    @SerializedName("cardno")
    public String cardno;
    @SerializedName("expmonth")
    public int expmonth;
    @SerializedName("expyear")
    public int expyear;
    @SerializedName("cardcvc")
    public String cardcvc;

    public CreditCardData(Long dbid, String cardNo, int expmonth, int expyear, String cardcvc, long userid) {
        this.dbid = dbid;
        this.cardno = cardNo;
        this.expmonth = expmonth;
        this.expyear = expyear;
        this.cardcvc = cardcvc;
        this.userid = userid;
    }
}
