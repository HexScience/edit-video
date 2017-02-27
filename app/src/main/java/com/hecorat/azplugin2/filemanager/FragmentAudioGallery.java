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

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentAudioGallery extends Fragment {

    public GridView mGridView;

    public AudioGalleryAdapter mFolderAdapter, mAudioAdapter;
    public MainActivity mActivity;
    private View mView;

    private int mCountSubFolder;
    private boolean mHasSdCard;

    public String mFolderName;
    private String mStoragePath;
    private String mSdPath;
    private String[] patterns = {".mp3", ".aac"};
    private ArrayList<String> mListFolder, mListFolderSd;
    private ArrayList<String> mListFirstAudio, mListAudio, mListFirstAudioSd;
    public GalleryState galleryState;

    public static FragmentAudioGallery newInstance(MainActivity activity) {
        FragmentAudioGallery fragmentAudioGallery = new FragmentAudioGallery();
        fragmentAudioGallery.mActivity = activity;
        fragmentAudioGallery.inflateViews();
        return fragmentAudioGallery;
    }

    private void inflateViews() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) mView.findViewById(R.id.video_gallery);

        galleryState = GalleryState.AUDIO_FOLDER;
        mListFolder = new ArrayList<>();
        mListFirstAudio = new ArrayList<>();
        mListAudio = new ArrayList<>();
        mListFolderSd = new ArrayList<>();
        mListFirstAudioSd = new ArrayList<>();

        mSdPath = Utils.getSdPath(mActivity);
        if (mSdPath != null) {
            mHasSdCard = true;
            mListFolder.add(mSdPath);
            mListFirstAudio.add("");
        }

        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory, mListFolder);

        mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudio);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = mActivity.getString(R.string.audio_tab_title);

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void openLayoutSdCard() {
        galleryState = GalleryState.AUDIO_FOLDER_SD;
        mListFolderSd.clear();
        mListFirstAudioSd.clear();

        mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudioSd);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onSdCardFolderClick);
        mActivity.setBtnUpLevelVisible(true);
        mFolderName += Constants.SLASH + Constants.SD_CARD;
        mActivity.setFolderName(mFolderName);

        mListFolderSd.add(mSdPath);
        listFolderFrom(new File(mSdPath), mListFolderSd);

        new ScanFolderSdCardTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    AdapterView.OnItemClickListener onSdCardFolderClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            galleryState = GalleryState.AUDIO_SUBFOLDER_SD;
            mListAudio.clear();
            mAudioAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListAudio);
            mGridView.setAdapter(mAudioAdapter);
            mGridView.setOnItemClickListener(onAudioClickListener);
            mActivity.setBtnUpLevelVisible(true);
            String folder = mListFolderSd.get(position);
            String name = new File(folder).getName();
            if (folder.equals(mSdPath)) {
                name = Constants.STORAGE_NAME;
            }
            mFolderName += Constants.SLASH + name;
            mActivity.setFolderName(mFolderName);

            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
        }
    };

    private class ScanFolderSdCardTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < mListFolderSd.size(); i++) {
                boolean scanSubFolder = !mListFolderSd.get(i).equals(mSdPath);
                mCountSubFolder = 0;
                if (!isAudioFolder(new File(mListFolderSd.get(i)), scanSubFolder)) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    private boolean matchFile(File file) {
        for (String pattern : patterns) {
            if (file.getName().endsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    public void upLevel() {
        switch (galleryState) {
            case AUDIO_FOLDER_SD:
            case AUDIO_SUBFOLDER:
                galleryState = GalleryState.AUDIO_FOLDER;
                mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudio);
                mGridView.setAdapter(mFolderAdapter);
                mGridView.setOnItemClickListener(onFolderClickListener);
                mFolderName = mActivity.getString(R.string.audio_tab_title);
                mActivity.setFolderName(mFolderName);
                mActivity.setBtnUpLevelVisible(false);
                break;
            case AUDIO_SUBFOLDER_SD:
                galleryState = GalleryState.AUDIO_FOLDER_SD;
                mFolderAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstAudioSd);
                mGridView.setAdapter(mFolderAdapter);
                mGridView.setOnItemClickListener(onSdCardFolderClick);
                mFolderName = mActivity.getString(R.string.audio_tab_title)
                        + Constants.SLASH + Constants.SD_CARD;
                mActivity.setFolderName(mFolderName);
                break;
        }
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mHasSdCard && i == 0 && galleryState == GalleryState.AUDIO_FOLDER) {
                openLayoutSdCard();
                return;
            }
            galleryState = GalleryState.AUDIO_SUBFOLDER;
            mListAudio.clear();
            mAudioAdapter = new AudioGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListAudio);
            mGridView.setAdapter(mAudioAdapter);
            mGridView.setOnItemClickListener(onAudioClickListener);

            mActivity.setBtnUpLevelVisible(true);
            mFolderName += Constants.SLASH + new File(mListFolder.get(i)).getName();
            mActivity.setFolderName(mFolderName);

            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onAudioClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int[] audioCoord = new int[2];
            view.getLocationOnScreen(audioCoord);
            mActivity.addAudio(mListAudio.get(i), audioCoord);
        }
    };

    private class AsyncTaskScanFile extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... value) {
            boolean subFolder = true;
            String folderPath = galleryState == GalleryState.AUDIO_SUBFOLDER ?
                    mListFolder.get(value[0]) : mListFolderSd.get(value[0]);
            if (folderPath.equals(mStoragePath) || folderPath.equals(mSdPath)) {
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

    private void loadAllAudio(File fileDirectory, ArrayList<String> listAudio, boolean subFolder) {
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null) {
            return;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (subFolder) {
                    loadAllAudio(file, listAudio, true);
                }
            } else {
                if (matchFile(file)) {
                    listAudio.add(file.getAbsolutePath());
                }
            }
        }
    }

    private class AsyncTaskScanFolder extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 1; i < mListFolder.size(); i++) {
                boolean scanSubFolder = !mListFolder.get(i).equals(mStoragePath);
                mCountSubFolder = 0;
                if (!isAudioFolder(new File(mListFolder.get(i)), scanSubFolder)) {
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

    private void listFolderFrom(File fileDirectory, ArrayList<String> listFolder) {
        File[] listFile = fileDirectory.listFiles();
        if (listFile == null) {
            return;
        }
        for (File file : listFile) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (name.charAt(0) != '.') {
                    listFolder.add(file.getAbsolutePath());
                }
            }
        }
    }

    private boolean isAudioFolder(File fileDirectory, boolean includeSubDir) {
        if (mCountSubFolder > 7) {
            return false;
        }
        boolean result = false;
        File[] fileList = fileDirectory.listFiles();
        if (fileList == null) {
            return false;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                if (includeSubDir) {
                    result = isAudioFolder(file, true);
                }
            } else {
                if (matchFile(file)) {
                    if (galleryState == GalleryState.AUDIO_FOLDER) {
                        mListFirstAudio.add(file.getAbsolutePath());
                    } else {
                        mListFirstAudioSd.add(file.getAbsolutePath());
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

    private class AudioGalleryAdapter extends ArrayAdapter<String> {

        private AudioGalleryAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.folder_gallery_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.iconFolder.setVisibility(View.GONE);

            switch (galleryState) {
                case AUDIO_FOLDER:
                    getLayoutFolder(viewHolder, position);
                    break;
                case AUDIO_FOLDER_SD:
                    getLayoutFolderSd(viewHolder, position);
                    break;
                case AUDIO_SUBFOLDER:
                case AUDIO_SUBFOLDER_SD:
                    getLayoutAudio(viewHolder, position);
                    break;
            }
            return convertView;
        }

        private void getLayoutFolder(ViewHolder viewHolder, int position) {
            int iconId;
            String name;
            if (mHasSdCard && position == 0) {
                iconId = R.drawable.ic_sd_card;
                name = Constants.SD_CARD;
            } else {
                iconId = R.drawable.ic_audio_folder;
                name = new File(mListFolder.get(position)).getName();
            }
            viewHolder.imageView.setImageResource(iconId);
            viewHolder.textView.setText(name);
        }

        private void getLayoutFolderSd(ViewHolder viewHolder, int position) {
            String folder = mListFolderSd.get(position);
            String name = new File(folder).getName();
            if (folder.equals(mSdPath)) {
                name = Constants.STORAGE_NAME;
            }
            viewHolder.imageView.setImageResource(R.drawable.ic_audio_folder);
            viewHolder.textView.setText(name);
        }

        private void getLayoutAudio(ViewHolder viewHolder, int position) {
            String name = new File(mListAudio.get(position)).getName();
            int iconId;
            if (name.endsWith(".mp3")) {
                iconId = R.drawable.ic_mp3;
            } else {
                iconId = R.drawable.ic_aac;
            }

            viewHolder.imageView.setImageResource(iconId);
            viewHolder.textView.setText(name);
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
