package com.bdf.bookDataFinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.listview.ListAdapterListener;
import com.bdf.bookDataFinder.common.treeview.FileNodeBinder;
import com.bdf.bookDataFinder.common.treeview.bean.File;

import com.bdf.bookDataFinder.views.home.HomeFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tlh on 2016/10/1 :)
 */
public class SearchResultListViewAdapter extends RecyclerView.Adapter<SearchResultListViewAdapter.MyViewHolder> {

    private Context context;
    private List<File> fileList;
    private ListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView filename;
        public ImageView imgDownload;
        public ProgressBar progressDownload;

        public MyViewHolder(View view) {
            super(view);
            filename = view.findViewById(R.id.tv_filename);
            imgDownload = view.findViewById(R.id.iv_download);
            progressDownload = view.findViewById(R.id.prgsBarFileDownload);
            progressDownload.setVisibility(View.INVISIBLE);
            imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File fileDownload = fileList.get(getAdapterPosition());
                    Global.onFileDownloadEvent(MyViewHolder.this.itemView, fileDownload);
                    listener.onPdfDownload(fileDownload);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPdfSelected(fileList.get(getAdapterPosition()));
                }
            });
        }
    }

    public SearchResultListViewAdapter(Context context, List<File> fileList, ListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.fileList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_file, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final File file = fileList.get(position);

        long nFileId = file.fileId;
        boolean canDownloadServer = file.canDownload;
        int nStatus = Global.getPdfFileStatus(String.valueOf(nFileId));
        if(nStatus==Constants.STATUS_FILE_CAN_DOWNLOAD && canDownloadServer) {
            holder.imgDownload.setImageResource(R.drawable.ic_downloader);
        }
        else if(nStatus==Constants.STATUS_FILE_ALREADY_EXIST) {
            holder.imgDownload.setImageResource(R.drawable.ic_download_finish);
        }
        else {
            holder.imgDownload.setImageResource(0);
        }

        holder.filename.setText(file.fileName);
        Glide.with(context)
                .load(file.getLayoutId())
                .apply(RequestOptions.circleCropTransform());
    }

    public void setUpdateData(ArrayList<File> result) {
        fileList.clear();
        fileList.addAll(result);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
