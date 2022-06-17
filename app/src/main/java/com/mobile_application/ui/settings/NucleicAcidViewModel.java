package com.mobile_application.ui.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NucleicAcidViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public NucleicAcidViewModel(MutableLiveData<String> mText) {
        this.mText = mText;
    }
}