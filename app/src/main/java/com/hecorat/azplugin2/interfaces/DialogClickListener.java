package com.hecorat.azplugin2.interfaces;

/**
 * Created by bkmsx on 1/3/2017.
 */

public interface DialogClickListener {
    int ASK_DONATE = 0;
    int DELETE_VIDEO = 1;
    int DELETE_EXTRA = 2;
    int DELETE_AUDIO = 3;
    int DELETE_PROJECT = 4;

    void onPositiveClick(int dialogId);
    void onNegativeClick(int dialogId);
}
