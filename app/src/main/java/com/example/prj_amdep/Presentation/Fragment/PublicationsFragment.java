package com.example.prj_amdep.Presentation.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prj_amdep.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

public class PublicationsFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 1;
    private Dialog dialogBuilder;
    private FloatingActionButton floatingActionButton;
    private Button btnAddPhotosVideos;

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
            case R.id.btnAddPhotos:
                Matisse.from(getActivity())
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(9)
                        .gridExpectedSize(360)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .showPreview(false) // Default is `true`
                        .theme(R.style.Matisse_Dracula)
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
        }
    }

    private void showAlertDialog(int layout){
        dialogBuilder = new Dialog(getActivity());
        View layoutView = getLayoutInflater().inflate(layout, null);
        dialogBuilder.setContentView(layoutView);
        btnAddPhotosVideos = layoutView.findViewById(R.id.btnAddPhotos);
        dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBuilder.create();
        dialogBuilder.show();
        btnAddPhotosVideos.setOnClickListener(this);
    }
}
