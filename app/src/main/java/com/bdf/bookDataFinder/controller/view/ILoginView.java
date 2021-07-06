package com.bdf.bookDataFinder.controller.view;

import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.controller.listener.ILoginEventListener;

public interface ILoginView {

    void onSuccess(UserProfile user);

    void onError(String message);

    void showProgress();

    void hideProgress();

    void setLoginEventListener(ILoginEventListener loginEventListener);
}
