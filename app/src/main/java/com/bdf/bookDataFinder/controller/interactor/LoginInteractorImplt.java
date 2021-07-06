package com.bdf.bookDataFinder.controller.interactor;



import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.controller.interactor.ILoginInteractor;
import com.bdf.bookDataFinder.controller.listener.ILoginListner;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginInteractorImplt implements ILoginInteractor {

    @Override
    public void login(String email, String password, final ILoginListner loginListner) {

        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("password",password);

        Call<UserProfile> call = Global.getApi().getApiService().login(hashMap);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful()) {
                    loginListner.onSuccess(response.body());
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            loginListner.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        loginListner.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                loginListner.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
            }
        });
    }
}
