package com.bdf.bookDataFinder.common.downloader.internal;

import com.bdf.bookDataFinder.common.downloader.Response;
import com.bdf.bookDataFinder.common.downloader.request.DownloadRequest;

public class SynchronousCall {

    public final DownloadRequest request;

    public SynchronousCall(DownloadRequest request) {
        this.request = request;
    }

    public Response execute() {
        DownloadTask downloadTask = DownloadTask.create(request);
        return downloadTask.run();
    }

}
