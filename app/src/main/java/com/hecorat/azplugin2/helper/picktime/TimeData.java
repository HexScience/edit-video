package com.hecorat.azplugin2.helper.picktime;

import android.text.format.Time;

public class TimeData extends Time {
	public String format = "";
	public int milisecs = 0;

	public TimeData(){}

	public TimeData(int timeMs) {
		setMsec(timeMs);
	}

	public void setMsec(int msecs) {
		milisecs = msecs;
		second = msecs / 1000;
		minute = second / 60;
		hour = minute / 60;
		minute = minute % 60;
		second = second % 60;
		setFormat();
	}

	public void setTime(int hours, int minutes, int seconds) {
		hour = hours;
		minute = minutes;
		second = seconds;
		milisecs = (hour * 3600 + minute * 60 + second) * 1000;
		setFormat();
	}

	public void setFormat() {
		String duration = "";
		if (hour > 0) {
			if (hour < 10) {
				duration += "0" + hour + ":";
			} else {
				duration += hour + ":";
			}
		}
		if (minute < 10) {
			duration += "0" + minute + ":";
		} else {
			duration += minute + ":";
		}

		if (second < 10) {
			duration += "0" + second;
		} else {
			duration += second;
		}
		format = duration;
	}

}