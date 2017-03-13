package com.hecorat.azplugin2.main;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.database.ProjectObject;
import com.hecorat.azplugin2.database.ProjectTable;
import com.hecorat.azplugin2.helper.AnalyticsHelper;

import java.util.ArrayList;

import static com.hecorat.azplugin2.main.Constants.DEFAULT_PROJECT_NAME;

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
    private int mCurrentProjectId;

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

        void onAllProjectsLoaded();

        void onOpenProjectClicked(ProjectObject projectObject);

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
        mCurrentProjectId = getArguments().getInt(KEY);
        View attachView = inflater.inflate(R.layout.fragment_project_list, container, false);
        mRecyclerView = (RecyclerView) attachView.findViewById(R.id.project_list);
        mProjectTable = new ProjectTable(mActivity);
        mAdapter = new ProjectAdapter();
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
                mCallback.onAllProjectsLoaded();
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

    private final class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

        private ArrayList<ProjectObject> mProjectList = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.project_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProjectAdapter.ViewHolder holder, final int position) {
            final ProjectObject project = mProjectList.get(position);
            final String projectName = project.name;
            final String projectFirstVideo = project.firstVideo;

            if (!TextUtils.isEmpty(projectFirstVideo)) {
                holder.mLoadThumbProgress.setVisibility(View.VISIBLE);
                Glide.with(mActivity).load(projectFirstVideo)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFirstResource) {
                                holder.mLoadThumbProgress.setVisibility(View.GONE);
                                holder.mImgThumb.setImageResource(R.drawable.ic_default_video_icon);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                holder.mLoadThumbProgress.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(holder.mImgThumb);
            }

            holder.mTvTitle.setText(projectName != null ? projectName : DEFAULT_PROJECT_NAME);

            holder.mImgOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onOpenProjectClicked(project);
                }
            });

            holder.mImgRename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRenameProjectClicked(project);
                }
            });

            holder.mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteProjectClicked(project);
                }
            });


            holder.mRootView.setBackgroundResource(project.id == mCurrentProjectId ?
                    R.drawable.bg_current_item : R.drawable.btn_touch_feedback_rec_nobackground);

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onOpenProjectClicked(project);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mProjectList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImgThumb;
            TextView mTvTitle;
            ImageView mImgOpen;
            ImageView mImgRename;
            ImageView mImgDelete;
            RelativeLayout mRootView;
            ProgressBar mLoadThumbProgress;

            ViewHolder(View view) {
                super(view);
                mImgThumb = (ImageView) view.findViewById(R.id.img_thumbnail);
                mTvTitle = (TextView) view.findViewById(R.id.tv_title);
                mImgOpen = (ImageView) view.findViewById(R.id.btn_open_project);
                mImgRename = (ImageView) view.findViewById(R.id.btn_rename_project);
                mImgDelete = (ImageView) view.findViewById(R.id.btn_delete_project);
                mLoadThumbProgress = (ProgressBar) view.findViewById(R.id.load_thumbnail_progress);
                mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
            }
        }

        void changeData(ArrayList<ProjectObject> projectList) {
            mProjectList = projectList;
            notifyDataSetChanged();
        }

        void onDeleteAllProjectClicked() {
            int size = mProjectList.size();
            if (size == 0) {
                Toast.makeText(mActivity, R.string.toast_project_list_empty, Toast.LENGTH_LONG).show();
                return;
            }

            if (mCurrentProjectId == -1) {
                mProjectTable.deleteAllProject();
                mProjectList.clear();
                notifyDataSetChanged();
                Toast.makeText(mActivity, mActivity.getString(R.string.toast_delete_all_projects),
                        Toast.LENGTH_LONG).show();
                setEmptyMessageVisibility(true);
            } else {
                if (size == 1) {
                    Toast.makeText(mActivity, R.string.toast_delete_current_project, Toast.LENGTH_LONG).show();
                    return;
                }

                int count = 0;
                ProjectObject currentProject = getCurrentProject();

                for (ProjectObject projectObject : mProjectList) {
                    if (!projectObject.equals(currentProject)) {
                        mProjectTable.deleteProject(projectObject.id);
                        count++;
                    }
                }

                mProjectList.clear();
                mProjectList.add(currentProject);
                notifyDataSetChanged();

                Toast.makeText(mActivity, mActivity.getString((count == 1) ?
                                R.string.toast_delete_project : R.string.toast_delete_projects, count),
                        Toast.LENGTH_LONG).show();
            }
        }

        private void onRenameProjectClicked(final ProjectObject projectObject) {
            final String oldName = projectObject.name;
            final int projectId = projectObject.id;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            View attachView = mActivity.getLayoutInflater().inflate(R.layout.edt_name_project, null);
            final EditText editText = (EditText) attachView.findViewById(R.id.edt_name_project);
            editText.setText(oldName);
            if (!TextUtils.isEmpty(oldName)) editText.setSelection(oldName.length());
            builder.setView(attachView)
                    .setIcon(R.drawable.ic_rename_project)
                    .setTitle(R.string.dialog_title_rename_project)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String newName = editText.getText().toString();
                                    if (newName.equals(oldName)) return;
                                    mProjectTable.updateValue(projectId, ProjectTable.PROJECT_NAME, newName);
                                    projectObject.name = newName;
                                    notifyItemChanged(mProjectList.indexOf(projectObject));
                                    AnalyticsHelper.getInstance().send(mActivity,
                                            Constants.CATEGORY_PROJECT,
                                            Constants.ACTION_RENAME_PROJECT);
                                }
                            });
            final AlertDialog renameDialog = builder.create();
            renameDialog.show();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Button button = renameDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                    button.setEnabled(!TextUtils.isEmpty(s.toString()));
                }
            });
        }

        private void onDeleteProjectClicked(final ProjectObject projectObject) {
            final int projectId = projectObject.id;

            if (projectId == mCurrentProjectId) {
                Toast.makeText(mActivity, mActivity.getString(R.string.toast_delete_current_project)
                        , Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.dialog_title_delete_project)
                    .setIcon(R.drawable.ic_delete_project)
                    .setMessage(R.string.dialog_msg_delete_project)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mProjectTable.deleteProject(projectObject.id);
                                    notifyItemRemoved(mProjectList.indexOf(projectObject));
                                    mProjectList.remove(projectObject);
                                    AnalyticsHelper.getInstance().send(mActivity,
                                            Constants.CATEGORY_PROJECT,
                                            Constants.ACTION_DELETE_PROJECT);
                                }
                            })
                    .show();
        }

        private ProjectObject getCurrentProject() {
            for (ProjectObject projectObject : mProjectList) {
                if (projectObject.id == mCurrentProjectId) {
                    return projectObject;
                }
            }
            return null;
        }
    }
}
