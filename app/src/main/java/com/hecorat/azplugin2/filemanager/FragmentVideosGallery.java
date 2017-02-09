package com.hecorat.azplugin2.filemanager;

import android.content.Context;
import android.media.Image;
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
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentVideosGallery extends Fragment {
    public ArrayList<String> mListFolder;
    public ArrayList<String> mListFirstVideo, mListVideo;
    public GridView mGridView;
    public String mStoragePath;
    public VideoGalleryAdapter mFolderAdapter, mVideoAdapter;
    public MainActivity mActivity;
    private View mView;

    public boolean mIsSubFolder;
    public String mFolderName;
    public String[] patterns = {".mp4"};
    public int mCountSubFolder;

    public static FragmentVideosGallery newInstance(MainActivity activity) {
        FragmentVideosGallery fragmentVideosGallery = new FragmentVideosGallery();
        fragmentVideosGallery.mActivity = activity;
        fragmentVideosGallery.inflateViews();
        return fragmentVideosGallery;
    }

    private void inflateViews(){
        mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) mView.findViewById(R.id.video_gallery);
        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder = new ArrayList<>();
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory);
        mListFirstVideo = new ArrayList<>();
        mListVideo = new ArrayList<>();

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mIsSubFolder = false;
        mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideo);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = mActivity.getString(R.string.video_tab_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    public void backToMain() {
        mIsSubFolder = false;
        if (mFolderAdapter == null){
            mFolderAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstVideo);
        }
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = getString(R.string.video_tab_title);
        mActivity.setFolderName(mFolderName);
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mIsSubFolder = true;
            mListVideo.clear();
            mVideoAdapter = new VideoGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListVideo);
            mGridView.setAdapter(mVideoAdapter);
            mGridView.setOnItemClickListener(onVideoClickListener);
            mActivity.mOpenVideoSubFolder = true;
            mActivity.setBtnUpLevelVisible(true);
            mFolderName += " / " + new File(mListFolder.get(i)).getName();
            mActivity.setFolderName(mFolderName);
            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onVideoClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String videoPath = mListVideo.get(i);
            int point[] = new int[2];
            view.getLocationOnScreen(point);
            mActivity.addVideo(videoPath, point);

            log("view x= "+point[0]+" y= "+point[1]);
        }
    };

    private class AsyncTaskScanFile extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... value) {
            boolean subFolder = true;
            String folderPath = mListFolder.get(value[0]);
            if (folderPath.equals(mStoragePath)){
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

    private class AsyncTaskScanFolder extends AsyncTask<Void, Void, Void> {
        long start;
        @Override
        protected void onPreExecute() {
            start = System.currentTimeMillis();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i=0; i<mListFolder.size(); i++) {
                boolean scanSubFolder = !mListFolder.get(i).equals(mStoragePath);
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

    private void listFolderFrom(File fileDirectory){
        File[] listFile = fileDirectory.listFiles();
        if (listFile == null) {
            return;
        }
        for (File file : listFile) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (name.charAt(0) != '.'){
                    mListFolder.add(file.getAbsolutePath());
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
                    mListFirstVideo.add(file.getAbsolutePath());
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

            String name, videoPath;
            int iconId ;
            if (mIsSubFolder) {
                videoPath = mListVideo.get(position);
                name = new File(videoPath).getName();
                iconId = R.drawable.ic_video;
            } else {
                videoPath = mListFirstVideo.get(position);
                name = new File(mListFolder.get(position)).getName();
                iconId = R.drawable.ic_folder;
            }
            viewHolder.iconFolder.setImageResource(iconId);
            viewHolder.textView.setText(name);
            Glide.with(mActivity).load(videoPath).centerCrop().into(viewHolder.imageView);
            return convertView;
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
