package com.bdf.bookDataFinder.apis;

import com.bdf.bookDataFinder.common.datas.CategoryData;
import com.bdf.bookDataFinder.common.datas.PayRequest;
import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.PayResponse;
import com.bdf.bookDataFinder.common.datas.Pdfbook;
import com.bdf.bookDataFinder.common.datas.UserProfile;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface WebApi {

    @GET("/bookDataFinder/api/login/")
    Call<UserProfile> login(@QueryMap HashMap<String, String> login);

    @POST("/bookDataFinder/api/signup/")
    Call<UserProfile> signup(@Body UserProfile signup);

    @GET("/bookDataFinder/api/changepwd/")
    Call<UserProfile> changePwd(@QueryMap HashMap<String, String> change);

    @GET("/bookDataFinder/api/bookcategories/")
    Call<CategoryData> getBookCategory();

    @GET("/bookDataFinder/api/booksBycategory/{category_id}")
    Call<List<Pdfbook>> getBookByCategory(@Path("category_id") Long categoryId);

    @GET("/bookDataFinder/api/booksBytitle/")
    Call<List<Pdfbook>> getBookByTitle(@QueryMap HashMap<String, String> search);


    @POST("/bookDataFinder/api/registerCard/")
    Call<CreditCardData> registerCard(@Body CreditCardData card);

    //payment
    //webhook
    @GET("/bookDataFinder/api/checkout")
    Call<Checkout> checkout(@QueryMap HashMap<String, String> userParam);

    @POST("/bookDataFinder/api/pay")
    Call<PayResponse> pay(@Body PayRequest chargeRequest);

}
