package com.hecorat.azplugin2.filemanager;

import android.content.Context;
import android.net.Uri;
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
import com.hecorat.azplugin2.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 08/11/2016.
 */
public class FragmentImagesGallery extends Fragment {

    public GridView mGridView;
    public String mStoragePath;
    public ImageGalleryAdapter mFolderAdapter, mImageAdapter;
    public MainActivity mActivity;

    public boolean mIsSubFolder;
    public int mCountSubFolder;
    public boolean mChooseSticker;

    public ArrayList<String> mListFolder, mListStiker;
    public ArrayList<String> mListFirstImage, mListImage;
    public String mFolderName;
    public String[] patterns = {".png", "jpg"};
    public static final String STICKER_FOLDER = "sticker";
    public static final String STICKER_FOLDER_NAME = "Stickers";
    public static final String ASSETS_PATH = "file:///android_asset/";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) view.findViewById(R.id.video_gallery);
        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder = new ArrayList<>();
        mListFolder.add(STICKER_FOLDER_NAME);
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory);
        mListFirstImage = new ArrayList<>();
        mListImage = new ArrayList<>();

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mChooseSticker = false;
        mIsSubFolder = false;
        mFolderAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstImage);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = getString(R.string.image_tab_title);

        return view;
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
        if (mFolderAdapter==null){
            mFolderAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstImage);
        }
        mGridView.setAdapter(mFolderAdapter);
        mFolderName = getString(R.string.image_tab_title);
        mActivity.setFolderName(mFolderName);
        mGridView.setOnItemClickListener(onFolderClickListener);
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mIsSubFolder = true;
            mGridView.setOnItemClickListener(onImageClickListener);
            mActivity.mOpenImageSubFolder = true;
            mActivity.setBtnUpLevelVisible(true);
            if (i==0){
                mChooseSticker = true;
                mImageAdapter = new ImageGalleryAdapter(mActivity, R.layout.image_gallery_layout, mListStiker);
                mGridView.setAdapter(mImageAdapter);
                mFolderName = STICKER_FOLDER_NAME;
                mActivity.setFolderName(mFolderName);
                return;
            }
            mChooseSticker = false;
            mListImage.clear();
            mImageAdapter = new ImageGalleryAdapter(getContext(), R.layout.image_gallery_layout, mListImage);
            mGridView.setAdapter(mImageAdapter);
            mFolderName += " / " + new File(mListFolder.get(i)).getName();
            mActivity.setFolderName(mFolderName);
            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onImageClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int viewCoord[] = new int[2];
            view.getLocationOnScreen(viewCoord);
            if (mChooseSticker){
                String assetsPath = mListStiker.get(i);
                String nameSticker = assetsPath.replace("/","_");
                String stickerPath = Utils.getResourceFolder()+"/"+nameSticker;
                Utils.copyFileFromAssets(mActivity, assetsPath, stickerPath);
                mActivity.addImage(stickerPath, viewCoord);
            } else {
                mActivity.addImage(mListImage.get(i), viewCoord);
            }
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
            loadAllImage(new File(folderPath), mListImage, subFolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mImageAdapter.notifyDataSetChanged();
        }
    }

    private void loadAllImage(File fileDirectory, ArrayList<String> listImage, boolean subFolder){
        File[] fileList = fileDirectory.listFiles();
        for (File file : fileList){
            if (file.isDirectory()) {
                if (subFolder) {
                    loadAllImage(file, listImage, true);
                }
            } else {
                if (matchFile(file)) {
                    listImage.add(file.getAbsolutePath());
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
            mListStiker = Utils.listFilesFromAssets(mActivity, STICKER_FOLDER);
            mListFirstImage.add(mListStiker.get(0));
            for (int i=1; i<mListFolder.size(); i++) {
                boolean scanSubFolder = !mListFolder.get(i).equals(mStoragePath);
                mCountSubFolder = 0;
                if (!isImageFolder(new File(mListFolder.get(i)), scanSubFolder)){
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
        for (File file : listFile) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (name.charAt(0) != '.'){
                    mListFolder.add(file.getAbsolutePath());
                }
            }
        }
    }

    private boolean isImageFolder(File fileDirectory, boolean includeSubDir) {
        if (mCountSubFolder>7) {
            return false;
        }
        boolean result = false;
        File[] fileList = fileDirectory.listFiles();
        for (File file : fileList){
            if (file.isDirectory()) {
                if (includeSubDir) {
                    result = isImageFolder(file, true);
                }
            } else {
                if (matchFile(file)) {
                    mListFirstImage.add(file.getAbsolutePath());
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

    private class ImageGalleryAdapter extends ArrayAdapter<String> {

        private ImageGalleryAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (!mIsSubFolder){
                return getFolderLayout(position);
            } else if (mChooseSticker){
                return getStickerLayout(position);
            } else {
                return getImageLayout(position);
            }
        }

        private View getFolderLayout(int position){
            View convertView = LayoutInflater.from(getContext()).inflate(R.layout.folder_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);

            if (position == 0) {
                textView.setText(STICKER_FOLDER_NAME);
                Uri uri = Uri.parse(ASSETS_PATH + mListFirstImage.get(0));
                Glide.with(getContext()).load(uri).centerCrop().into(imageView);
            } else {
                String name = new File(mListFolder.get(position)).getName();
                textView.setText(name);
                Glide.with(getContext()).load(mListFirstImage.get(position)).centerCrop().into(imageView);
            }
            return convertView;
        }

        private View getStickerLayout(int position){
            View convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            ImageView iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
            iconFolder.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            Uri uri = Uri.parse(ASSETS_PATH + mListStiker.get(position));
            Glide.with(getContext()).load(uri).centerCrop().into(imageView);

            return convertView;
        }

        private View getImageLayout(int position){
            View convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            String imagePath = mListImage.get(position);
            String name = new File(imagePath).getName();
            textView.setText(name);
            Glide.with(getContext()).load(imagePath).centerCrop().into(imageView);
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
