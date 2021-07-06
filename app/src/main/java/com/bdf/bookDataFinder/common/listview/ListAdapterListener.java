package com.bdf.bookDataFinder.common.listview;

import com.bdf.bookDataFinder.common.treeview.bean.File;

public interface ListAdapterListener {
    void onPdfSelected(File file);
    void onPdfDownload(File file);
}