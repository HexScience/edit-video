package com.hecorat.azplugin2.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.helper.RecyclerViewItemDivider;

import java.util.ArrayList;

/**
 * Created by macos on 22/02/2017.
 */
@SuppressWarnings("unused")
public class ProjectListDialog extends DialogFragment {
    private static final String KEY = "current_project_id";

    private Activity mActivity;
    private ProjectTable mProjectTable;
    private int mCurrentProjectId;
    private RecyclerView recyclerView;
    private ArrayList<ProjectObject> mProjectList= new ArrayList<>();

    public static ProjectListDialog newInstance(int currentProjectId) {
        ProjectListDialog instance = new ProjectListDialog();
        Bundle args = new Bundle();
        args.putInt(KEY, currentProjectId);
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mProjectTable = new ProjectTable(mActivity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurrentProjectId = getArguments().getInt(KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View attachView = mActivity.getLayoutInflater().inflate(R.layout.fragment_project_list, null);
        recyclerView = (RecyclerView) attachView.findViewById(R.id.project_list);
        recyclerView.addItemDecoration(new RecyclerViewItemDivider(mActivity,
                LinearLayoutManager.VERTICAL));

        loadRecentProjectsFromDb();

        builder.setTitle(R.string.recent_project_dialog_title)
                .setView(attachView)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }

    private void loadRecentProjectsFromDb() {
        CursorLoader cursorLoader = new CursorLoader(mActivity) {
            @Override
            public Cursor loadInBackground() {
                return mProjectTable.queryAllRecentProject();
            }

            @Override
            public void deliverResult(Cursor cursor) {
                super.deliverResult(cursor);
                mProjectList = mProjectTable.getRecentProjectsFromCursor(cursor);
                if (mProjectList.size() > 0) {
                    recyclerView.setAdapter(new ProjectAdapter(mActivity,
                            mProjectTable, mCurrentProjectId));
                }
            }
        };
        cursorLoader.startLoading();
    }
}
