package com.hecorat.editvideo.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentAudioGallery extends Fragment {
    public ArrayList<String> mListFolder;
    public ArrayList<String> mListFirstAudio, mListAudio;
    public GridView mGridView;
    public String mStoragePath;
    public AudioGalleryAdapter mFolderAdapter, mAudioAdapter;
    public MainActivity mActivity;

    public int mCountSubFolder;
    public boolean mIsSubFolder;
    public String mFolderName;
    String[] pattern = {".mp3",".aac"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) view.findViewById(R.id.video_gallery);
        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder = new ArrayList<>();
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory);
        mListFirstAudio = new ArrayList<>();
        mListAudio = new ArrayList<>();

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mIsSubFolder = false;
        mFolderAdapter = new AudioGalleryAdapter(getContext(), R.layout.image_layout, mListFirstAudio);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = getString(R.string.audio_tab_title);
        return view;
    }

    private boolean matchFile(File file){
        for (int i=0; i<pattern.length; i++) {
            if (file.getName().endsWith(pattern[i])){
                return true;
            }
        }
        return false;
    }

    public void backToMain() {
        mIsSubFolder = false;
        mGridView.setAdapter(mFolderAdapter);
        mFolderName = getString(R.string.audio_tab_title);
        mActivity.setFolderName(mFolderName);
        mGridView.setOnItemClickListener(onFolderClickListener);
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mIsSubFolder = true;
        mListAudio.clear();
        mAudioAdapter = new AudioGalleryAdapter(getContext(), R.layout.image_layout, mListAudio);
        mGridView.setAdapter(mAudioAdapter);
        mGridView.setOnItemClickListener(onAudioClickListener);
        mActivity.mOpenAudioSubFolder = true;
        mFolderName = new File(mListFolder.get(i)).getName();
        mActivity.setFolderName(mFolderName);
        new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onAudioClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mActivity.addAudio(mListAudio.get(i));
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
            loadAllAudio(new File(folderPath), mListAudio, subFolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAudioAdapter.notifyDataSetChanged();
        }
    }

    private void loadAllAudio(File fileDirectory, ArrayList<String> listAudio, boolean subFolder){
        File[] fileList = fileDirectory.listFiles();
        for (int i=0; i<fileList.length; i++){
            if (fileList[i].isDirectory()) {
                if (subFolder) {
                    loadAllAudio(fileList[i], listAudio, true);
                }
            } else {
                if (matchFile(fileList[i])) {
                    listAudio.add(fileList[i].getAbsolutePath());
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
                boolean scanSubFolder = mListFolder.get(i).equals(mStoragePath)? false:true;
                mCountSubFolder = 0;
                if (!isAudioFolder(new File(mListFolder.get(i)), scanSubFolder)){
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
        for (int i=0; i<listFile.length; i++) {
            if (listFile[i].isDirectory()) {
                String name = listFile[i].getName();
                if (name.charAt(0) != '.'){
                    mListFolder.add(listFile[i].getAbsolutePath());
                }
            }
        }
    }

    private boolean isAudioFolder(File fileDirectory, boolean includeSubDir) {
        if (mCountSubFolder>7) {
            return false;
        }
        boolean result = false;
        File[] fileList = fileDirectory.listFiles();
        for (int i=0; i<fileList.length; i++){
            if (fileList[i].isDirectory()) {
                if (includeSubDir) {
                    result = isAudioFolder(fileList[i], true);
                }
            } else {
                if (matchFile(fileList[i])) {
                    mListFirstAudio.add(fileList[i].getAbsolutePath());
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

    private class AudioGalleryAdapter extends ArrayAdapter<String> {

        public AudioGalleryAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            ImageView iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
            iconFolder.setVisibility(View.GONE);
            String name;
            if (mIsSubFolder) {
                name = new File(mListAudio.get(position)).getName();
            } else {
                name = new File(mListFolder.get(position)).getName();
            }
            textView.setText(name);
            int iconId;
            if (name.endsWith(".mp3")) {
                iconId = R.drawable.ic_mp3;
            } else if (name.endsWith(".aac")) {
                iconId = R.drawable.ic_aac;
            } else {
                iconId = R.drawable.ic_audio_folder;
            }
            imageView.setImageResource(iconId);
            return convertView;
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
