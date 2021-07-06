package com.bdf.bookDataFinder.common.treeview;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.adapter.PdfDataTreeViewAdapter;
import com.bdf.bookDataFinder.common.treeview.bean.File;

public class FileNodeBinder extends TreeViewBinder<FileNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        File fileNode = (File) node.getContent();
        holder.tvName.setText(fileNode.fileName);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_book_file;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {

        public TextView tvName;
        public ImageView imgDownload;
        public ProgressBar prgsDownload;
        private PdfDataTreeViewAdapter.OnTreeNodeListener iTreeNodeEvent = null;

        public ViewHolder(View rootView) {
            super(rootView);
            this.tvName = rootView.findViewById(R.id.tv_filename);
            this.imgDownload = rootView.findViewById(R.id.iv_download);

            this.prgsDownload = rootView.findViewById(R.id.prgsBarFileDownload);
            this.prgsDownload.setVisibility(View.INVISIBLE);
        }
    }
}
