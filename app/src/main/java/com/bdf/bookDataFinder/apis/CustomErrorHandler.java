package com.bdf.bookDataFinder.apis;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.bdf.bookDataFinder.R;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

//import retrofit.ErrorHandler;
//import retrofit.RetrofitError;

public class CustomErrorHandler{

}
//public class CustomErrorHandler implements ErrorHandler {
//    private final Context ctx;
//    private final String TAG = "CustomErrorHandler.java";
//
//    public CustomErrorHandler(Context ctx) {
//        this.ctx = ctx;
//    }
//
//    @SuppressLint("StringFormatInvalid")
//    @Override
//    public Throwable handleError(RetrofitError cause) {
//        String errorDescription;
//
//        if (cause.getKind().equals(RetrofitError.Kind.NETWORK)) {
//            errorDescription = ctx.getString(R.string.error_network);
//        } else {
//            if (cause.getResponse() == null) {
//                errorDescription = ctx.getString(R.string.error_no_response);
//            } else {
//                try {
//                    PostResponse errorResponse = (PostResponse) cause.getBodyAs(PostResponse.class);
//                    errorDescription = errorResponse.getError();
//                } catch (Exception ex) {
//                    try {
//                        errorDescription = ctx.getString(R.string.error_network_http_error, cause.getResponse().getStatus());
//                    } catch (Exception ex2) {
//                        Log.e(TAG, "handleError: " + ex2.getLocalizedMessage());
//                        errorDescription = ctx.getString(R.string.error_unknown);
//                    }
//                }
//            }
//        }
//        return new Exception(errorDescription);
//    }
//
//    public class Error {
//        @Expose
//        private List<String> errors = new ArrayList<String>();
//
//        public List<String> getErrors() {
//            return errors;
//        }
//
//        public void setErrors(List<String> errors) {
//            this.errors = errors;
//        }
//
//    }
//
//}
//
