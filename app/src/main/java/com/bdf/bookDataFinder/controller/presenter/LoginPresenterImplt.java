package com.bdf.bookDataFinder.controller.presenter;

import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.controller.interactor.ILoginInteractor;
import com.bdf.bookDataFinder.controller.interactor.LoginInteractorImplt;
import com.bdf.bookDataFinder.controller.listener.ILoginListner;
import com.bdf.bookDataFinder.controller.presenter.ILoginPresenter;
import com.bdf.bookDataFinder.controller.view.ILoginView;

public class LoginPresenterImplt implements ILoginPresenter, ILoginListner {
    ILoginInteractor loginInteractor;
    ILoginView loginView;

    public LoginPresenterImplt(ILoginView loginView) {
        this.loginView = loginView;
        loginInteractor = (ILoginInteractor) new LoginInteractorImplt();
    }

    @Override
    public void onSuccess(UserProfile loginResponse) {
        loginView.hideProgress();
        loginView.onSuccess(loginResponse);
    }

    @Override
    public void onError(String message) {
        loginView.hideProgress();
        loginView.onError(message);
    }

    @Override
    public void login(String email, String password) {
        loginView.showProgress();
        loginInteractor.login(email, password, this);
    }
}
