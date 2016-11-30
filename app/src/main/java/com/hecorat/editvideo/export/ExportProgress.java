package com.hecorat.editvideo.export;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.MainActivity;

public class ExportProgress extends AsyncTask<Void, Integer, Void> {
	private ProgressDialog progressDialog;
	private MainActivity mActivity;
	private int mVideoDuration; //seconds

	public ExportProgress(MainActivity activity, int durationSecond) {
		mActivity = activity;
		progressDialog = new ProgressDialog(activity);
		setTitleDialog(mActivity.getResources().getString(R.string.export_progress_msg));
		mVideoDuration = durationSecond;
	}

	public void setTitleDialog(String title){
		progressDialog.setTitle(title);
	}

	@Override
	protected void onPreExecute() {
		mActivity.mFinishExport = false;
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		int duration = mVideoDuration;
		int total = 0;

		while (total < duration) {
			String line = FFmpeg.getInstance(mActivity).getLineLog();
			if (line != null) {
				int start = line.indexOf("time=");
				if (start >= 0) {
					String hours = line.substring(start + 5, start + 7);
					String minutes = line.substring(start + 8, start + 10);
					String seconds = line.substring(start + 11, start + 13);
					total = Integer.parseInt(hours) * 3600 + Integer.parseInt(minutes) * 60
							+ Integer.parseInt(seconds);
					publishProgress(total * 100 / duration);
					Log.d("Log Convert AAC", total + "");
				}
			}
			if (mActivity.mFinishExport){
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progressDialog.setProgress(values[0]);
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		progressDialog.dismiss();
		mActivity.hideStatusBar();
		super.onPostExecute(result);
	}
}
