package com.hecorat.azplugin2.helper.picktime;

class TimeData {
	public String format = "";
	int milisecs = 0;
	int second, minute, hour;

	public TimeData(){}

	TimeData(int timeMs) {
		setMsec(timeMs);
	}

	void setMsec(int msecs) {
		milisecs = msecs;
		second = msecs / 1000;
		minute = second / 60;
		hour = minute / 60;
		minute = minute % 60;
		second = second % 60;
		setFormat();
	}

	void setTime(int hours, int minutes, int seconds) {
		hour = hours;
		minute = minutes;
		second = seconds;
		milisecs = (hour * 3600 + minute * 60 + second) * 1000;
		setFormat();
	}

	private void setFormat() {
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