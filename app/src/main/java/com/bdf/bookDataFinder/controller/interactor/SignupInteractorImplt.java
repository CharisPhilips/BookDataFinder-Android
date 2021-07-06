package com.bdf.bookDataFinder.controller.interactor;


import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.controller.listener.ISignupListner;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupInteractorImplt implements ISignupInteractor {

    @Override
    public void signup(String email, String password, final ISignupListner signupListner) {

        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("password", password);

        UserProfile signup = new UserProfile();
        signup.email = email;
        signup.password = password;
        Call<UserProfile> call = Global.getApi().getApiService().signup(signup);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    signupListner.onSuccess(response.body());
                } else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if (error != null) {
                            signupListner.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        signupListner.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                signupListner.onError(Application.s_Application.getResources().getString(R.string.message_signup_fail));
            }
        });
    }
}
