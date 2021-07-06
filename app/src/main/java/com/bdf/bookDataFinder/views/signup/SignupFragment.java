package com.bdf.bookDataFinder.views.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.controller.listener.ISignupEventListener;
import com.bdf.bookDataFinder.controller.presenter.ISignupPresenter;
import com.bdf.bookDataFinder.controller.presenter.SignupPresenterImplt;
import com.bdf.bookDataFinder.controller.view.ISignupView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupFragment extends Fragment implements ISignupView {

    @BindView(R.id.edit_email)
    public EditText editEmail;
    @BindView(R.id.edit_password)
    public EditText editPassword;
    @BindView(R.id.edit_repassword)
    public EditText editRepassword;
    @BindView(R.id.chk_remember_me)
    public CheckBox chkRememberMe;
    @BindView(R.id.btn_signup)
    public Button btnSignup;

    private ISignupPresenter signupPresenter;
    private ISignupEventListener signupEventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, root);

        if (signupPresenter == null) {
            signupPresenter = new SignupPresenterImplt(this);
        }

        initListeners();
        return root;
    }

    private void initListeners() {
        this.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    @Override
    public void onSuccess(UserProfile user) {
        if (user != null) {
            //login success
            if (this.chkRememberMe.isChecked()) {
                Global.saveUserData(user);
            }

            ViewUtils.showToast(getContext(), getResources().getString(R.string.message_signup_succcess));
            SignupFragment.this.signupEventListener.onSignupSuccess();
        }
    }

    @Override
    public void onError(String message) {
        ViewUtils.showToast(getContext(), message);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    private void signup() {
        if (validateData()) {
            makeSignupRequest();
        }
    }

    private boolean validateData() {
        String email = editEmail.getText().toString().trim();
        if (email.equalsIgnoreCase("")) {
            ViewUtils.showToast(getContext(), getContext().getString(R.string.message_enter_email));
            return false;
        }
        if (editPassword.getText().toString().length() > 0) {

        } else {
            ViewUtils.showToast(getContext(), getContext().getString(R.string.message_enter_password));
            return false;
        }

        if (editRepassword.getText().toString().equals(editPassword.getText().toString())) {

        } else {
            ViewUtils.showToast(getContext(), getContext().getString(R.string.message_verify_password));
            return false;
        }
        return true;
    }

    private void makeSignupRequest() {
        signupPresenter.signup(editEmail.getText().toString(), editPassword.getText().toString());
    }

    @Override
    public void setSignupEventListener(ISignupEventListener signupEventListener) {
        this.signupEventListener = signupEventListener;
    }
}
