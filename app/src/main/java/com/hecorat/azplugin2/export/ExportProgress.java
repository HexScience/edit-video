package com.hecorat.azplugin2.export;

import android.os.AsyncTask;

import com.hecorat.azplugin2.main.MainActivity;

public class ExportProgress extends AsyncTask<Void, Integer, Void> {
	private MainActivity mActivity;
	private ExportFragment mExportFragment;

	private int mVideoDuration; //seconds

	public ExportProgress(MainActivity activity, int durationSecond) {
		mActivity = activity;
		mExportFragment = mActivity.mExportFragment;
		mVideoDuration = durationSecond;
	}

	@Override
	protected void onPreExecute() {
		mActivity.mFinishExport = false;
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
		mExportFragment.setExportProgress(values[0]);
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		mActivity.hideStatusBar();
		mExportFragment.onExportCompleted();
		super.onPostExecute(result);
	}
}
