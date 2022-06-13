package com.mobile_application.ui.NucleicSelect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NucleicSelectViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public NucleicSelectViewModel(MutableLiveData<String> mText) {
        this.mText = mText;
    }
    public LiveData<String> getText() {
        return mText;
    }
}