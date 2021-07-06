package com.bdf.bookDataFinder.common.treeview.bean;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.treeview.LayoutItemType;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class Dir implements LayoutItemType {
    public String dirName;
    public long id;
    public Dir(String dirName, long id) {
        this.dirName = dirName;
        this.id = id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_book_dir;
    }
}
