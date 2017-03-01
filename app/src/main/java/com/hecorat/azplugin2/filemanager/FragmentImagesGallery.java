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
import com.hecorat.azplugin2.helper.AnalyticsHelper;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.Constants;
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
    private View mView;

    public int mCountSubFolder;
    private boolean mHasSdCard;

    public ArrayList<String> mListFolder, mListStiker, mListFolderSd;
    public ArrayList<String> mListFirstImage, mListImage, mListFirstImageSd;
    public String mFolderName;
    public String[] patterns = {".png", "jpg"};
    public GalleryState galleryState;
    private String mSdPath;

    public static final String STICKER_FOLDER = "sticker";
    public static final String STICKER_FOLDER_NAME = "Stickers";
    public static final String ASSETS_PATH = "file:///android_asset/";

    public static FragmentImagesGallery newInstance(MainActivity activity) {
        FragmentImagesGallery fragmentImagesGallery = new FragmentImagesGallery();
        fragmentImagesGallery.mActivity = activity;
        fragmentImagesGallery.inflateViews();
        return fragmentImagesGallery;
    }

    private void inflateViews(){
        mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_videos_gallery, null);
        mGridView = (GridView) mView.findViewById(R.id.video_gallery);

        mListFolder = new ArrayList<>();
        mListFirstImage = new ArrayList<>();
        mListImage = new ArrayList<>();
        mListFolderSd = new ArrayList<>();
        mListFirstImageSd = new ArrayList<>();

        galleryState = GalleryState.IMAGE_FOLDER;

        mSdPath = Utils.getSdPath(mActivity);
        if (mSdPath != null) {
            mListFolder.add(mSdPath);
            mListFirstImage.add("");
            mHasSdCard = true;
        } else {
            mHasSdCard = false;
        }

        mStoragePath = Environment.getExternalStorageDirectory().toString();
        File fileDirectory = new File(mStoragePath);
        mListFolder.add(STICKER_FOLDER_NAME);
        mListFolder.add(mStoragePath);
        listFolderFrom(fileDirectory, mListFolder);

        new AsyncTaskScanFolder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mFolderAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstImage);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onFolderClickListener);
        mFolderName = mActivity.getString(R.string.image_tab_title);
    }

    private void openLayoutSdCard(){
        galleryState = GalleryState.IMAGE_FOLDER_SD;
        mListFolderSd.clear();
        mListFirstImageSd.clear();

        mListFolderSd.add(mSdPath);
        listFolderFrom(new File(mSdPath), mListFolderSd);
        mFolderAdapter = new ImageGalleryAdapter
                (mActivity, R.layout.folder_gallery_layout, mListFirstImageSd);
        mGridView.setAdapter(mFolderAdapter);
        mGridView.setOnItemClickListener(onSdCardFolderClickListener);
        mActivity.setBtnUpLevelVisible(true);
        mFolderName += Constants.SLASH + Constants.SD_CARD;
        mActivity.setFolderName(mFolderName);

        new ScanFolderSdCardTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class ScanFolderSdCardTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (int i=0; i<mListFolderSd.size(); i++) {
                boolean scanSubFolder = !mListFolderSd.get(i).equals(mSdPath);
                mCountSubFolder = 0;
                if (!isImageFolder(new File(mListFolderSd.get(i)), scanSubFolder)){
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

    AdapterView.OnItemClickListener onSdCardFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            galleryState = GalleryState.IMAGE_SUBFOLDER_SD;
            mListImage.clear();
            mImageAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListImage);
            mGridView.setAdapter(mImageAdapter);
            mGridView.setOnItemClickListener(onImageClickListener);
            mActivity.setBtnUpLevelVisible(true);
            String folder = mListFolderSd.get(position);
            if (folder.equals(mSdPath)) {
                mFolderName += Constants.SLASH + Constants.STORAGE_NAME;
            } else {
                mFolderName += Constants.SLASH + new File(folder).getName();
            }
            mActivity.setFolderName(mFolderName);

            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
        }
    };

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
            case IMAGE_FOLDER_SD:
            case IMAGE_SUBFOLDER:
            case IMAGE_SUBFOLDER_STICKER:
                galleryState = GalleryState.IMAGE_FOLDER;
                mFolderAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstImage);
                mGridView.setAdapter(mFolderAdapter);
                mGridView.setOnItemClickListener(onFolderClickListener);

                mFolderName = mActivity.getString(R.string.image_tab_title);
                mActivity.setFolderName(mFolderName);
                mActivity.setBtnUpLevelVisible(false);
                break;
            case IMAGE_SUBFOLDER_SD:
                galleryState = GalleryState.IMAGE_FOLDER_SD;
                mFolderAdapter = new ImageGalleryAdapter(mActivity, R.layout.folder_gallery_layout, mListFirstImageSd);
                mGridView.setAdapter(mFolderAdapter);
                mActivity.setBtnUpLevelVisible(true);
                mFolderName = mActivity.getString(R.string.image_tab_title)
                        + Constants.SLASH + Constants.SD_CARD;
                mActivity.setFolderName(mFolderName);
                mGridView.setOnItemClickListener(onSdCardFolderClickListener);
                break;
        }
    }

    AdapterView.OnItemClickListener onFolderClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mHasSdCard && i == 0 && galleryState == GalleryState.IMAGE_FOLDER) {
                openLayoutSdCard();
                return;
            }
            mGridView.setOnItemClickListener(onImageClickListener);
            mActivity.mOpenImageSubFolder = true;
            mActivity.setBtnUpLevelVisible(true);
            if (i==0 || mHasSdCard && i==1){
                galleryState = GalleryState.IMAGE_SUBFOLDER_STICKER;
                mImageAdapter = new ImageGalleryAdapter(mActivity, R.layout.image_gallery_layout, mListStiker);
                mGridView.setAdapter(mImageAdapter);
                mFolderName += Constants.SLASH + STICKER_FOLDER_NAME;
                mActivity.setFolderName(mFolderName);
                return;
            }
            galleryState = GalleryState.IMAGE_SUBFOLDER;
            mListImage.clear();
            mImageAdapter = new ImageGalleryAdapter(mActivity, R.layout.image_gallery_layout, mListImage);
            mGridView.setAdapter(mImageAdapter);
            mFolderName += Constants.SLASH + new File(mListFolder.get(i)).getName();
            mActivity.setFolderName(mFolderName);
            new AsyncTaskScanFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }
    };

    AdapterView.OnItemClickListener onImageClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int viewCoord[] = new int[2];
            view.getLocationOnScreen(viewCoord);
            if (galleryState == GalleryState.IMAGE_SUBFOLDER_STICKER){
                String assetsPath = mListStiker.get(i);
                String nameSticker = assetsPath.replace("/","_");
                String stickerPath = Utils.getResourceFolder()+"/"+nameSticker;
                Utils.copyFileFromAssets(mActivity, assetsPath, stickerPath);
                mActivity.addImage(stickerPath, viewCoord);
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_ADD_FILE, Constants.ACTION_ADD_STICKER);
            } else {
                mActivity.addImage(mListImage.get(i), viewCoord);
                AnalyticsHelper.getInstance()
                        .send(mActivity, Constants.CATEGORY_ADD_FILE, Constants.ACTION_ADD_IMAGE);
            }
        }
    };

    private class AsyncTaskScanFile extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... value) {
            boolean subFolder = true;
            String folderPath = galleryState == GalleryState.IMAGE_SUBFOLDER_SD ?
                   mListFolderSd.get(value[0]) :  mListFolder.get(value[0]);
            if (folderPath.equals(mStoragePath) || folderPath.equals(mSdPath)){
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
        if (fileList == null) {
            return;
        }
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
        @Override
        protected Void doInBackground(Void... voids) {
            mListStiker = Utils.listFilesFromAssets(mActivity, STICKER_FOLDER);
            mListFirstImage.add(mListStiker.get(0));
            int begin = mHasSdCard ? 2 : 1;
            for (int i=begin; i<mListFolder.size(); i++) {
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

    private boolean isImageFolder(File fileDirectory, boolean includeSubDir) {
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
                    result = isImageFolder(file, true);
                }
            } else {
                if (matchFile(file)) {
                    if (galleryState == GalleryState.IMAGE_FOLDER) {
                        mListFirstImage.add(file.getAbsolutePath());
                    } else {
                        mListFirstImageSd.add(file.getAbsolutePath());
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

    private class ImageGalleryAdapter extends ArrayAdapter<String> {

        private ImageGalleryAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            switch (galleryState) {
                case IMAGE_FOLDER:
                    return getFolderLayout(position);
                case IMAGE_FOLDER_SD:
                    return getFolderLayoutSd(position);
                case IMAGE_SUBFOLDER_STICKER:
                    return getStickerLayout(position);
                case IMAGE_SUBFOLDER_SD:
                case IMAGE_SUBFOLDER:
                    return getImageLayout(position);
                default:
                    return getFolderLayout(position);
            }
        }

        private View getFolderLayout(int position){
            View convertView = LayoutInflater.from(mActivity).inflate(R.layout.folder_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            ImageView iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
            iconFolder.setVisibility(View.VISIBLE);
            int stickerPosition = mHasSdCard ? 1 : 0;
            if (mHasSdCard && position == 0) {
                textView.setText("Sd Card");
                imageView.setImageResource(R.drawable.ic_sd_card);
                iconFolder.setVisibility(View.INVISIBLE);
            } else if (position == stickerPosition) {
                textView.setText(STICKER_FOLDER_NAME);
                Uri uri = Uri.parse(ASSETS_PATH + mListFirstImage.get(stickerPosition));
                Glide.with(mActivity).load(uri).centerCrop().into(imageView);
            } else {
                String name = new File(mListFolder.get(position)).getName();
                textView.setText(name);
                Glide.with(mActivity).load(mListFirstImage.get(position)).centerCrop().into(imageView);
            }
            return convertView;
        }

        private View getFolderLayoutSd(int position){
            View convertView = LayoutInflater.from(mActivity).inflate(R.layout.folder_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            String folder = mListFolderSd.get(position);
            if (folder.equals(mSdPath)) {
                textView.setText("0");
            } else {
                String name = new File(folder).getName();
                textView.setText(name);
            }
            Glide.with(mActivity).load(mListFirstImageSd.get(position)).centerCrop().into(imageView);
            return convertView;
        }

        private View getStickerLayout(int position){
            View convertView = LayoutInflater.from(mActivity).inflate(R.layout.image_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            ImageView iconFolder = (ImageView) convertView.findViewById(R.id.icon_folder);
            iconFolder.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            Uri uri = Uri.parse(ASSETS_PATH + mListStiker.get(position));
            Glide.with(mActivity).load(uri).centerCrop().into(imageView);

            return convertView;
        }

        private View getImageLayout(int position){
            View convertView = LayoutInflater.from(mActivity).inflate(R.layout.image_gallery_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            String imagePath = mListImage.get(position);
            String name = new File(imagePath).getName();
            textView.setText(name);
            Glide.with(mActivity).load(imagePath).centerCrop().into(imageView);
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
