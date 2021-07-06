package com.bdf.bookDataFinder.common.treeview.bean;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.treeview.LayoutItemType;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class File implements LayoutItemType {

    public String fileName;
    public long fileId;
    public long categoryId;
    public boolean canDownload;

    public File(Long fileId, String fileName, long categoryId, boolean canDownload) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.categoryId = categoryId;
        this.canDownload = canDownload;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_book_file;
    }
}
