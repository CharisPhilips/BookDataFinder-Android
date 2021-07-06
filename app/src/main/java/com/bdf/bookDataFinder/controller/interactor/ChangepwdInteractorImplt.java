package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.controller.listener.IChangepwdListner;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangepwdInteractorImplt implements IChangepwdInteractor {

    @Override
    public void changePassword(String email, String oldpassword, String password, final IChangepwdListner changepwdListner) {

        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("oldpassword", oldpassword);
        hashMap.put("password", password);

        Call<UserProfile> call = Global.getApi().getApiService().changePwd(hashMap);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful()) {
                    changepwdListner.onSuccess(response.body());
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            changepwdListner.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        changepwdListner.onError(Application.s_Application.getResources().getString(R.string.message_changepwd_fail));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                changepwdListner.onError(Application.s_Application.getResources().getString(R.string.message_changepwd_fail));
            }
        });
    }
}
