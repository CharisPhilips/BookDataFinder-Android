package com.bdf.bookDataFinder.views.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.activities.MainActivity;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.PayRequest;
import com.bdf.bookDataFinder.common.datas.Checkout;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.datas.PayResponse;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.controller.presenter.IPaymentPresenter;
import com.bdf.bookDataFinder.controller.presenter.PaymentPresenterImplt;
import com.bdf.bookDataFinder.controller.view.IPaymentView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardMultilineWidget;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment implements IPaymentView {

    @BindView(R.id.card_multiline_widget)
    public CardMultilineWidget cardWidget;
    @BindView(R.id.btn_register)
    public Button btn_register;
    @BindView(R.id.layout_parent)
    public LinearLayout layoutParent;
    @BindView(R.id.btn_pay)
    public Button btn_pay;
    @BindView(R.id.tv_service)
    public TextView tv_service;

    private IPaymentPresenter paymentPresenter;
    //when withhook mode
    private String paymentIntentClientSecret;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        ButterKnife.bind(this, root);
        if(!Global.IS_CONNECT_INTERNET) {
            cardWidget.setVisibility(View.INVISIBLE);
            btn_register.setVisibility(View.INVISIBLE);
            btn_pay.setVisibility(View.INVISIBLE);
            ViewUtils.showToast(Application.s_Application, getResources().getString(R.string.error_network));
        }
        else {
            if (paymentPresenter == null) {
                paymentPresenter = new PaymentPresenterImplt(this);
            }
            InvalidateCard();
            initListeners();
        }

        return root;
    }

    private void initListeners() {

        if(Global.g_user!=null && Global.g_user.allowService) {
            tv_service.setText(Global.g_user.servicedateStr);
        }
        else {
            this.paymentPresenter.checkout(Global.g_user.email);
        }

        //for test
        cardWidget.setCardNumber("4242424242424242");
        cardWidget.setExpiryDate(3, 2020);
        cardWidget.setCvcCode("314");
        //for test

        this.btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_register.setEnabled(false);

                //for test
                CreditCardData creditCard = new CreditCardData(null,"4242424242424242", 3, 2020, "314", Global.g_user.id);
                paymentPresenter.register(creditCard);
                //for test

                if (!cardWidget.validateAllFields()) {
                    ViewUtils.showToast(PaymentFragment.this.getContext(), getResources().getString(R.string.message_enter_card));
                    return;
                }

                Card card = PaymentFragment.this.cardWidget.getCard();
                if(card!=null) {
                    //CreditCardData creditCard = new CreditCardData(card.getNumber(), card.getExpMonth(), card.getExpYear(), card.getCvc(), Global.g_user.dbid);
                    paymentPresenter.register(creditCard);
                }
            }
        });

        this.btn_pay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentMethodCreateParams params = cardWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    showProgress();
                    Global.g_stripe.createPaymentMethod(params, new ApiResultCallback<PaymentMethod>() {
                        @Override
                        public void onSuccess(@NonNull PaymentMethod result) {
                            // Create and confirm the PaymentIntent by calling the sample server's /pay endpoint.
                            pay(result.id, null);
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            ViewUtils.showToast(Application.s_Application, "Error: " + e.getMessage());
                        }
                    });
//                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, PaymentFragment.this.paymentIntentClientSecret);
//                    Global.g_stripe.confirmPayment(PaymentFragment.this, confirmParams);
                }
            }
        });
    }

    private void pay(@Nullable String paymentMethodId, @Nullable String paymentIntentId) {

        String email = Global.g_user.email;

        PayRequest charge = new PayRequest();
        if (paymentMethodId != null) {
            charge.stripeEmail = email;
            charge.paymentMethodId = paymentMethodId;
        } else {
            charge.stripeEmail = email;
            charge.paymentIntentId = paymentIntentId;
        }

        Call<PayResponse> call = Global.getApi().getApiService().pay(charge);
        call.enqueue(new Callback<PayResponse>() {

            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                if(response.isSuccessful()) {
                    final PaymentFragment activity = PaymentFragment.this;
                    if (activity == null) {
                        return;
                    }

                    if (!response.isSuccessful()) {
                        activity.getActivity().runOnUiThread(() -> {
                            ViewUtils.showToast(Application.s_Application, "Error: " + response.toString());
                        });
                    } else {
                        String error = response.body().error;
                        String clientSecret = response.body().clientSecret;
                        boolean requiresAction = response.body().requiresAction;
                        if (error != null) {
                            ViewUtils.showToast(Application.s_Application, "Error: " + error);
                        }
                        else {
                            if (requiresAction) {
                                activity.getActivity().runOnUiThread(() -> {
                                    Global.g_stripe.authenticatePayment(activity, paymentIntentClientSecret);
                                });
                            } else {
                                ViewUtils.showToast(Application.s_Application, "Payment succeeded: " + paymentIntentClientSecret);
                                login();
                            }
                        }
                    }
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            ViewUtils.showToast(Application.s_Application, "Error: " + error.getMessage());
                        }
                    } catch (Exception e) {
                        ViewUtils.showToast(Application.s_Application, "Error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {

            }
        });
    }

    private class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<PaymentFragment> activityRef;
        PaymentResultCallback(@NonNull PaymentFragment activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final PaymentFragment fragment = activityRef.get();
            if (fragment == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                fragment.getActivity().runOnUiThread(() -> {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    ViewUtils.showToast(Application.s_Application, "Payment completed");
                    login();
                });
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                hideProgress();
                fragment.getActivity().runOnUiThread(() -> {
                    ViewUtils.showToast(Application.s_Application, "Payment failed: " + paymentIntent.getLastPaymentError().getMessage());
                });
            } else if (status == PaymentIntent.Status.RequiresConfirmation) {
                fragment.pay(null, paymentIntent.getId());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final PaymentFragment activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.getActivity().runOnUiThread(() -> {
                ViewUtils.showToast(Application.s_Application, "Error\n" + e.toString());
            });
        }
    }

    @Override
    public void onSuccessCheckout(Checkout checkoutResponse) {
        this.btn_pay.setVisibility(View.VISIBLE);
        this.btn_pay.setText(Application.s_Application.getResources().getString(R.string.title_pay) + " " + checkoutResponse.amount + " " + checkoutResponse.currency.name());
        Global.STRIPE_CLIENT_PUBLIC_KEY = checkoutResponse.stripePublicKey;
        this.paymentIntentClientSecret = checkoutResponse.clientSecret;
        Global.g_stripe = new Stripe(Application.s_Application, Objects.requireNonNull(Global.STRIPE_CLIENT_PUBLIC_KEY));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Global.g_stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    @Override
    public void onSuccessRegister(CreditCardData cardResponse) {
        if(cardResponse!=null) {
            cardResponse.userid = Global.g_user.dbId;
            Global.getDBHelper().registerCreditcard(cardResponse);
            Global.g_user.addCreditCard(cardResponse);
            InvalidateCard();
        }
        btn_register.setEnabled(true);
    }

    private void InvalidateCard() {
        if(Global.g_user!=null && Global.g_user.creditCardList.size()==1) {
            CreditCardData cardData = Global.g_user.creditCardList.get(0);
            cardWidget.setCardNumber(cardData.cardno);
            cardWidget.setExpiryDate(cardData.expmonth, cardData.expyear);
            cardWidget.setCvcCode(cardData.cardcvc);
        }
    }

    @Override
    public void onSuccessCharge(PayRequest chargeResponse) {

    }

    @Override
    public void onError(String message) {
        ViewUtils.showToast(getContext(), message);
        btn_register.setEnabled(true);
    }

    @Override
    public void showProgress() {
        ((MainActivity)this.getActivity()).showProgress();
    }

    @Override
    public void hideProgress() {
        ((MainActivity)this.getActivity()).hideProgress();
    }


//    private class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
//        @NonNull private final WeakReference<PaymentFragment> activityRef;
//
//        PaymentResultCallback(@NonNull PaymentFragment fragment) {
//            activityRef = new WeakReference<>(fragment);
//        }
//
//        @Override
//        public void onSuccess(@NonNull PaymentIntentResult result) {
//            final PaymentFragment activity = activityRef.get();
//            if (activity == null) {
//                return;
//            }
//
//            PaymentIntent paymentIntent = result.getIntent();
//            PaymentIntent.Status status = paymentIntent.getStatus();
//            if (status == PaymentIntent.Status.Succeeded) {
//                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                ViewUtils.showToast(PaymentFragment.this.getContext(), "Payment completed");// + gson.toJson(paymentIntent)
//                btn_pay.setVisibility(View.INVISIBLE);
//                showProgress();
//                login();
//            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
//                // Payment failed – allow retrying using a different payment method
//                ViewUtils.showToast(PaymentFragment.this.getContext(), "Payment failed");// + Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
//            }
//        }
//
//        @Override
//        public void onError(@NonNull Exception e) {
//            final PaymentFragment view = activityRef.get();
//            if (view == null) {
//                return;
//            }
//            // Payment request failed – allow retrying using the same payment method
//            ViewUtils.showToast(PaymentFragment.this.getContext(), "Error" + e.toString());
//        }
//    }

    private void login() {
        HashMap hashMap = new HashMap();
        hashMap.put("email", Global.g_user.email);
        hashMap.put("password", Global.g_user.password);

        Call<UserProfile> callLogin = Global.getApi().getApiService().login(hashMap);
        callLogin.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful() && response.body().allowService) {
                    Global.saveUserData(response.body());
                    tv_service.setText(Global.g_user.servicedateStr);
                    PaymentFragment.this.btn_pay.setVisibility(View.INVISIBLE);
                    hideProgress();
                }
                else {
                    login();
                }

            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                hideProgress();
            }
        });
    }

}
