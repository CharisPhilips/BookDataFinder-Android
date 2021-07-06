package com.bdf.bookDataFinder.common.treeview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.treeview.bean.Dir;

public class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {

        holder.ivArrow.setRotation(0);
        holder.ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
        int rotateDegree = node.isExpand() ? 90 : 0;
        holder.ivArrow.setRotation(rotateDegree);
        Dir dirNode = (Dir) node.getContent();
        holder.tvName.setText(dirNode.dirName);
        if (node.isLeaf())
            holder.ivArrow.setVisibility(View.INVISIBLE);
        else holder.ivArrow.setVisibility(View.VISIBLE);

        if (node.isSelect()) {
            holder.itemView.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.shape_round_folder));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorBackground));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_book_dir;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private ImageView ivArrow;
        private TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_filename);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }

        public TextView getTvName() {
            return tvName;
        }
    }
}
