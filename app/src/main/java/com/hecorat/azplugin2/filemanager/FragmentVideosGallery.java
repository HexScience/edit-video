package com.hecorat.azplugin2.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentVideosGallery extends Fragment {
    public GridView mGridView;
    public VideoGalleryAdapter mFolderAdapter, mVideoAdapter;
    public MainActivity mActivity;
    private View mView;

    private String mSdPath;
    public String mFolderName;
    public String[] patterns = {".mp4"};
    public String mStoragePath;
    public ArrayList<String> mListFolder, mListFolderSd;
    public ArrayList<String> mListFirstVideo, mListVideo, mListFirstVideoSd;

    public int mCountSubFolder;
    private boolean mHasSdCard;
    public GalleryState galleryState;

    public static FragmentVideosGallery newInstance(MainActivity activity) {
        FragmentVideosGallery fragmentVideosGallery = new FragmentVideosGallery();
        fragmentVideosGallery.mActivity = activity;
        fragmentVideosGallery.inflateViews();
        return fragmentVideosGallery;
    }

    private void inflateViews(){
        mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) mView.findViewById(R.id.video_gallery);
        mListFolder = new ArrayList<>();
        mListFirstVideo = new ArrayList<>();
        mListVideo = new ArrayList<>();
        mListFolderSd = new ArrayList<>();
        mListFirstVideoSd = new ArrayList<>();

        mSdPath = Utils.getSdPath(mActivity);
        if (mSdPath != null) {
            mListFolder.add(mSdPath);
            mListFirstVideo.add("");
            mHasSdCard = true;
        } else {
            mHasSdCard = false;
        }

        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder.add(mStoragePath);

        listFolderFrom(fileDirectory, mListFolder);
        galleryState = GalleryState.VIDEO_FOLDER;

        new AsyncTaskScanFolderVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideo);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = mActivity.getString(R.string.video_tab_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            inflateViews();
        }
        return mView;
    }

    private boolean matchFile(File file){
        for (String pattern : patterns) {
            if (file.getName().endsWith(pattern)){
                return true;
            }
        }
        return false;
    }

    public void upLevel() {
        switch (galleryState) {
            case VIDEO_FOLDER_SD:
            case VIDEO_SUBFOLDER:
                galleryState = GalleryState.VIDEO_FOLDER;
                mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideo);
                mGridView.setAdapter(mFolderAdapter);
                mGridView.setOnItemClickListener(onFolderClickListener);
                mFolderName = getString(R.string.video_tab_title);
                mActivity.setFolderName(mFolderName);
                mActivity.setBtnUpLevelVisible(false);
                break;
            case VIDEO_SUBFOLDER_SD:
                galleryState = GalleryState.VIDEO_FOLDER_SD;
                mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideoSd);
                mGridView.setAdapter(mFolderAdapter);
                mGridView.setOnItemClickListener(onFolderClickListener);
                mFolderName = getString(R.string.video_tab_title) + Constants.SLASH + Constants.SD_CARD;
                mActivity.setFolderName(mFolderName);
                mActivity.setBtnUpLevelVisible(true);
                break;
        }
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mHasSdCard && i == 0 && galleryState == GalleryState.VIDEO_FOLDER) {
                setupLayoutSdFolder();
                return;
            }

            mListVideo.clear();
            mActivity.setBtnUpLevelVisible(true);
            String name;
            if (galleryState == GalleryState.VIDEO_FOLDER) {
                galleryState = GalleryState.VIDEO_SUBFOLDER;
                name = new File(mListFolder.get(i)).getName();
            } else {
                galleryState = GalleryState.VIDEO_SUBFOLDER_SD;
                String folderPath = mListFolderSd.get(i);
                if (folderPath.equals(mSdPath)) {
                    name = Constants.STORAGE_NAME;
                } else {
                    name = new File(folderPath).getName();
                }
            }
            mFolderName += Constants.SLASH + name;
            mActivity.setFolderName(mFolderName);
            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);

            mVideoAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListVideo);
            mGridView.setAdapter(mVideoAdapter);
            mGridView.setOnItemClickListener(onVideoClickListener);
        }
    };

    private void setupLayoutSdFolder() {
        galleryState = GalleryState.VIDEO_FOLDER_SD;
        mListFolderSd.clear();
        mListFirstVideoSd.clear();

        mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideoSd);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);

        mActivity.setBtnUpLevelVisible(true);
        mFolderName += Constants.SLASH + Constants.SD_CARD;
        mActivity.setFolderName(mFolderName);
        mListFolderSd.add(mSdPath);
        listFolderFrom(new File(mSdPath), mListFolderSd);

        new AsyncTaskScanFolderSd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class AsyncTaskScanFolderSd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < mListFolderSd.size(); i++) {
                boolean scanSubFolder = i != 0;
                mCountSubFolder = 0;
                if (!isVideoFolder(new File(mListFolderSd.get(i)), scanSubFolder)){
                    mListFolderSd.remove(i);
                    i--;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFolderAdapter.notifyDataSetChanged();
        }
    }

    AdapterView.OnItemClickListener onVideoClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String videoPath = mListVideo.get(i);
            int point[] = new int[2];
            view.getLocationOnScreen(point);
            mActivity.addVideo(videoPath, point);
        }
    };

    private class AsyncTaskScanFile extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... value) {
            boolean subFolder = true;
            String folderPath = galleryState == GalleryState.VIDEO_SUBFOLDER ?
                    mListFolder.get(value[0]) : mListFolderSd.get(value[0]);
            if (folderPath.equals(mStoragePath) || folderPath.equals(mSdPath)){
                subFolder = false;
            }
            loadAllVideo(new File(folderPath), mListVideo, subFolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mVideoAdapter.notifyDataSetChanged();
        }
    }

    private void loadAllVideo(File fileDirectory, ArrayList<String> listVideo, boolean subFolder){
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null){
            return;
        }
        for (File file : fileList){
            if (file.isDirectory()) {
                if (subFolder) {
                    loadAllVideo(file, listVideo, true);
                }
            } else {
                if (matchFile(file)) {
                    listVideo.add(file.getAbsolutePath());
                }
            }
        }
    }

    private class AsyncTaskScanFolderVideo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int begin = mHasSdCard ? 1 : 0;
            for (int i = begin; i < mListFolder.size(); i++) {
                boolean scanSubFolder = i != begin;
                mCountSubFolder = 0;
                if (!isVideoFolder(new File(mListFolder.get(i)), scanSubFolder)){
                    mListFolder.remove(i);
                    i--;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFolderAdapter.notifyDataSetChanged();
        }
    }

    private void listFolderFrom(File fileDirectory, ArrayList<String> listFolder){
        File[] listFile = fileDirectory.listFiles();
        if (listFile == null) {
            return;
        }
        for (File file : listFile) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (name.charAt(0) != '.'){
                    listFolder.add(file.getAbsolutePath());
                }
            }
        }
    }

    private boolean isVideoFolder(File fileDirectory, boolean includeSubDir) {
        if (mCountSubFolder>7) {
            return false;
        }
        boolean result = false;
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null) {
            return false;
        }

        for (File file : fileList){
            if (file.isDirectory()) {
                if (includeSubDir) {
                    result = isVideoFolder(file, true);
                }
            } else {
                if (matchFile(file)) {
                    if (galleryState == GalleryState.VIDEO_FOLDER) {
                        mListFirstVideo.add(file.getAbsolutePath());
                    } else {
                        mListFirstVideoSd.add(file.getAbsolutePath());
                    }
                    result = true;
                }
            }
            if (result) {
                break;
            }
        }
        mCountSubFolder++;
        return result;
    }

    private class VideoGalleryAdapter extends ArrayAdapter<String> {

        private VideoGalleryAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.folder_gallery_layout, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            switch (galleryState) {
                case VIDEO_FOLDER:
                    setLayoutVideoFolder(viewHolder, position);
                    break;
                case VIDEO_FOLDER_SD:
                    setLayoutVideoFolderSd(viewHolder, position);
                    break;
                case VIDEO_SUBFOLDER:
                case VIDEO_SUBFOLDER_SD:
                    setLayoutVideo(viewHolder, position);
                    break;
            }
            return convertView;
        }

        private void setLayoutVideoFolder(ViewHolder viewHolder, int position) {
            if (mHasSdCard && position == 0) {
                viewHolder.iconFolder.setImageBitmap(null);
                viewHolder.textView.setText(Constants.SD_CARD);
                viewHolder.imageView.setImageResource(R.drawable.ic_sd_card);
            } else {
                String videoPath = mListFirstVideo.get(position);
                String name = new File(mListFolder.get(position)).getName();
                int iconId = R.drawable.ic_folder;
                viewHolder.iconFolder.setImageResource(iconId);
                viewHolder.textView.setText(name);
                Glide.with(mActivity).load(videoPath).centerCrop().into(viewHolder.imageView);
            }
        }

        private void setLayoutVideoFolderSd(ViewHolder viewHolder, int position){
            String videoPath = mListFirstVideoSd.get(position);
            String name = new File(mListFolderSd.get(position)).getName();
            if (position == 0 && mListFolderSd.get(0).equals(mSdPath)) {
                name = Constants.STORAGE_NAME;
            }
            int iconId = R.drawable.ic_folder;
            viewHolder.iconFolder.setImageResource(iconId);
            viewHolder.textView.setText(name);
            Glide.with(mActivity).load(videoPath).centerCrop().into(viewHolder.imageView);
        }

        private void setLayoutVideo(ViewHolder viewHolder, int position) {
            String videoPath = mListVideo.get(position);
            String name = new File(videoPath).getName();
            int iconId = R.drawable.ic_video;
            viewHolder.iconFolder.setImageResource(iconId);
            viewHolder.textView.setText(name);
            Glide.with(mActivity).load(videoPath).centerCrop().into(viewHolder.imageView);
        }

        class ViewHolder {
            ImageView imageView, iconFolder;
            TextView textView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private void log(String msg) {
        Log.e("Fragment Video", msg);
    }
}
