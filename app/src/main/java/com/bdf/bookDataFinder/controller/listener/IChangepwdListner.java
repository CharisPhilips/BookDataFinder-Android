package com.bdf.bookDataFinder.controller.listener;

import com.bdf.bookDataFinder.common.datas.UserProfile;

public interface IChangepwdListner {
    void onSuccess(UserProfile loginResponse);
    void onError(String message);
}
