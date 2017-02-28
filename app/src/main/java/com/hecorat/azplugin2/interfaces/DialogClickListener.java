package com.hecorat.azplugin2.interfaces;

/**
 * Created by bkmsx on 1/3/2017.
 */

public interface DialogClickListener {
    int ASK_DONATE = 0;
    int DELETE_VIDEO = 1;
    int DELETE_IMAGE = 2;
    int DELETE_TEXT = 3;
    int DELETE_AUDIO = 4;
    int DELETE_PROJECT = 5;
    int OVERWRITE_FILE = 6;
    int WARNING_DURATION_GIF = 7;
    int SAVE_PROJECT = 8;

    void onPositiveClick(int dialogId, String detail);
    void onNegativeClick(int dialogId);
}
