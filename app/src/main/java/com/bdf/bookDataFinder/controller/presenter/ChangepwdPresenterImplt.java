package com.bdf.bookDataFinder.controller.presenter;

import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.controller.interactor.ChangepwdInteractorImplt;
import com.bdf.bookDataFinder.controller.interactor.IChangepwdInteractor;
import com.bdf.bookDataFinder.controller.listener.IChangepwdListner;
import com.bdf.bookDataFinder.controller.view.IChangepwdView;

public class ChangepwdPresenterImplt implements IChangepwdPresenter, IChangepwdListner {

    IChangepwdInteractor changepwdInteractor;
    IChangepwdView changepwdView;

    public ChangepwdPresenterImplt(IChangepwdView changepwdView) {
        this.changepwdView = changepwdView;
        changepwdInteractor = (IChangepwdInteractor) new ChangepwdInteractorImplt();
    }

    @Override
    public void onSuccess(UserProfile loginResponse) {
        changepwdView.hideProgress();
        changepwdView.onSuccess(loginResponse);
    }

    @Override
    public void onError(String message) {
        changepwdView.hideProgress();
        changepwdView.onError(message);
    }

    @Override
    public void changePassword(String email, String oldpassword, String password) {
        changepwdView.showProgress();
        changepwdInteractor.changePassword(email, oldpassword, password, this);
    }
}
