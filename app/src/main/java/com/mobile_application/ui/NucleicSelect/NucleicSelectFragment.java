package com.mobile_application.ui.NucleicSelect;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile_application.R;
import com.mobile_application.databinding.NucleicSelectFragmentBinding;

public class NucleicSelectFragment extends Fragment {

    private NucleicSelectFragmentBinding binding;

    public static NucleicSelectFragment newInstance() {
        return new NucleicSelectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        NucleicSelectViewModel nucleicSelectViewModel =
                new ViewModelProvider(this).get(NucleicSelectViewModel.class);
        return inflater.inflate(R.layout.nucleic_select_fragment2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NucleicSelectViewModel nucleicSelectViewModel = new ViewModelProvider(this).get(NucleicSelectViewModel.class);
        // TODO: Use the ViewModel
    }

}