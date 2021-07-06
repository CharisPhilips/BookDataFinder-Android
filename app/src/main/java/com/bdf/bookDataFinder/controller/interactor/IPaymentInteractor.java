package com.bdf.bookDataFinder.controller.interactor;

import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.controller.listener.IPaymentListner;

public interface IPaymentInteractor {
    void checkout(String email, final IPaymentListner paymentListner);
    void register(CreditCardData card, final IPaymentListner paymentListner);
    void charge(long usrid, String token, final IPaymentListner paymentListner);
}
