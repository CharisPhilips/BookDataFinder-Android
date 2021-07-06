package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.controller.listener.IPaymentListner;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentInteractorImplt implements IPaymentInteractor {

    @Override
    public void checkout(String email, IPaymentListner paymentListner) {

        HashMap hashMap = new HashMap();
        hashMap.put("email", email);

        Call<Checkout> call = Global.getApi().getApiService().checkout(hashMap);
        call.enqueue(new Callback<Checkout>() {
            @Override
            public void onResponse(Call<Checkout> call, Response<Checkout> response) {
                if(response.isSuccessful()) {
                    paymentListner.onSuccessCheckout(response.body());
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            paymentListner.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        paymentListner.onError(Application.s_Application.getResources().getString(R.string.message_checkout_fail));
                    }
                }
            }
            @Override
            public void onFailure(Call<Checkout> call, Throwable error) {
                paymentListner.onError(Application.s_Application.getResources().getString(R.string.message_checkout_fail));
            }
        });
    }

    @Override
    public void register(CreditCardData card, IPaymentListner paymentListner) {

        Call<CreditCardData> call = Global.getApi().getApiService().registerCard(card);
        call.enqueue(new Callback<CreditCardData>() {
            @Override
            public void onResponse(Call<CreditCardData> call, Response<CreditCardData> response) {
                if(response.isSuccessful()) {
                    paymentListner.onSuccessRegister(response.body());
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            paymentListner.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        paymentListner.onError(Application.s_Application.getResources().getString(R.string.message_card_register_fail));
                    }
                }
            }

            @Override
            public void onFailure(Call<CreditCardData> call, Throwable error) {
                paymentListner.onError(Application.s_Application.getResources().getString(R.string.message_card_register_fail));
            }
        });
    }

    @Override
    public void charge(long usrid, String token, IPaymentListner paymentListner) {

    }


//    @Override
//    public void (String email, String password, final ILoginListner loginListner) {
//
//        HashMap hashMap = new HashMap();
//        hashMap.put("email", email);
//        hashMap.put("password", password);
//
//        Call<UserProfile> call = Global.getApi().getApiService().login(hashMap);
//        call.enqueue(new Callback<UserProfile>() {
//            @Override
//            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
//                loginListner.onSuccess(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<UserProfile> call, Throwable error) {
//                loginListner.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
//            }
//        });
//    }
}
