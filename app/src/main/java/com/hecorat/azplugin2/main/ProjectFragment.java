package com.hecorat.azplugin2.main;

import android.content.Context;
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
import com.hecorat.azplugin2.dialogfragment.DialogConfirm;
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.NameDialog;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.interfaces.DialogClickListener;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectFragment extends Fragment implements NameDialog.DialogClickListener, DialogClickListener {
    MainActivity mActivity;
    public ImageView mBtnAddProject;
    public LinearLayout mLayoutScrollView;
    public View mPreviousSelectProject, mSelectProject;
    public Button mBtnOpen, mBtnDelete, mBtnRename;
    public LinearLayout mLayoutButton;
    public View mView;

    public ProjectTable mProjectTable;
    public ArrayList<ProjectObject> mProjectList;

    public int mSelectProjectId;
    private int mMaxIndex;

    public String mSelectProjectName;

    public static ProjectFragment newInstance(MainActivity activity) {
        ProjectFragment projectFragment = new ProjectFragment();
        projectFragment.mActivity = activity;
        projectFragment.inflateView();
        projectFragment.initFragmentArguments();
        return projectFragment;
    }

    private void inflateView() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.project_fragment, null);
        mBtnAddProject = (ImageView) mView.findViewById(R.id.btn_add_project);
        mBtnAddProject.setOnClickListener(onBtnAddProjectClick);

        mBtnOpen = (Button) mView.findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(onBtnOpenClick);

        mBtnDelete = (Button) mView.findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(onBtnDeleteClick);

        mBtnRename = (Button) mView.findViewById(R.id.btn_rename);
        mBtnRename.setOnClickListener(onBtnRenameClick);

        mLayoutScrollView = (LinearLayout) mView.findViewById(R.id.layout_scrollview);

        mLayoutButton = (LinearLayout) mView.findViewById(R.id.layout_button);
    }

    private void initFragmentArguments() {
        mProjectTable = mActivity.mProjectTable;
        new LoadProjectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log("onViewCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        log("onAttach");
    }

    private class LoadProjectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mProjectList = mProjectTable.getData();
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
            dialog.show(mActivity.getSupportFragmentManager(), "rename");
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
            findMaxIndex();
            String nameProject = "Project_"+(mMaxIndex +1);
            NameDialog dialog = NameDialog.newInstance(mActivity, NameDialog.CREATE_PROJECT, nameProject);
            dialog.setOnClickListener(ProjectFragment.this);
            dialog.show(mActivity.getSupportFragmentManager(), "name project");
        }
    };

    private void findMaxIndex() {
        if (mProjectList == null) {
            mMaxIndex = 0;
            return;
        }
        for (ProjectObject project : mProjectList) {
            String name = project.name;
            String[] listString = name.split("_");
            if (listString.length > 0) {
                String indexString = listString[listString.length - 1];
                try {
                    int index = Integer.parseInt(indexString);
                    if (mMaxIndex < index) {
                        mMaxIndex = index;
                    }
                } catch (NumberFormatException e) {
                    System.out.print(e + "");
                }
            }
        }
    }

    @Override
    public void onPositiveClick(int dialogId, String detail) {
        mActivity.hideStatusBar();
        switch (dialogId) {
            case DialogClickListener.DELETE_PROJECT:
                deleteProject();
                break;
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        mActivity.hideStatusBar();
    }

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

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_PROJECT, Constants.ACTION_RENAME_PROJECT);
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
        setLayoutProjectInvisible();

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_PROJECT, Constants.ACTION_NEW_PROJECT);
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
            setLayoutProjectInvisible();
            mLayoutButton.setVisibility(View.INVISIBLE);
            if (mActivity.mProjectId == mSelectProjectId) {
                return;
            }
            mActivity.resetActivity();
            mActivity.mProjectId = mSelectProjectId;
            mActivity.mProjectName = mSelectProjectName;
            mActivity.openProject();

            AnalyticsHelper.getInstance()
                    .send(mActivity, Constants.CATEGORY_PROJECT, Constants.ACTION_OPEN_PROJECT);
        }
    };

    private void setLayoutProjectInvisible() {
        View view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    View.OnClickListener onBtnDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogConfirm.newInstance(mActivity, ProjectFragment.this, DialogClickListener.DELETE_PROJECT, "")
                    .show(mActivity.getSupportFragmentManager(), "delete project");
        }
    };

    private void deleteProject() {
        mProjectTable.deleteProject(mSelectProjectId);
        mActivity.deleteAllObjects(mSelectProjectId);
        mLayoutScrollView.removeView(mPreviousSelectProject);
        mLayoutButton.setVisibility(View.INVISIBLE);

        AnalyticsHelper.getInstance()
                .send(mActivity, Constants.CATEGORY_PROJECT, Constants.ACTION_DELETE_PROJECT);
    }

    private void log(String msg) {
        Log.e("Project Fragment", msg);
    }
}
