package com.bdf.bookDataFinder.controller.presenter;

import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.controller.interactor.ISignupInteractor;
import com.bdf.bookDataFinder.controller.interactor.SignupInteractorImplt;
import com.bdf.bookDataFinder.controller.listener.ISignupListner;
import com.bdf.bookDataFinder.controller.view.ISignupView;

public class SignupPresenterImplt implements ISignupPresenter, ISignupListner {

    ISignupInteractor signupInteractor;
    ISignupView signupView;

    public SignupPresenterImplt(ISignupView signupView) {
        this.signupView = signupView;
        signupInteractor = (ISignupInteractor) new SignupInteractorImplt();
    }

    @Override
    public void onSuccess(UserProfile loginResponse) {
        signupView.hideProgress();
        signupView.onSuccess(loginResponse);
    }

    @Override
    public void onError(String message) {
        signupView.hideProgress();
        signupView.onError(message);
    }

    @Override
    public void signup(String email, String password) {
        signupView.showProgress();
        signupInteractor.signup(email, password, this);
    }
}
