package com.bdf.bookDataFinder.controller.presenter;

import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.controller.interactor.IPaymentInteractor;
import com.bdf.bookDataFinder.controller.interactor.PaymentInteractorImplt;
import com.bdf.bookDataFinder.controller.listener.IPaymentListner;
import com.bdf.bookDataFinder.controller.view.IPaymentView;

public class PaymentPresenterImplt implements IPaymentPresenter, IPaymentListner {

    IPaymentInteractor paymentInteractor;
    IPaymentView paymentView;

    public PaymentPresenterImplt(IPaymentView loginView) {
        this.paymentView = loginView;
        paymentInteractor = (IPaymentInteractor) new PaymentInteractorImplt();
    }

    @Override
    public void checkout(String email) {
        paymentView.showProgress();
        paymentInteractor.checkout(email, this);
    }

    @Override
    public void register(CreditCardData cardData) {
        paymentView.showProgress();
        paymentInteractor.register(cardData, this);
    }

    @Override
    public void charge(long userid, String token) {
        paymentView.showProgress();
        paymentInteractor.charge(userid, token, this);
    }

    @Override
    public void onSuccessCheckout(Checkout checkoutResponse) {
        paymentView.hideProgress();
        paymentView.onSuccessCheckout(checkoutResponse);
    }

    @Override
    public void onSuccessRegister(CreditCardData cardResponse) {
        paymentView.hideProgress();
        paymentView.onSuccessRegister(cardResponse);
    }

    @Override
    public void onError(String message) {
        paymentView.hideProgress();
        paymentView.onError(message);
    }
}
