package com.bdf.bookDataFinder.apis.payment;

import com.bdf.bookDataFinder.apis.payment.model.CreateChargeResponse;
import com.bdf.bookDataFinder.apis.payment.model.VerifyResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentService {
    @FormUrlEncoded
    @POST("/create-charge")
    Call<CreateChargeResponse> createCharge(@Field("uid") String uid, @Field("session_id") String session_id,
                                            @Field("token") String token, @Field("amount") double amount,
                                            @Field("dev_reference") String dev_reference, @Field("description") String description);

    @FormUrlEncoded
    @POST("/verify-transaction")
    Call<VerifyResponse> verifyTransaction(@Field("uid") String uid, @Field("transaction_id") String transaction_id,
                                           @Field("type") String type, @Field("value") String value);
}
