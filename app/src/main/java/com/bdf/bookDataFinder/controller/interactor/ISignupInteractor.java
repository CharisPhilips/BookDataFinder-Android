package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.controller.listener.ISignupListner;

public interface ISignupInteractor {
    void signup(String email, String password, final ISignupListner signupListner);
}
