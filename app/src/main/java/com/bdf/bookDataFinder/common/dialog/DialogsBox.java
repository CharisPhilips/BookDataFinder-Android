package com.bdf.bookDataFinder.common.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bdf.bookDataFinder.R;

public class DialogsBox extends Dialog {

    private Activity activity;
    private String url;
    private ImageView imageView, closeDialog;
    private ProgressBar progressBar;

    public DialogsBox(Activity activity, String url) {
        super(activity);
        this.activity = activity;
        this.url = url;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindow() != null)
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

    }
}
