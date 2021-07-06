package com.bdf.bookDataFinder.views.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.treeview.DirectoryNodeBinder;
import com.bdf.bookDataFinder.common.treeview.FileNodeBinder;
import com.bdf.bookDataFinder.common.treeview.IPdfFileEvent;
import com.bdf.bookDataFinder.common.treeview.bean.File;
import com.bdf.bookDataFinder.common.treeview.TreeNode;
import com.bdf.bookDataFinder.adapter.PdfDataTreeViewAdapter;
import com.bdf.bookDataFinder.controller.listener.IProgressListener;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recycleView;
    private PdfDataTreeViewAdapter adapter;
    private IPdfFileEvent iPdfFileSelectEvent = null;
    private IProgressListener iProgressListener = null;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(this.recycleView==null) {
            this.recycleView = (RecyclerView)(inflater.inflate(R.layout.fragment_home, container, false));
        }

        if(container.getContext() instanceof IPdfFileEvent) {
            this.iPdfFileSelectEvent = (IPdfFileEvent) container.getContext();
        }

        if(container.getContext() instanceof IProgressListener) {
            this.iProgressListener = (IProgressListener) container.getContext();
        }

        if(homeViewModel==null) {
            homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
            initData();
        }

        return recycleView;
    }

    private void initData() {
        this.adapter = new PdfDataTreeViewAdapter(this.homeViewModel.rootNodes, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()), this.iProgressListener);
        this.adapter.ifCollapseChildWhileCollapseParent(true);
        this.adapter.ifCollapseSibling(false);
        this.adapter.setOnTreeNodeListener(new PdfDataTreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) {
                    onToggle(!node.isExpand(), holder);
//                    if (!node.isExpand())
//                        adapter.collapseBrotherNode(node);
                }
                else if(node.isFile() && ((((File)node.getContent()).canDownload || Global.getPdfFileStatus(String.valueOf(((File) node.getContent()).fileId))==Constants.STATUS_FILE_ALREADY_EXIST) && HomeFragment.this.iPdfFileSelectEvent!=null)) {
                    HomeFragment.this.iPdfFileSelectEvent.onPdfFileSelect(((File)node.getContent()).fileId);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDownload(TreeNode node, RecyclerView.ViewHolder holder) {
                if(node.isFile() && HomeFragment.this.iPdfFileSelectEvent!=null) {
                    HomeFragment.this.iPdfFileSelectEvent.onPdfDownload(((File)node.getContent()).fileId, ((File)node.getContent()).fileName);
                    return true;
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : -90;
                ivArrow.animate().rotationBy(rotateDegree).start();
            }
        });

        this.recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.recycleView.setAdapter(this.adapter);
    }

    public void onDataUpdate() {
        this.adapter.notifyDataSetChanged();
    }
}