package com.hecorat.azplugin2.main;

/**
 * Created by bkmsx on 01/11/2016.
 */
public interface Constants {
    /**
     * Constant values
     */

    int DB_VERSION = 2;
    int SCALE_VALUE = 20;
    int MARGIN_LEFT_TIME_LINE = 100;
    int DEFAULT_DURATION = 10000;
    int BORDER_WIDTH = 5;
    int MAX_VOLUME = 200;

    // for Az Recorder
    String VIDEO_FILE_PATH = "filePath";
    String USE_SD_CARD = "use sd card";
    String DIRECTORY = "directory";
    String ACTION_COPY_FILE_TO_SDCARD = "copy file to sd";
    String ACTION_COPY_COMPLETED = "copy completed";
    String COMMAND_OPEN_GALLERY = "command open gallery notification";
    String COMMAND = "command";
    String IS_VIP = "is vip";
    /**
     * Categories and Actions for Google Analytics
     */
    String CATEGORY_PROJECT = "PROJECT";
    String ACTION_NEW_PROJECT = "New project";
    String ACTION_OPEN_PROJECT = "Open project";
    String ACTION_DELETE_PROJECT = "Delete project";
    String ACTION_RENAME_PROJECT = "Rename project";

    String CATEGORY_VIDEO = "EDIT VIDEO";
    String ACTION_TRIM_VIDEO = "Trim video";
    String ACTION_DELETE_VIDEO = "Delete video";
    String ACTION_CHANGE_VOLUME_VIDEO = "Change volume video";
    String ACTION_PICK_TIME_VIDEO = "Pick time video";
    String ACTION_DRAG_VIDEO = "Drag video";

    String CATEGORY_TEXT = "EDIT TEXT";
    String ACTION_DELETE_TEXT = "Delete text";
    String ACTION_CHANGE_TEXT = "Change text";
    String ACTION_CHANGE_TEXT_COLOR = "Change text color";
    String ACTION_CHANGE_TEXT_BACKGROUND = "Change text background";
    String ACTION_CHANGE_HEX_COLOR = "Change hex color";
    String ACTION_CHANGE_TEXT_FONT = "Change text font";
    String ACTION_ROTATE_TEXT = "Rotate text";
    String ACTION_DRAG_TEXT = "Drag text";

    String CATEGORY_IMAGE = "EDIT IMAGE";
    String ACTION_DELETE_IMAGE = "Delete image";
    String ACTION_ROTATE_IMAGE = "Rotate image";
    String ACTION_DRAG_IMAGE = "Drag image";

    String CATEGORY_AUDIO = "EDIT AUDIO";
    String ACTION_DELETE_AUDIO = "Delete audio";
    String ACTION_CHANGE_VOLUME_AUDIO = "Change volume audio";
    String ACTION_DRAG_AUDIO = "Drag audio";

    String CATEGORY_ADD_FILE = "ADD FILE";
    String ACTION_ADD_VIDEO = "Add video";
    String ACTION_ADD_IMAGE = "Add image";
    String ACTION_ADD_STICKER = "Add sticker";
    String ACTION_ADD_TEXT = "Add text";
    String ACTION_ADD_AUDIO = "Add audio";

    String CATEGORY_EXPORT = "EXPORT VIDEO";
    String ACTION_CLICK_EXPORT = "Click exportVideo";
    String ACTION_EXPORT_SUCCESSFUL = "Export successful";
    String ACTION_CANCEL_EXPORT = "Cancel exportVideo";
    String ACTION_WATCH_VIDEO = "Watch video";
    String ACTION_SHARE_VIDEO = "Share video";

    String CATEGORY_CLICK_BACK = "CLICK BACK";
    String ACTION_CLICK_BUTTON_BACK = "Click button back";
    String ACTION_CLICK_NAVIGATION_BACK = "Click navigation back";

    String CATEGORY_DONATE = "DONATE";
    String ACTION_REMOVE_WATERMARK = "Remove watermark";
    String ACTION_REMOVE_SUCCESSFUL = "Remove successful";
    String ACTION_REMOVE_FAILED = "Remove failed";

    int DIMENSION_IMAGE_DURATION = 1;
    int DIMENSION_TEXT_DURATION = 2;
    int DIMENSION_AUDIO_DURATION = 3;
    int DIMENSION_OUTPUT_QUALITY = 4;
    int DIMENSION_OUTPUT_DURATION = 5;

    //For Az Screen Recorder Analytic
    String EVENT_ACTION_DIALOG_FROM_NEW_TRIM = "Dialog from new trimming";
    String EVENT_ACTION_DIALOG_FROM_NEW_GIF = "Dialog from new GIF";
    String EVENT_ACTION_DIALOG_FROM_WATERMARK = "Dialog from watermark";

    /**
     * Name folder and keys
     */
    String OUTPUT_FOLDER = "AZ_Video_Editor";
    String TEMP_FOLDER = ".temp";
    String RESOURCE_FOLDER = ".resource";
    String FONT_FOLDER = ".fonts";
    String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr845N+56OFsJAZV60" +
            "BVWsf1Rh2f6Y/Pqv2J6Ss+b11k72OXtZmWhrwZ2y00jPWDYCMXNhcl8TIE4rSln0asN0sux6Tr" +
            "QvwQwDBudC6ovdg7h1a+yhkfJWy4qu9K21qzKe/O81mxX64kbjUQsjKgLvKj18rKL+7yybFr+w" +
            "sY2LV08jiiOehiZYVGQFYf4rzIClt9GYZHGio0m7jm+4q6GMTL7S6ReBbsB3tDDKYIm7eRAoBCH" +
            "skSegPipPwnsjAlc0eEqhBX2YVKGB7lq0py7bi2AmiWPC5s3fwYi1nOgfRu7itbFaZVJrZAhYcm" +
            "eyxPFcBfbSDimKXFzfnEh3TWidQIDAQAB";
    String SKU_DONATE = "donate";

    /**
     * Request code
     */
    int REQUEST_CODE_PURCHASE = 1;
    String ACTION_IABTABLE = "com.hecorat.IABTABLE";
    String SD_CARD = "Sd Card";
    String SLASH = " / ";
    String STORAGE_NAME = "0";
}
