package com.hecorat.azplugin2.main;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;

import java.util.ArrayList;

/**
 * Created by macos on 09/03/2017.
 */

public class ProjectFragment extends Fragment implements View.OnClickListener {

    private static final String KEY = "current_project_id";
    private static final String TAG = "ProjectFragment";

    private ProjectTable mProjectTable;
    private ArrayList<ProjectObject> mProjectList = new ArrayList<>();
    private ProjectAdapter mAdapter;
    private Activity mActivity;

    private RecyclerView mRecyclerView;
    private TextView mTxtEmptyMessage;

    private Callback mCallback;

    public static ProjectFragment newInstance(int currentProjectId) {
        ProjectFragment instance = new ProjectFragment();
        Bundle args = new Bundle();
        args.putInt(KEY, currentProjectId);
        instance.setArguments(args);
        return instance;
    }

    interface Callback {
        void onBackButtonClicked();

        void onNewProjectButtonClicked();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mCallback = (Callback) getActivity();
        int currentProjectId = getArguments().getInt(KEY);
        View attachView = inflater.inflate(R.layout.fragment_project_list, container, false);
        mRecyclerView = (RecyclerView) attachView.findViewById(R.id.project_list);
        mProjectTable = new ProjectTable(mActivity);
        mAdapter = new ProjectAdapter(mActivity, mProjectTable, currentProjectId);
        mRecyclerView.setAdapter(mAdapter);

        attachView.findViewById(R.id.btn_go_back).setOnClickListener(this);
        attachView.findViewById(R.id.btn_add_project).setOnClickListener(this);
        attachView.findViewById(R.id.btn_menu_more).setOnClickListener(this);
        mTxtEmptyMessage = (TextView) attachView.findViewById(R.id.txt_empty_message);
        Log.v(TAG, "onCreateView");
        return attachView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated");
        loadRecentProjectsFromDb();
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadRecentProjectsFromDb() {
        CursorLoader cursorLoader = new CursorLoader(mActivity) {
            @Override
            public Cursor loadInBackground() {
                Log.v(TAG, "loadInBackground");
                return mProjectTable.queryAllRecentProject();
            }

            @Override
            public void deliverResult(Cursor cursor) {
                super.deliverResult(cursor);
                Log.v(TAG, "deliverResult");
                mProjectList = mProjectTable.getRecentProjectsFromCursor(cursor);
                boolean hasData = mProjectList.size() > 0;
                if (hasData) {
                    Log.v(TAG, "size > 0");
                    mAdapter.changeData(mProjectList);
                }
                setEmptyMessageVisibility(!hasData);
            }
        };
        cursorLoader.startLoading();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_go_back:
                mCallback.onBackButtonClicked();
                break;
            case R.id.btn_add_project:
                mCallback.onNewProjectButtonClicked();
                break;
            case R.id.btn_menu_more:
                PopupMenu popup = new PopupMenu(mActivity, view);
                popup.getMenuInflater().inflate(R.menu.menu_popup_project_fm_action_bar,
                        popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_clear_all_projects:
                                mAdapter.onDeleteAllProjectClicked();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                break;
            default:
                break;
        }
    }

    public void setEmptyMessageVisibility(boolean visibility) {
        if (visibility) {
            mRecyclerView.setVisibility(View.GONE);
            mTxtEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTxtEmptyMessage.setVisibility(View.GONE);
        }
    }
}
