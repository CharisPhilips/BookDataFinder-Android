package com.bdf.bookDataFinder.views.search;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.adapter.SearchResultListViewAdapter;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.Pdfbook;
import com.bdf.bookDataFinder.common.listview.ListAdapterListener;
import com.bdf.bookDataFinder.common.listview.MyDividerItemDecoration;
import com.bdf.bookDataFinder.common.treeview.IPdfFileEvent;
import com.bdf.bookDataFinder.common.treeview.bean.File;
import com.bdf.bookDataFinder.controller.listener.IProgressListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment implements View.OnClickListener, ListAdapterListener {

    private static final String TAG = "SearchFragment";

    private SearchViewModel searchViewModel;
    //    private TagsEditText tagsEditText;
    private EditText searchEditText;
    private SearchResultListViewAdapter searchAdapter;
    private RecyclerView recyclerView;
    private IPdfFileEvent iPdfFileSelectEvent = null;
    private IProgressListener iProgressListener = null;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList item = new ArrayList();

        this.searchAdapter = new SearchResultListViewAdapter(this.getContext(), item, this);
        this.recyclerView = root.findViewById(R.id.listSearchResult);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(searchAdapter);

//        this.tagsEditText = root.findViewById(R.dbid.tags_search);
//        this.tagsEditText.setHint("Input tag");
//        this.tagsEditText.setTagsListener(this);
//        this.tagsEditText.setAdapter(new ArrayAdapter(root.getContext(), android.R.DialogsBox.simple_dropdown_item_1line, getResources().getStringArray(R.array.tags)));
//        this.tagsEditText.setThreshold(1);

        this.searchEditText = root.findViewById(R.id.edit_search);

        fetchContacts();
        setEventListener();

        if (container.getContext() instanceof IPdfFileEvent) {
            this.iPdfFileSelectEvent = (IPdfFileEvent) container.getContext();
        }
        if (container.getContext() instanceof IProgressListener) {
            this.iProgressListener = (IProgressListener) container.getContext();
        }
        return root;
    }

    private void setEventListener() {

        searchViewModel.getSearchText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                SearchFragment.this.searchEditText.setText(s);
            }
        });

        this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        SearchFragment.this.OnSearchText();
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.dbid.tags_search: {
//                break;
//            }
//        }
    }

    private void fetchContacts() {

    }

    private void OnSearchText() {

        String strTitle = searchEditText.getText().toString();
        HashMap hashMap = new HashMap();
        hashMap.put("title", strTitle);
        Call<List<Pdfbook>> call = Global.getApi().getApiService().getBookByTitle(hashMap);

        if(this.iProgressListener!=null) {
            this.iProgressListener.showProgress();
        }
        call.enqueue(new Callback<List<Pdfbook>>() {
            @Override
            public void onResponse(Call<List<Pdfbook>> call, Response<List<Pdfbook>> response) {
                List<Pdfbook> result = response.body();
                if (result == null) {
                    return;
                }
                int nCount = result.size();
                //searchAdapter.clear();
                ArrayList<File> resultFile = new ArrayList<File>();
                for (int i = 0; i < nCount; i++) {
                    Pdfbook book = result.get(i);
                    File file = new File(Long.parseLong(book.id), book.bookname, book.categoryid, book.canDownload);
                    resultFile.add(file);
                }
                searchAdapter.setUpdateData(resultFile);
                if(SearchFragment.this.iProgressListener!=null) {
                    SearchFragment.this.iProgressListener.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<List<Pdfbook>> call, Throwable error) {
                if(SearchFragment.this.iProgressListener!=null) {
                    SearchFragment.this.iProgressListener.hideProgress();
                }
            }
        });
    }

    @Override
    public void onPdfSelected(File file) {//else if(node.isFile() && ((((File)node.getContent()).canDownload || Global.getPdfFileStatus(String.valueOf(((File) node.getContent()).fileId))==Constants.STATUS_FILE_ALREADY_EXIST) && HomeFragment.this.iPdfFileSelectEvent!=null)) {
        if (SearchFragment.this.iPdfFileSelectEvent != null && (file.canDownload || Global.getPdfFileStatus(String.valueOf(file.fileId))== Constants.STATUS_FILE_ALREADY_EXIST)) {
            SearchFragment.this.iPdfFileSelectEvent.onPdfFileSelect(file.fileId);
        }
    }

    @Override
    public void onPdfDownload(File file) {
        if (SearchFragment.this.iPdfFileSelectEvent != null) {
            SearchFragment.this.iPdfFileSelectEvent.onPdfDownload(file.fileId, file.fileName);
        }
    }
}