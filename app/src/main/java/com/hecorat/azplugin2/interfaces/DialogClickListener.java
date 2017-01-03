package com.hecorat.azplugin2.interfaces;

/**
 * Created by bkmsx on 1/3/2017.
 */

public interface DialogClickListener {
    int ASK_DONATE = 0;

    void onPositiveClick(int dialogId);
    void onNegativeClick(int dialogId);
}
