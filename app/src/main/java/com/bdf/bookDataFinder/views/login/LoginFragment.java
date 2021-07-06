package com.bdf.bookDataFinder.views.login;

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
import com.bdf.bookDataFinder.controller.listener.ILoginEventListener;
import com.bdf.bookDataFinder.controller.presenter.ILoginPresenter;
import com.bdf.bookDataFinder.controller.presenter.LoginPresenterImplt;
import com.bdf.bookDataFinder.controller.view.ILoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginFragment extends Fragment implements ILoginView {

    @BindView(R.id.edit_email)
    public EditText editEmail;
    @BindView(R.id.edit_password)
    public EditText editPassword;
    @BindView(R.id.chk_remember_me)
    public CheckBox chkRememberMe;
    @BindView(R.id.btn_login)
    public Button btnLogin;
    @BindView(R.id.btn_signup)
    public Button btnSignup;

    private ILoginPresenter loginPresenter;
    private ILoginEventListener loginEventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);

        if (loginPresenter == null) {
            loginPresenter = new LoginPresenterImplt(this);
        }
        initListeners();
        return root;
    }

    private void initListeners() {
        // ExpandableListView on child click listener
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        this.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginFragment.this.loginEventListener != null) {
                    LoginFragment.this.loginEventListener.onGotoSignup();
                }
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
            ViewUtils.showToast(getContext(), getResources().getString(R.string.message_login_succcess));
            LoginFragment.this.loginEventListener.onLoginSuccess();
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

    public void login() {
        if (validateData()) {
            makeLoginRequest();
        }
    }

    private void makeLoginRequest() {
        loginPresenter.login(editEmail.getText().toString(), editPassword.getText().toString());
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
        return true;
    }

    @Override
    public void setLoginEventListener(ILoginEventListener loginEventListener) {
        this.loginEventListener = loginEventListener;
    }
}
