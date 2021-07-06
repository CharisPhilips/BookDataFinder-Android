package com.bdf.bookDataFinder.common.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.downloader.Error;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import retrofit2.Response;

public class StringUtils {

    public static String getRootDirPath(Context context) {
        return context.getApplicationContext().getFilesDir().getAbsolutePath();
    }

    public static File getCategoryFilePath(Context context) {
        String strPath = getRootPdfDirPath(context).getPath();
        File rootPdfPath = new File(strPath + File.separator + Global.FILE_CATEGORYJSON_NAME);
        return rootPdfPath;
    }

    public static File getRootPdfDirPath(Context context) {
        String strPath = getRootDirPath(context);
        File rootPdfPath = new File(strPath + File.separator + Global.PATH_PDF_DOWNLOAD);
        return rootPdfPath;
    }

    public static File getPdfFilePath(Context context, long nId) {
        int n10_10 = (int) (((long) (nId / 10000000000L)) % 100);
        int n10_8 = (int) (((long) (nId / 100000000L)) % 100);
        int n10_6 = (int) (((long) (nId / 1000000L)) % 100);
        int n10_4 = (int) (((long) (nId / 10000L)) % 100);
        int n10_2 = (int) (((long) (nId / 100L)) % 100);

        String folderPath = getRootPdfDirPath(context)
                + File.separator + String.valueOf(n10_10)
                + File.separator + String.valueOf(n10_8)
                + File.separator + String.valueOf(n10_6)
                + File.separator + String.valueOf(n10_4)
                + File.separator + String.valueOf(n10_2);

        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folder, String.valueOf(nId) + ".pdf");
        return file;
    }

    public static String getExternalStoragePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];
            return file.getAbsolutePath();
        } else {
           return null;
        }
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes){
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static ErrorResponse getErrorResponse(Response response) throws Exception {
        try {
            String strErrorMessage = response.errorBody().source().readString(Charset.forName("utf-8"));
            Gson gson = new Gson();
            return gson.fromJson(strErrorMessage, ErrorResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
