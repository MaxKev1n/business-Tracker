package com.mobile_application.ui.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class FirstViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public FirstViewModel(MutableLiveData<String> mText) {
        this.mText = mText;
    }
}