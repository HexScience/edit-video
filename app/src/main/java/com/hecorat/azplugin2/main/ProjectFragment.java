package com.hecorat.azplugin2.main;

import android.os.AsyncTask;
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
import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.helper.NameDialog;
import com.hecorat.azplugin2.helper.Utils;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectFragment extends Fragment implements NameDialog.DialogClickListener {
    static MainActivity mActivity;
    public ImageView mBtnAddProject;
    public LinearLayout mLayoutScrollView;
    public View mPreviousSelectProject, mSelectProject;
    public Button mBtnOpen, mBtnDelete, mBtnRename;
    public LinearLayout mLayoutButton;

    public ProjectTable mProjectTable;
    public ArrayList<ProjectObject> mProjectList;

    public int mSelectProjectId;
    private int mCountVideo;

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

        mBtnRename = (Button) view.findViewById(R.id.btn_rename);
        mBtnRename.setOnClickListener(onBtnRenameClick);

        mLayoutScrollView = (LinearLayout) view.findViewById(R.id.layout_scrollview);

        mLayoutButton = (LinearLayout) view.findViewById(R.id.layout_button);

        mProjectTable = mActivity.mProjectTable;

        new LoadProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private class LoadProjectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mProjectList = mProjectTable.getData();
            mCountVideo = mProjectList.size();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            addProjectsToLayoutScrollView();
        }
    }

    View.OnClickListener onBtnRenameClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NameDialog dialog = NameDialog.newInstance(mActivity, NameDialog.RENAME, mSelectProjectName);
            dialog.setOnClickListener(ProjectFragment.this);
            dialog.show(mActivity.getSupportFragmentManager().beginTransaction(), "rename");
        }
    };

    private void addProjectsToLayoutScrollView() {
        for (ProjectObject project : mProjectList) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.project_item, null);
            view.setTag(project);
            view.setOnClickListener(onProjectItemClick);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            Glide.with(mActivity).load(project.firstVideo).into(imageView);

            TextView nameProject = (TextView) view.findViewById(R.id.name_project);
            nameProject.setText(project.name);

            mLayoutScrollView.addView(view);
            ((LinearLayout.LayoutParams) view.getLayoutParams()).rightMargin = Utils.dpToPixel(mActivity, 10);
        }
    }

    View.OnClickListener onBtnAddProjectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String nameProject = "Project_"+(mCountVideo+1);
            NameDialog dialog = NameDialog.newInstance(mActivity, NameDialog.CREATE_PROJECT, nameProject);
            dialog.setOnClickListener(ProjectFragment.this);
            dialog.show(mActivity.getSupportFragmentManager().beginTransaction(), "name project");
        }
    };

    @Override
    public void onPositiveClick(String name, int type) {
        switch (type) {
            case NameDialog.CREATE_PROJECT:
                createNewProject(name);
                break;
            case NameDialog.RENAME:
                renameProject(name);
                break;
        }
    }

    private void renameProject(String name) {
        mProjectTable.updateValue(mSelectProjectId, ProjectTable.NAME, name);
        mSelectProjectName = name;

        ProjectObject projectObject = (ProjectObject) mSelectProject.getTag();
        projectObject.name = name;
        mSelectProject.setTag(projectObject);

        TextView nameProject = (TextView) mSelectProject.findViewById(R.id.name_project);
        nameProject.setText(name);

        mActivity.hideStatusBar();
    }
    
    private void createNewProject(String name) {
        mActivity.setLayoutFragmentVisible(false);
        mActivity.mProjectName = name;
        long id = mActivity.mProjectTable.insertValue(mActivity.mProjectName,
                System.currentTimeMillis() + "");
        mActivity.mProjectId = (int) id;
        mActivity.resetActivity();
        mActivity.hideStatusBar();
        mActivity.addWaterMark();
    }

    @Override
    public void onNegativeClick() {
        mActivity.hideStatusBar();
    }

    View.OnClickListener onProjectItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ProjectObject project = (ProjectObject) view.getTag();
            mSelectProjectId = project.id;
            mSelectProjectName = project.name;
            mSelectProject = view;

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

    View.OnClickListener onBtnOpenClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.setLayoutFragmentVisible(false);
            mLayoutButton.setVisibility(View.INVISIBLE);
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
            mActivity.deleteAllObjects(mSelectProjectId);
            mLayoutScrollView.removeView(mPreviousSelectProject);

            mLayoutButton.setVisibility(View.INVISIBLE);

        }
    };

    private void log(String msg) {
        Log.e("Project Fragment", msg);
    }
}
