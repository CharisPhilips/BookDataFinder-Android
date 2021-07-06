package com.bdf.bookDataFinder.controller.listener;

import com.bdf.bookDataFinder.common.datas.UserProfile;

public interface ILoginListner {
    void onSuccess(UserProfile loginResponse);
    void onError(String message);
}
