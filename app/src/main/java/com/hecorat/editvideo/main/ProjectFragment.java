package com.hecorat.editvideo.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.database.AudioTable;
import com.hecorat.editvideo.database.ImageObject;
import com.hecorat.editvideo.database.ImageTable;
import com.hecorat.editvideo.database.ProjectTable;
import com.hecorat.editvideo.database.TextObject;
import com.hecorat.editvideo.database.TextTable;
import com.hecorat.editvideo.database.VideoTable;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectFragment extends Fragment{
    static MainActivity mActivity;
    public Button mBtnAddProject, mBtnOpenProject;
    ProjectTable projectTable;
    VideoTable videoTable;
    AudioTable audioTable;
    ImageTable imageTable;
    TextTable textTable;

    public static ProjectFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new ProjectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_fragment, null);
        mBtnAddProject = (Button) view.findViewById(R.id.btn_add_project);
        mBtnOpenProject = (Button) view.findViewById(R.id.btn_open_project);
        mBtnAddProject.setOnClickListener(onBtnAddProjectClick);
        mBtnOpenProject.setOnClickListener(onBtnOpenProjectClick);
        projectTable = new ProjectTable(mActivity);
        videoTable = new VideoTable(mActivity);
        audioTable = new AudioTable(mActivity);
        imageTable = new ImageTable(mActivity);
        textTable = new TextTable(mActivity);
        return view;
    }

    View.OnClickListener onBtnAddProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            imageTable.dropTable();
            imageTable.createTable();
            TextObject text = new TextObject(1, "bha/fgd", "0", "100", "1",
                    "2", "3", "100", "100", "2", "30", "size", "fontPath", "fontColor", "boxColor");
            textTable.insertValue(text);
            text = new TextObject(1, "gfdagfdaf", "0", "100", "1",
                    "2", "3", "100", "100", "2", "30", "size", "fontPath", "fontColor", "boxColor");
            textTable.insertValue(text);
        }
    };

    View.OnClickListener onBtnOpenProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<TextObject> list = textTable.getData(1);
            for (TextObject text : list) {
                String msg = text.id +", "+text.projectId+", "+text.path+", "+text.left
                        +", "+text.right+", "+text.inLayoutImage+", "+text.orderInLayout
                        +", "+text.orderInList+", "+text.x+", "+text.y+", "+text.scale+", "
                        + text.rotation+", "+text.size+", "+text.fontPath+", "+text.fontColor
                        + ", "+text.boxColor;
                log(msg);
            }
        }
    };

    private void log(String msg) {
        Log.e("Project Fragment", msg);
    }
}
