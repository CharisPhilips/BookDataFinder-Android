package com.bdf.bookDataFinder.views.home;

import androidx.lifecycle.ViewModel;

import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.CategoryData;
import com.bdf.bookDataFinder.common.treeview.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    public HomeViewModel() {
        initData();
    }
    public List<TreeNode> rootNodes = new ArrayList<>();
    private void initData() {
        if(Global.g_category!=null) {
            int nSubCateLenth = Global.g_category.category.size();
            for(int i = 0; i < nSubCateLenth; i++) {
                TreeNode subTreeNode = CategoryData.toTreeNode(Global.g_category.category.get(i));
                rootNodes.add(subTreeNode);
            }
        }
    }
}