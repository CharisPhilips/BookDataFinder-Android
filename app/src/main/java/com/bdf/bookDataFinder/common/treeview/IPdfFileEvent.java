package com.bdf.bookDataFinder.common.treeview;

public interface IPdfFileEvent {
    public boolean onPdfFileSelect(long nPdfFileId);
    public void onPdfDownload(long nPdfFileId, String strPdfName);
}
