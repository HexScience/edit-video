package com.hecorat.azplugin2.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.main.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class RangeSeekBar extends AppCompatImageView {
	private MainActivity mActivity;
	private Paint paint = new Paint();
	private RectF rectThumb1, rectThumb2;
	private RectF rectGreyLeft, rectGreyRight;
	private Path arrowPath;
	private ArrayList<Bitmap> listBitmap;
	private Bitmap defaultBitmap;
	private OnSeekBarChangedListener onSeekBarChangedListener;
	private MediaMetadataRetriever retriever;
	private Rect rectBgr;

	private int leftLimit, rightLimit;
	public int top;
	public int height;
	public int widthSeekbar;
	private double leftFraction, rightFraction;
	private int left, right;
	private int thumbWidth;
	private int roundRadius;
	private int midY, midX;
	private int arrowHaftHeight, arrowOffset;
	private int selectedPosition = NO_ACTION;
	public int minValue, maxValue, minSelectedValue, maxSelectedValue, currentValue;
	private int durationVideo;
	private int bitmapWidth;
	private int textSize;

	private final static int THUMB_LEFT = 1, THUMB_RIGHT = 2, MID_BAR = 3, NO_ACTION = 0;
	public static final int BITMAP_NUMBER = 12;

	RangeSeekBar(Context context) {
		super(context);
	}

	public RangeSeekBar(MainActivity activity, OnSeekBarChangedListener listener, int width, int height, String videoPath) {
		super(activity);
		mActivity = activity;
		thumbWidth = Utils.dpToPixel(activity, 10);
		textSize = Utils.dpToPixel(mActivity, 14);
		top = 30;
		leftFraction = 0;
		rightFraction = 1;
		roundRadius = 4;
		arrowOffset = 6;

		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(textSize);
		int textWidth = (int)textPaint.measureText("00:00")+10 + thumbWidth;

		this.height = height;
		leftLimit = textWidth;
		rightLimit = width - leftLimit;
		this.widthSeekbar = rightLimit-leftLimit;
		rectBgr = new Rect(leftLimit, top, rightLimit, top+height);

		left = leftLimit;
		right = rightLimit;

		bitmapWidth = this.widthSeekbar /BITMAP_NUMBER;
		defaultBitmap = Utils.createDefaultBitmap();
		listBitmap = new ArrayList<>();

		retriever = new MediaMetadataRetriever();
		retriever.setDataSource(videoPath);
		durationVideo = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		minValue = 0; //miliS
		maxValue = durationVideo; //miliS
		arrowHaftHeight = height / 4;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, this.height + top);
		setLayoutParams(params);
		currentValue = minValue;
		updateSeekBar();
		setOnTouchListener(onTouchListener);
		onSeekBarChangedListener = listener;
		new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void setCurrentValue(int value) {
		currentValue = value;
		invalidate();
	}

	public void setRange(int min, int max) {
		minValue = min;
		maxValue = max;
	}

	OnTouchListener onTouchListener = new OnTouchListener() {
		int x, y, oldX, oldY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			x = (int) event.getX();
			y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				oldX = x;
				oldY = y;
				int epslonX = 120;
				int epslonY = 20;
				if (oldX > left - thumbWidth - epslonX && oldX < left + epslonX && oldY > top - epslonY
						&& oldY < top + height + epslonY) {
					selectedPosition = THUMB_LEFT;
					if (oldX > midX) {
						selectedPosition = THUMB_RIGHT;
					}
				} else if (oldX > right - epslonX && oldX < right + thumbWidth + epslonX && oldY > top - epslonY
						&& oldY < top + height + epslonY) {
					selectedPosition = THUMB_RIGHT;
					if (oldX < midX) {
						selectedPosition = THUMB_LEFT;
					}
				} else if (oldX > left + epslonX && oldX < right - epslonX && midY > top - epslonY
						&& midY < top + height + epslonY) {
					selectedPosition = MID_BAR;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int moveX = x - oldX;
				switch (selectedPosition) {
				case THUMB_LEFT:
					left = x;
					if (left < leftLimit) {
						left = leftLimit;
					}
					if (left > right) {
						left = right;
					}
					updateSeekBar();
					updateTime(true);
					break;
				case THUMB_RIGHT:
					right = x;
					if (right > rightLimit) {
						right = rightLimit;
					}
					if (right < left) {
						right = left;
					}
					updateSeekBar();
					updateTime(false);
					break;
				case MID_BAR:
					left += moveX;
					right += moveX;
					if (left < leftLimit) {
						right = leftLimit + right - left;
						left = leftLimit;
					}
					if (right > rightLimit) {
						left = rightLimit - (right - left);
						right = rightLimit;
					}
					updateSeekBar();
					updateTime(true);
					break;
				}
				oldX = x;
				oldY = y;
				break;
			case MotionEvent.ACTION_UP:
				selectedPosition = NO_ACTION;
				invalidate();
				break;
			}
			return true;
		}
	};

	public void updateTime(boolean seekMin) {
		minSelectedValue = (int)((double)(left - leftLimit) / (rightLimit - leftLimit) * (maxValue - minValue) + minValue);
		maxSelectedValue = (int)((double)(right - leftLimit) / (rightLimit - leftLimit) * (maxValue - minValue) + minValue);
		int seekValue = seekMin ? minSelectedValue : maxSelectedValue;
		setCurrentValue(seekValue);
		log("maxSelectedValue = " + maxSelectedValue);
		onSeekBarChangedListener.seekVideoTo(seekValue);
		onSeekBarChangedListener.updateSelectedTime(minSelectedValue, maxSelectedValue);
	}

	public void setSelectedValue(int min, int max) {
		left = (int)((double)(min - minValue) / (maxValue - minValue) * (rightLimit - leftLimit) + leftLimit);
		right = (int)((double)(max - minValue) / (maxValue - minValue) * (rightLimit - leftLimit) + leftLimit);
		updateSeekBar();
	}

	public void updateSeekBar() {
		leftFraction = (double) (left - leftLimit) / widthSeekbar;
		rightFraction = (double) (right - leftLimit) / widthSeekbar;
		minSelectedValue = (int) (leftFraction * (maxValue - minValue) + minValue);
		maxSelectedValue = (int) (rightFraction * (maxValue - minValue) + minValue);
		midY = top + height / 2;
		midX = (left + right) / 2;
		rectThumb1 = new RectF(left - thumbWidth, top, left, top + height);
		rectThumb2 = new RectF(right, top, right + thumbWidth, top + height);
		rectGreyLeft = new RectF(leftLimit, top, left, top + height);
		rectGreyRight = new RectF(right, top, rightLimit, top + height);
		arrowPath = new Path();
		arrowPath.moveTo(left - arrowOffset, midY - arrowHaftHeight);
		arrowPath.lineTo(left - thumbWidth + arrowOffset, midY);
		arrowPath.lineTo(left - arrowOffset, midY + arrowHaftHeight);
		arrowPath.moveTo(right + arrowOffset, midY - arrowHaftHeight);
		arrowPath.lineTo(right + thumbWidth - arrowOffset, midY);
		arrowPath.lineTo(right + arrowOffset, midY + arrowHaftHeight);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int bgrColor = ContextCompat.getColor(mActivity, R.color.range_seekbar_bgr);
		paint.setColor(bgrColor);
		canvas.drawRect(rectBgr, paint);

		for (int i = 0; i < listBitmap.size(); i++) {
			Bitmap bitmap = listBitmap.get(i);
			canvas.drawBitmap(bitmap, leftLimit + i * bitmapWidth, top, paint);
		}

		paint.setColor(ContextCompat.getColor(mActivity, R.color.rect_grey_edit_icon));
		canvas.drawRect(rectGreyLeft, paint);
		canvas.drawRect(rectGreyRight, paint);
		int thumbColor = ContextCompat.getColor(mActivity, R.color.thumb_seekbar_color);
		int thumbColorPress = ContextCompat.getColor(mActivity, R.color.orange);
		paint.setColor(thumbColor);
		paint.setStrokeWidth(4);
		canvas.drawLine(left - thumbWidth / 2, top + 2, right + thumbWidth / 2, top + 2, paint);
		canvas.drawLine(left - thumbWidth / 2, top + height - 2, right + thumbWidth / 2, top + height - 2, paint);
		paint.setColor(selectedPosition == THUMB_LEFT ? thumbColorPress : thumbColor);
		canvas.drawRoundRect(rectThumb1, roundRadius, roundRadius, paint);
		paint.setColor(selectedPosition == THUMB_RIGHT ? thumbColorPress : thumbColor);
		canvas.drawRoundRect(rectThumb2, roundRadius, roundRadius, paint);
		paint.setColor(Color.DKGRAY);
		canvas.drawPath(arrowPath, paint);

		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTextSize(textSize);
		int currentTime = currentValue / 1000;
		int durationTime = maxValue / 1000;
		String minLabel = String.format(Locale.getDefault(), "%02d:%02d", currentTime / 60, currentTime % 60);
		String maxLabel = String.format(Locale.getDefault(), "%02d:%02d", durationTime / 60, durationTime % 60);
		canvas.drawText(minLabel, 5, top + height / 2 + textSize / 2, paint);
		canvas.drawText(maxLabel, rightLimit + thumbWidth + 10, top + height / 2 + textSize / 2, paint);
	}

	private class AsyncTaskExtractFrame extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			int microDuration = durationVideo * 1000;
			int step = microDuration/BITMAP_NUMBER;
			int timeStamp = 0;
			int countBitmap = 0;
			int bitmapWidthCorrection;
			while (countBitmap < BITMAP_NUMBER) {
				Bitmap bitmap = null;
				int currentTimeStamp = timeStamp;
				while (bitmap==null && currentTimeStamp < Math.min(timeStamp+2900000, microDuration)) {
					bitmap = retriever.getFrameAtTime(timeStamp, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
					currentTimeStamp += 60000;
				}
				if (bitmap == null) {
					bitmap = defaultBitmap;
				}
				countBitmap++;

				if (countBitmap<BITMAP_NUMBER){
					bitmapWidthCorrection = bitmapWidth;
				} else {
					bitmapWidthCorrection = widthSeekbar-bitmapWidth*(BITMAP_NUMBER-1);
					if (bitmapWidthCorrection<1){
						bitmapWidthCorrection=1;
					}
				}
				Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidthCorrection,
						height, false);
				listBitmap.add(scaleBitmap);
				publishProgress();

				timeStamp += step;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			invalidate();
		}
	}

	interface OnSeekBarChangedListener {
		void seekVideoTo(int value);
		void updateSelectedTime(int min, int max);
	}

	private void log(String msg) {
		Log.e("Seekbar",msg);
	}
}
