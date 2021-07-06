package com.bdf.bookDataFinder.controller.presenter;

import com.bdf.bookDataFinder.common.datas.CreditCardData;

public interface IPaymentPresenter {

    void checkout(String email);
    void register(CreditCardData cardData);
    void charge(long userid, String token);
}
