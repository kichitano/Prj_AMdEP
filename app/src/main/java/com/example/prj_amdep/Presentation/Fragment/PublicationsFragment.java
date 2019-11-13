package com.example.prj_amdep.Presentation.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prj_amdep.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PublicationsFragment extends Fragment implements View.OnClickListener {

    private Dialog dialogBuilder;
    private FloatingActionButton floatingActionButton;

    public PublicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //VARIABLES DECLARATION
        floatingActionButton = getView().findViewById(R.id.fabCreatePublication);

        //SETTING ACTIONS TO BUTTONS
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCreatePublication:
                showAlertDialog(R.layout.fragment_create_publication);
                break;
        }
    }

    private void showAlertDialog(int layout){
        dialogBuilder = new Dialog(getActivity());
        View layoutView = getLayoutInflater().inflate(layout, null);
        dialogBuilder.setContentView(layoutView);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.create();
        dialogBuilder.show();
    }
}
