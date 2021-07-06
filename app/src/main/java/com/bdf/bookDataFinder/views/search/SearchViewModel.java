package com.bdf.bookDataFinder.views.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<String> mSearchText;

    public SearchViewModel() {
        mSearchText = new MutableLiveData<>();
    }

    public LiveData<String> getSearchText() {
        return mSearchText;
    }

    public List<String> getTextList() {
        return null;
    }
}