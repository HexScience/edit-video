package com.hecorat.azplugin2.helper.picktime;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.hecorat.azplugin2.R;

public class DialogTimePicker extends DialogFragment {
	public TimeData timeData, timeDataDuration;
	public NumberPicker pickHours, pickMinutes, pickSeconds;
	public Context mActivity;
	private OnDialogTimePickerListener mCallback;

	public static DialogTimePicker newInstance(Context activity, TimeData timeData,
                                               TimeData timeDataDuration, OnDialogTimePickerListener listener) {
		DialogTimePicker f = new DialogTimePicker();
		f.timeData = timeData;
		f.timeDataDuration = timeDataDuration;
		f.mActivity = activity;
		f.mCallback = listener;
		return f;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_pick_time_title);
		builder.setIcon(R.drawable.ic_time_picker);
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_pick_time, null);
		pickHours = (NumberPicker) view.findViewById(R.id.numpick_hours);
		pickMinutes = (NumberPicker) view.findViewById(R.id.numpick_minutes);
		pickSeconds = (NumberPicker) view.findViewById(R.id.numpick_seconds);
		pickHours.setMinValue(0);
		pickHours.setMaxValue(timeDataDuration.hour);
		pickHours.setValue(timeData.hour);
		pickHours.setOnValueChangedListener(onHourChangedListener);

		if (pickHours.getValue() < timeDataDuration.hour) {
			pickMinutes.setMaxValue(59);
		} else {
			pickMinutes.setMaxValue(timeDataDuration.minute);
		}
		pickMinutes.setMinValue(0);
		pickMinutes.setValue(timeData.minute);
		pickMinutes.setOnValueChangedListener(onMinuteChangedListener);

		if (pickMinutes.getValue() < timeDataDuration.minute) {
			pickSeconds.setMaxValue(59);
		} else {
			pickSeconds.setMaxValue(timeDataDuration.second);
		}
		pickSeconds.setMinValue(0);
		pickSeconds.setValue(timeData.second);

		builder.setView(view);
		builder.setNegativeButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timeData.setTime(pickHours.getValue(), pickMinutes.getValue(), pickSeconds.getValue());
				mCallback.onTimePickerChanged();
				dialog.dismiss();
			}
		});

		builder.setNeutralButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	OnValueChangeListener onMinuteChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			setMaxPickers();
		}
	};

	OnValueChangeListener onHourChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			setMaxPickers();
		}
	};

	public void setMaxPickers() {
		if (pickHours.getValue() < timeDataDuration.hour) {
			pickMinutes.setMaxValue(59);
			pickSeconds.setMaxValue(59);
		} else {
			pickMinutes.setMaxValue(timeDataDuration.minute);
			if (pickMinutes.getValue() == timeDataDuration.minute) {
				pickSeconds.setMaxValue(timeDataDuration.second);
			} else {
				pickSeconds.setMaxValue(59);
			}
		}
	}
	
	interface OnDialogTimePickerListener {
		void onTimePickerChanged();
	}
}
