package com.hecorat.editvideo.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hecorat.editvideo.R;
import com.hecorat.editvideo.database.ProjectObject;
import com.hecorat.editvideo.database.ProjectTable;
import com.hecorat.editvideo.helper.Utils;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectFragment extends Fragment{
    static MainActivity mActivity;
    public ImageView mBtnAddProject;
    public LinearLayout mLayoutScrollView;
    public View mPreviousSelectProject;
    public Button mBtnOpen, mBtnDelete;
    public LinearLayout mLayoutButton;

    public ProjectTable mProjectTable;
    public ArrayList<ProjectObject> mProjectList;

    public int mSelectProjectId;

    public String mSelectProjectName;

    public static ProjectFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new ProjectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_fragment, null);
        mBtnAddProject = (ImageView) view.findViewById(R.id.btn_add_project);
        mBtnAddProject.setOnClickListener(onBtnAddProjectClick);

        mBtnOpen = (Button) view.findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(onBtnOpenClick);

        mBtnDelete = (Button) view.findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);

        mLayoutScrollView = (LinearLayout) view.findViewById(R.id.layout_scrollview);

        mLayoutButton = (LinearLayout) view.findViewById(R.id.layout_button);

        mProjectTable = mActivity.mProjectTable;
        mProjectList = mProjectTable.getData();
        addProjectsToLayoutScrollView();
        return view;
    }

    private void addProjectsToLayoutScrollView(){
        for (ProjectObject project : mProjectList) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.project_item, null);
            view.setTag(project);
            view.setOnClickListener(onProjectItemClick);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            Glide.with(mActivity).load(project.firstVideo).into(imageView);

            TextView nameProject = (TextView) view.findViewById(R.id.name_project);
            nameProject.setText(project.name);

            mLayoutScrollView.addView(view);
            ((LinearLayout.LayoutParams)view.getLayoutParams()).rightMargin = Utils.dpToPixel(mActivity, 10);
        }
    }

    View.OnClickListener onBtnAddProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NameProjectDialog.newInstance(mActivity)
                    .show(mActivity.getSupportFragmentManager().beginTransaction(),"name project");
        }
    };

    View.OnClickListener onProjectItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ProjectObject project = (ProjectObject) view.getTag();
            mSelectProjectId = project.id;
            mSelectProjectName = project.name;

            mLayoutButton.setVisibility(View.VISIBLE);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            imageView.setBackgroundResource(R.drawable.bgr_project_item_select);

            if (mPreviousSelectProject != null) {
                if (!mPreviousSelectProject.equals(view)) {
                    imageView = (ImageView) mPreviousSelectProject.findViewById(R.id.image_view);
                    imageView.setBackgroundResource(R.drawable.bgr_project_item_normal);
                }
            }

            mPreviousSelectProject = view;
        }
    };

    View.OnClickListener onBtnOpenClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            mActivity.setLayoutFragmentVisible(false);
            if (mActivity.mProjectId == mSelectProjectId) {
                return;
            }
            mActivity.resetActivity();
            mActivity.mProjectId = mSelectProjectId;
            mActivity.mProjectName = mSelectProjectName;
            mActivity.openProject();
        }
    };

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mProjectTable.deleteProject(mSelectProjectId);
            mActivity.deleteProject(mSelectProjectId);
            mLayoutScrollView.removeView(mPreviousSelectProject);
        }
    };

    private void log(String msg) {
        Log.e("Project Fragment", msg);
    }
}
