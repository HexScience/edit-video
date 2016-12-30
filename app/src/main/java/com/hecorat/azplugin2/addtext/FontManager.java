package com.hecorat.azplugin2.addtext;

import android.content.Context;

import com.hecorat.azplugin2.helper.Utils;

import java.util.ArrayList;

/**
 * Created by bkmsx on 12/22/2016.
 */

public class FontManager {

    public static final String FONT_FOLDER = "font";

    public static ArrayList<String> getFontPaths(Context context){
        ArrayList<String> listFontPath = new ArrayList<>();
        ArrayList<String> listFontAssets = Utils.listFilesFromAssets(context, FONT_FOLDER);
        for (String font : listFontAssets) {
            String fontName = font.replace("/", "_");
            String fontPath = Utils.getFontFolder() + "/" + fontName;
            Utils.copyFileFromAssets(context, font, fontPath);
            listFontPath.add(fontPath);
        }
        return listFontPath;
    }
}
