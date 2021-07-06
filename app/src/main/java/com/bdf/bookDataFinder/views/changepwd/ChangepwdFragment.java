package com.bdf.bookDataFinder.views.changepwd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.controller.presenter.ChangepwdPresenterImplt;
import com.bdf.bookDataFinder.controller.presenter.IChangepwdPresenter;
import com.bdf.bookDataFinder.controller.view.IChangepwdView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangepwdFragment extends Fragment implements IChangepwdView {

    @BindView(R.id.edit_oldpassword)
    public EditText editOldpassword;
    @BindView(R.id.edit_password)
    public EditText editPassword;
    @BindView(R.id.edit_repassword)
    public EditText editRepassword;
    @BindView(R.id.chk_remember_me)
    public CheckBox chkRememberMe;
    @BindView(R.id.btn_changepassword)
    public Button   btnChangepassword ;
    @BindView(R.id.layout_parent)
    public LinearLayout layoutParent;

    private IChangepwdPresenter changepwdPresenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_changepassword, container, false);
        ButterKnife.bind(this, root);

        if (changepwdPresenter == null) {
            changepwdPresenter = new ChangepwdPresenterImplt(this);
        }
        initListeners();
        return root;
    }

    private void initListeners() {
        this.btnChangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    @Override
    public void onSuccess(UserProfile user) {
        if (user != null) {
            //change password success
            Global.saveUserData(user);
            ViewUtils.showToast(getContext(), getResources().getString(R.string.message_changepwd_succcess));
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

    private void changePassword() {
        if (validateData()) {
            makeChangepwdRequest();
        }
    }

    private boolean validateData() {

        String oldpass = editOldpassword.getText().toString();
        if(!oldpass.equals(Global.g_user.password)) {
            ViewUtils.showToast(getContext(), getContext().getString(R.string.message_enter_oldpassword));
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

    private void makeChangepwdRequest() {
        changepwdPresenter.changePassword(Global.g_user.email, editOldpassword.getText().toString(), editPassword.getText().toString());
    }

}
