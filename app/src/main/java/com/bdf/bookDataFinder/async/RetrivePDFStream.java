package com.bdf.bookDataFinder.async;

import android.content.Context;
import android.os.AsyncTask;

import com.bdf.bookDataFinder.adapter.PdfDataTreeViewAdapter;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.controller.listener.IProgressListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrivePDFStream extends AsyncTask<String, Void, Boolean> {

    private PDFView pdfView;
    private OnPageChangeListener pageChangeListener;
    private OnLoadCompleteListener onLoadCompleteListener;
    private OnPageErrorListener onPageErrorListener;
    private IProgressListener iProgressListener;
    private Context context;

    public RetrivePDFStream(PDFView pdfView, OnPageChangeListener pageChangeListener, OnLoadCompleteListener onLoadCompleteListener, OnPageErrorListener onPageErrorListener, IProgressListener iProgressListener, Context context)
    {
        this.pdfView = pdfView;
        this.pageChangeListener = pageChangeListener;
        this.onLoadCompleteListener = onLoadCompleteListener;
        this.onPageErrorListener = onPageErrorListener;
        this.iProgressListener = iProgressListener;
        this.context = context;
    }

    protected InputStream doInBackground() {
        return doInBackground();
    }

    protected Boolean doInBackground(String... params) {

        if(Global.g_pdfInputStream!=null) {
            try {
                Global.g_pdfInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Global.g_pdfInputStream = null;
        }

        try {

            URL uri = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                Global.g_pdfInputStream = new BufferedInputStream(urlConnection.getInputStream());
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        if(result) {
            this.pdfView.fromStream(Global.g_pdfInputStream).defaultPage(0)
                //this.pdfView.fromUri(Uri.parse(Global.BACKEND_URL + "/bookDataFinder/api/download/1")).defaultPage(0)
                .onPageChange(this.pageChangeListener)
                .enableAnnotationRendering(true)
                .onLoad(this.onLoadCompleteListener)
                .scrollHandle(new DefaultScrollHandle(context))
                .spacing(10) // in dp
                .onPageError(this.onPageErrorListener)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
        }

    }
}
