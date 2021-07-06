package com.bdf.bookDataFinder.controller.listener;

import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;

public interface IPaymentListner {

    void onSuccessCheckout(Checkout checkoutResponse);
    void onSuccessRegister(CreditCardData cardResponse);
    void onError(String message);
}
