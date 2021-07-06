package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.controller.listener.IChangepwdListner;

public interface IChangepwdInteractor {
    void changePassword(String email, String oldpassword, String password, final IChangepwdListner changepwdListner);
}
