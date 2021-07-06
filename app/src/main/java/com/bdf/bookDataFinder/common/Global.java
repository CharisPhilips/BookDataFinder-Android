package com.bdf.bookDataFinder.common;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.apis.Api;
import com.bdf.bookDataFinder.common.datas.CategoryData;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.Pdfbook;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.downloader.Error;
import com.bdf.bookDataFinder.common.downloader.OnCancelListener;
import com.bdf.bookDataFinder.common.downloader.OnDownloadListener;
import com.bdf.bookDataFinder.common.downloader.OnPauseListener;
import com.bdf.bookDataFinder.common.downloader.OnProgressListener;
import com.bdf.bookDataFinder.common.downloader.OnStartOrResumeListener;
import com.bdf.bookDataFinder.common.downloader.PRDownloader;
import com.bdf.bookDataFinder.common.downloader.Progress;
import com.bdf.bookDataFinder.common.downloader.Status;
import com.bdf.bookDataFinder.common.treeview.bean.File;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.db.DBHelper;
import com.stripe.android.Stripe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class Global {

    public static int DISPLAY_HEIGHT;
    public static int DISPLAY_WIDTH;
    public static int STATUSBAR_HEGIHT;
    public static int ACTIONBAR_HEGIHT;
    public static String STRIPE_CLIENT_PUBLIC_KEY = null;

    public static Stripe g_stripe = null;
    //categories
    public static CategoryData g_category = null;
    //user
    public static UserProfile g_user = null;
    //network
    public static boolean IS_CONNECT_INTERNET = true;
    //categories
    public final static String FILE_CATEGORYJSON_NAME = "dataCategories.json";
    //pdf files
    public final static String PATH_PDF_DOWNLOAD = "myPdfFiles";

    public static int g_downloadId = 1;
    public static InputStream g_pdfInputStream = null;

    public static boolean isDownloadable() {
        if(!IS_CONNECT_INTERNET) {
            return false;
        }
        if(!isLogin()) {
            return false;
        }
        if(!Global.g_user.allowService) {
            return false;
        }
        return true;
    }

    public static boolean isLogin() {
        if(g_user!=null && g_user.email!=null && g_user.email.trim().length() > 0) {
            return true;
        }
        return false;
    }

    public static DBHelper getDBHelper(Context context) {
        return new DBHelper(context);
    }

    public static DBHelper getDBHelper() {
        return Application.s_Application.dbHelper;
    }

    public static Api getApi() { return Application.s_Application.api; }

    public static void onFileDownloadEvent(View view, File fileDownload) {

        java.io.File downloadFolder = StringUtils.getPdfFilePath(Application.s_Application, fileDownload.fileId);

        final TextView tvFilename = view.findViewById(R.id.tv_filename);
        final ImageView ivDownload = view.findViewById(R.id.iv_download);
        final ProgressBar prgsDownload = view.findViewById(R.id.prgsBarFileDownload);
        final TextView tvProgress = view.findViewById(R.id.tvProgress);

        prgsDownload.setVisibility(View.VISIBLE);
        if (Status.RUNNING == PRDownloader.getStatus(g_downloadId)) {
            PRDownloader.pause(g_downloadId);
            return;
        }

        if (Status.PAUSED == PRDownloader.getStatus(g_downloadId)) {
            PRDownloader.resume(g_downloadId);
            return;
        }

        String downloadUrl = Constants.BACKEND_URL + "bookDataFinder/api/download?" + "bookId=" + String.valueOf(fileDownload.fileId) + "&" + "userId=" + String.valueOf(g_user.id);
        g_downloadId = PRDownloader.download(downloadUrl, downloadFolder.getParent(), downloadFolder.getName())
            .build()
            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                @Override
                public void onStartOrResume() {
                    prgsDownload.setIndeterminate(false);
                    ivDownload.setEnabled(true);
                }
            })
            .setOnPauseListener(new OnPauseListener() {
                @Override
                public void onPause() {
//                        button.setText(R.string.resume);
                }
            })
            .setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel() {
                    prgsDownload.setProgress(0);
                    tvProgress.setText("");
                    g_downloadId = 0;
                    prgsDownload.setIndeterminate(false);
                }
            })
            .setOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(Progress progress) {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    prgsDownload.setProgress((int) progressPercent);
                    tvProgress.setText(progressPercent + "%");
                    prgsDownload.setIndeterminate(false);
                }
            })
            .start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    ivDownload.setEnabled(false);
                    tvProgress.setVisibility(View.INVISIBLE);
                    prgsDownload.setVisibility(View.INVISIBLE);
                    //write db
                    Pdfbook pdf = new Pdfbook();
                    pdf.id = String.valueOf(fileDownload.fileId);
                    pdf.filePath = downloadFolder.getPath();
                    pdf.displayName = fileDownload.fileName;
                    pdf.categoryid = fileDownload.categoryId;

                    getDBHelper().registerPdfFile(pdf, g_user.dbId);
                    ivDownload.setImageResource(R.drawable.ic_download_finish);
                }

                @Override
                public void onError(Error error) {
                    try {
                        JSONObject reader = new JSONObject(error.getServerErrorMessage());
                        if(reader.has("message")) {
                            String message = (String) reader.get("message");
                            ViewUtils.showToast(Application.s_Application, message);
                            tvProgress.setText("");
                        }
                        else {
                            if(error.getConnectionException()!=null && error.getConnectionException().getMessage()!=null) {
                                String message = error.getConnectionException().getMessage();
                                ViewUtils.showToast(Application.s_Application, message);
                                tvProgress.setText("");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    prgsDownload.setProgress(0);
                    g_downloadId = 0;
                    ivDownload.setEnabled(false);
                    prgsDownload.setIndeterminate(false);
                    tvProgress.setVisibility(View.INVISIBLE);
                    prgsDownload.setVisibility(View.INVISIBLE);
                }
            });
    }

    //0: disable, 1: can download, 2: finish download
    public static int getPdfFileStatus(String serverId) {
        if(g_user!=null) {
            Pdfbook pdfData = getDBHelper().getPdffileByServeridAndUserid(serverId, g_user.dbId);
            if(pdfData==null) {
                if(isDownloadable()) {
                    return Constants.STATUS_FILE_CAN_DOWNLOAD;
                }
                else {
                    return Constants.STATUS_FILE_CANNOT_DOWNLOAD;
                }
            }
            java.io.File file = new java.io.File(pdfData.filePath);
            if(file.exists()) {
                return Constants.STATUS_FILE_ALREADY_EXIST;
            }

        }
        return Constants.STATUS_FILE_CANNOT_DOWNLOAD;
    }

    public static void saveUserData(UserProfile user) {
        if(user==null) {
            g_user = null;
            Application.s_Application.writeSharedPreferenceEmailData(null, null);
            return;
        }
        else {
            UserProfile dbUser = getDBHelper().registerUser(user);
            if(dbUser!=null) {
                g_user = dbUser;
                g_user.allowService = user.allowService;
                g_user.servicedateStr = user.servicedateStr;
                Application.s_Application.writeSharedPreferenceEmailData(user.email, user.password);
                CreditCardData card = getDBHelper().getCreditcardDataByUserid(dbUser.dbId);
                if(card!=null) {
                    g_user.addCreditCard(card);
                }
            }
        }
    }
}


//     if(!bInitLayout) {
//        ButterKnife.bind(this);
//        int nPaddingPixels = ViewUtils.dpToPx(40 + 32);
//        int nGroupPixels = (int) (ViewUtils.dpToPx(16) + getResources().getDimension(R.dimen.larger_text_size));
//        layoutParent.setMinimumHeight(Global.DISPLAY_HEIGHT - (Global.ACTIONBAR_HEGIHT * 2 + nPaddingPixels + nGroupPixels));
//        initListeners();
//        bInitLayout = true;
//    }
