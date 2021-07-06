package com.bdf.bookDataFinder.adapter;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.Pdfbook;
import com.bdf.bookDataFinder.common.treeview.FileNodeBinder;
import com.bdf.bookDataFinder.common.treeview.TreeNode;
import com.bdf.bookDataFinder.common.treeview.TreeViewBinder;
import com.bdf.bookDataFinder.common.treeview.bean.Dir;
import com.bdf.bookDataFinder.common.treeview.bean.File;
import com.bdf.bookDataFinder.controller.listener.IProgressListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfDataTreeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String KEY_IS_EXPAND = "IS_EXPAND";

    private final List<? extends TreeViewBinder> viewBinders;
    private List<TreeNode> displayNodes;
    public static TreeNode selectedTVNode;
    private IProgressListener iProgressListener;
    private int padding = 30;
    private OnTreeNodeListener onTreeNodeListener;
    private boolean toCollapseChild;
    private boolean toCollapseSibling;

    public PdfDataTreeViewAdapter(List<TreeNode> nodes, List<? extends TreeViewBinder> viewBinders, IProgressListener progressListener) {
        super();
        this.iProgressListener = progressListener;
        displayNodes = new ArrayList<>();
        selectedTVNode = null;
        if (nodes != null)
            findDisplayNodes(nodes);
        this.viewBinders = viewBinders;
    }

    /**
     * @param nodes 基准点
     */
    private void findDisplayNodes(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            displayNodes.add(node);
            if (!node.isLeaf() && node.isExpand())
                findDisplayNodes(node.getChildList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayNodes.get(position).getContent().getLayoutId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinders.size() == 1)
                return viewBinders.get(0).provideViewHolder(v);
            if (viewBinder.getLayoutId() == viewType)
                return viewBinder.provideViewHolder(v);
        }
        return viewBinders.get(0).provideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            for (String key : b.keySet()) {
                switch (key) {
                    case KEY_IS_EXPAND:
                        if (onTreeNodeListener != null)
                            onTreeNodeListener.onToggle(b.getBoolean(key), holder);
                        break;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.itemView.setPaddingRelative(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        } else {
            holder.itemView.setPadding(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        }
        if (holder instanceof FileNodeBinder.ViewHolder) {

            TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
            long nFileId = ((File)selectedNode.getContent()).fileId;
            boolean canDownloadServer = ((File)selectedNode.getContent()).canDownload;
            int nStatus = Global.getPdfFileStatus(String.valueOf(nFileId));

            if(nStatus==Constants.STATUS_FILE_CAN_DOWNLOAD && canDownloadServer) {
                ((FileNodeBinder.ViewHolder) holder).imgDownload.setImageResource(R.drawable.ic_downloader);
            }
            else if(nStatus==Constants.STATUS_FILE_ALREADY_EXIST) {
                ((FileNodeBinder.ViewHolder) holder).imgDownload.setImageResource(R.drawable.ic_download_finish);
            }
            else {
                ((FileNodeBinder.ViewHolder) holder).imgDownload.setImageResource(0);
            }
            ((FileNodeBinder.ViewHolder) holder).imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
                    // Prevent multi-click during the short interval.
                    try {
                        long lastClickTime = (long) holder.itemView.getTag();
                        if (System.currentTimeMillis() - lastClickTime < 500)
                            return;
                    } catch (Exception e) {
                        holder.itemView.setTag(System.currentTimeMillis());
                    }
                    holder.itemView.setTag(System.currentTimeMillis());
                    Global.onFileDownloadEvent(((FileNodeBinder.ViewHolder) holder).itemView, ((File) selectedNode.getContent()));
                    if (onTreeNodeListener != null && onTreeNodeListener.onDownload(selectedNode, holder)) {
                        return;
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
                // Prevent multi-click during the short interval.
                try {
                    long lastClickTime = (long) holder.itemView.getTag();
                    if (System.currentTimeMillis() - lastClickTime < 500)
                        return;
                } catch (Exception e) {
                    holder.itemView.setTag(System.currentTimeMillis());
                }
                holder.itemView.setTag(System.currentTimeMillis());
                if (onTreeNodeListener != null && onTreeNodeListener.onClick(selectedNode, holder)) {
                    return;
                }
                if (selectedNode.isLeaf()) {
                    if(!selectedNode.haveChild()) {
                        if(!selectedNode.isFile() && ((Dir)selectedNode.getContent()).id > 0) {
                            Call<List<Pdfbook>> call = Global.getApi().getApiService().getBookByCategory(((Dir)selectedNode.getContent()).id);
                            if(PdfDataTreeViewAdapter.this.iProgressListener !=null) {
                                PdfDataTreeViewAdapter.this.iProgressListener.showProgress();
                            }

                            call.enqueue(new Callback<List<Pdfbook>>() {
                                @Override
                                public void onResponse(Call<List<Pdfbook>> call, Response<List<Pdfbook>> response) {
                                    System.out.println("success");
                                    List<Pdfbook> result = response.body();
                                    if(result!=null && result.size() > 0) {
                                        for(int i = 0; i < result.size(); i++) {
                                            Pdfbook book = result.get(i);
                                            selectedNode.addChild(new TreeNode(new File(Long.valueOf(book.id), book.bookname, book.categoryid, book.canDownload)));
                                        }
                                    }
                                    int positionStart = displayNodes.indexOf(selectedNode) + 1;
                                    addChildNodes(selectedNode, positionStart);
                                    notifyDataSetChanged();
                                    if(PdfDataTreeViewAdapter.this.iProgressListener !=null) {
                                        PdfDataTreeViewAdapter.this.iProgressListener.hideProgress();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Pdfbook>> call, Throwable error) {
                                    if(Global.g_user!=null) {
                                        List<Pdfbook> result = Global.getDBHelper().getPdffileByCategoryidAndUserid(((Dir)selectedNode.getContent()).id, Global.g_user.dbId);
                                        if(result!=null && result.size() > 0) {
                                            for(int i = 0; i < result.size(); i++) {
                                                Pdfbook book = result.get(i);
                                                selectedNode.addChild(new TreeNode(new File(Long.valueOf(book.id), book.bookname, book.categoryid, book.canDownload)));
                                            }
                                        }
                                        int positionStart = displayNodes.indexOf(selectedNode) + 1;
                                        addChildNodes(selectedNode, positionStart);
                                        notifyDataSetChanged();
                                    }
                                    if(PdfDataTreeViewAdapter.this.iProgressListener !=null) {
                                        PdfDataTreeViewAdapter.this.iProgressListener.hideProgress();
                                    }
                                }
                            });
                        }
                    }
                    else {
                        boolean isExpand = selectedNode.isExpand();
                        if (!isExpand) {
                            int positionStart = displayNodes.indexOf(selectedNode) + 1;
                            if (toCollapseSibling) {
                                addChildNodes(selectedNode, positionStart);
                                removeAllNodeExceptSelf(selectedNode, selectedTVNode, true);
                            } else {
                                addChildNodes(selectedNode, positionStart);
                            }
                        } else {
                            removeChildNodes(selectedNode, true);
                        }
                        notifyDataSetChanged();
                    }
                    return;
                }

                // This TreeNode was locked to click.
                if (selectedNode.isLocked()) return;

                boolean isExpand = selectedNode.isExpand();
                int positionStart = displayNodes.indexOf(selectedNode) + 1;
                if (!isExpand) {
                    if (toCollapseSibling) {
                        addChildNodes(selectedNode, positionStart);
                        removeAllNodeExceptSelf(selectedNode, selectedTVNode, true);
                        //notifyDataSetChanged();
                    } else {
                        addChildNodes(selectedNode, positionStart);
                        //notifyItemRangeInserted(positionStart, addChildNodes(selectedNode, positionStart));
                    }
                } else {
                    removeChildNodes(selectedNode, true);
                    //notifyItemRangeRemoved(positionStart, removeChildNodes(selectedNode, true));
                }
                if (selectedTVNode != null) {
                    selectedTVNode.deselect();
                }
                selectedNode.select();
                notifyDataSetChanged();
                selectedTVNode = selectedNode;
            }
        });
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == displayNodes.get(position).getContent().getLayoutId()) {
                viewBinder.bindView(holder, position, displayNodes.get(position));
            }
        }
    }

    private int addChildNodes(TreeNode pNode, int startIndex) {
        List<TreeNode> childList = pNode.getChildList();
        int addChildCount = 0;
        for (TreeNode treeNode : childList) {
            displayNodes.add(startIndex + addChildCount++, treeNode);
            if (treeNode.isExpand()) {
                addChildCount += addChildNodes(treeNode, startIndex + addChildCount);
            }
        }
        if (!pNode.isExpand()) {
            pNode.toggle();
        }
        return addChildCount;
    }

    private int removeChildNodes(TreeNode pNode) {
        return removeChildNodes(pNode, true);
    }

    private int removeChildNodes(TreeNode pNode, boolean shouldToggle) {
        if (pNode == null || !pNode.haveChild())
            return 0;
        List<TreeNode> childList = pNode.getChildList();
        int removeChildCount = childList.size();
        displayNodes.removeAll(childList);
        for (TreeNode child : childList) {
            if (child.isExpand()) {
                if (toCollapseChild)
                    child.toggle();
                removeChildCount += removeChildNodes(child, false);
            }
        }
        if (shouldToggle)
            pNode.toggle();
        return removeChildCount;
    }

    private boolean isAncestorNode(TreeNode pNode, TreeNode pExpectAncestorNode) {
        while (pNode != null) {
            if (pNode == pExpectAncestorNode) {
                return true;
            }
            pNode = pNode.getParent();
        }
        return false;
    }

    private int removeAllNodeExceptSelf(TreeNode pNode, TreeNode pOriginalNode, boolean shouldToggle) {
        if (pNode == null || !pNode.haveChild())
            return 0;
        if (pNode == null || pOriginalNode == null) {
            return 0;
        }
        if (isAncestorNode(pNode, pOriginalNode)) {
            return 0;
        }
        int removeChildCount = removeChildNodes(pOriginalNode);
        TreeNode parentNode = pNode.getParent();
        TreeNode parentOriginalNode = pOriginalNode.getParent();
        if (parentNode == null || parentOriginalNode == null) {
            return removeChildCount;
        }
        do {
            if (parentNode == parentOriginalNode) {
                break;
            }
            removeChildCount += removeChildNodes(parentOriginalNode);
            parentOriginalNode = parentOriginalNode.getParent();
        }
        while (parentNode != null && parentOriginalNode != null);
        return removeChildCount;
    }

    @Override
    public int getItemCount() {
        return displayNodes == null ? 0 : displayNodes.size();
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void ifCollapseChildWhileCollapseParent(boolean toCollapseChild) {
        this.toCollapseChild = toCollapseChild;
    }

    public void ifCollapseSibling(boolean toCollapseSibling) {
        this.toCollapseSibling = toCollapseSibling;
    }

    public void setOnTreeNodeListener(OnTreeNodeListener onTreeNodeListener) {
        this.onTreeNodeListener = onTreeNodeListener;
    }

    public interface OnTreeNodeListener {
        /**
         * called when TreeNodes were clicked.
         * @return weather consume the click event.
         */
        boolean onClick(TreeNode node, RecyclerView.ViewHolder holder);

        /**
         * called when TreeNodes were clicked.
         * @return weather consume the click event.
         */
        boolean onDownload(TreeNode node, RecyclerView.ViewHolder holder);

        /**
         * called when TreeNodes were toggle.
         * @param isExpand the status of TreeNodes after being toggled.
         */
        void onToggle(boolean isExpand, RecyclerView.ViewHolder holder);
    }

    public void refresh(List<TreeNode> treeNodes) {
        displayNodes.clear();
        findDisplayNodes(treeNodes);
        notifyDataSetChanged();
    }

    public Iterator<TreeNode> getDisplayNodesIterator() {
        return displayNodes.iterator();
    }

    private void notifyDiff(final List<TreeNode> temp) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return temp.size();
            }

            @Override
            public int getNewListSize() {
                return displayNodes.size();
            }

            // judge if the same items
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return PdfDataTreeViewAdapter.this.areItemsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            // if they are the same items, whether the contents has bean changed.
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return PdfDataTreeViewAdapter.this.areContentsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return PdfDataTreeViewAdapter.this.getChangePayload(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    private Object getChangePayload(TreeNode oldNode, TreeNode newNode) {
        Bundle diffBundle = new Bundle();
        if (newNode.isExpand() != oldNode.isExpand()) {
            diffBundle.putBoolean(KEY_IS_EXPAND, newNode.isExpand());
        }
        if (diffBundle.size() == 0)
            return null;
        return diffBundle;
    }

    // For DiffUtil, if they are the same items, whether the contents has bean changed.
    private boolean areContentsTheSame(TreeNode oldNode, TreeNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent())
                && oldNode.isExpand() == newNode.isExpand();
    }

    // judge if the same item for DiffUtil
    private boolean areItemsTheSame(TreeNode oldNode, TreeNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent());
    }

    /**
     * collapse all root nodes.
     */
    public void collapseAll() {
        // Back up the nodes are displaying.
        List<TreeNode> temp = backupDisplayNodes();
        //find all root nodes.
        List<TreeNode> roots = new ArrayList<>();
        for (TreeNode displayNode : displayNodes) {
            if (displayNode.isRoot())
                roots.add(displayNode);
        }
        //Close all root nodes.
        for (TreeNode root : roots) {
            if (root.isExpand())
                removeChildNodes(root);
        }
        notifyDiff(temp);
    }

    @NonNull
    private List<TreeNode> backupDisplayNodes() {
        List<TreeNode> temp = new ArrayList<>();
        for (TreeNode displayNode : displayNodes) {
            try {
                temp.add(displayNode.clone());
            } catch (CloneNotSupportedException e) {
                temp.add(displayNode);
            }
        }
        return temp;
    }

    public void collapseNode(TreeNode pNode) {
        List<TreeNode> temp = backupDisplayNodes();
        removeChildNodes(pNode);
        notifyDiff(temp);
    }

    public void collapseBrotherNode(TreeNode pNode) {
        List<TreeNode> temp = backupDisplayNodes();
        if (pNode.isRoot()) {
            List<TreeNode> roots = new ArrayList<>();
            for (TreeNode displayNode : displayNodes) {
                if (displayNode.isRoot())
                    roots.add(displayNode);
            }
            //Close all root nodes.
            for (TreeNode root : roots) {
                if (root.isExpand() && !root.equals(pNode))
                    removeChildNodes(root);
            }
        } else {
            TreeNode parent = pNode.getParent();
            if (parent == null)
                return;
            List<TreeNode> childList = parent.getChildList();
            for (TreeNode node : childList) {
                if (node.equals(pNode) || !node.isExpand())
                    continue;
                removeChildNodes(node);
            }
        }
        notifyDiff(temp);
    }

}
