package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.controller.listener.ILoginListner;

public interface ILoginInteractor {
    void login(String email, String password, final ILoginListner loginListner);
}
