package com.bdf.bookDataFinder.controller.view;

import com.bdf.bookDataFinder.common.datas.PayRequest;
import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;

public interface IPaymentView {

    void onSuccessCheckout(Checkout checkoutResponse);

    void onSuccessRegister(CreditCardData cardResponse);

    void onSuccessCharge(PayRequest chargeResponse);

    void onError(String message);

    void showProgress();

    void hideProgress();
}

